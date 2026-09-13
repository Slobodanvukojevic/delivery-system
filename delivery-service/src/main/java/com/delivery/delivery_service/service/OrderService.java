package com.delivery.delivery_service.service;

import com.delivery.delivery_service.dto.AssignLockerResponse;
import com.delivery.delivery_service.dto.CreateOrderRequest;
import com.delivery.delivery_service.dto.OrderResponse;
import com.delivery.delivery_service.entity.*;
import com.delivery.delivery_service.exception.*;
import com.delivery.delivery_service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final double BASE_PRICE = 200.0;
    private static final double PRICE_PER_KG = 50.0;
    private static final int RETURN_LOCKER_DAYS = 3;
    private static final int RETURN_BRANCH_DAYS = 10;

    private static final Set<OrderStatus> FINAL_STATUSES = Set.of(
            OrderStatus.DELIVERED,
            OrderStatus.PICKED_UP_BY_CUSTOMER,
            OrderStatus.RETURN_TO_SENDER,
            OrderStatus.CANCELLED
    );

    private final DeliveryOrderRepository orderRepository;
    private final ParcelLockerRepository lockerRepository;
    private final LockerCompartmentRepository compartmentRepository;
    private final Random random = new Random();

    public OrderService(DeliveryOrderRepository orderRepository,
                        ParcelLockerRepository lockerRepository,
                        LockerCompartmentRepository compartmentRepository,
                        BranchRepository branchRepository) {
        this.orderRepository = orderRepository;
        this.lockerRepository = lockerRepository;
        this.compartmentRepository = compartmentRepository;
    }



    public OrderResponse createOrder(CreateOrderRequest request) {
        double price = BASE_PRICE + (PRICE_PER_KG * request.getWeight());
        String pickupCode = generatePickupCode();

        DeliveryOrder order = DeliveryOrder.builder()
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderPhone(request.getSenderPhone())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .weight(request.getWeight())
                .pickupAddress(request.getPickupAddress())
                .dropoffAddress(request.getDropoffAddress())
                .deliveryMethod(request.getDeliveryMethod())
                .selectedBranchId(request.getSelectedBranchId())
                .selectedLockerId(request.getSelectedLockerId())
                .status(OrderStatus.PENDING)
                .pickupCode(pickupCode)
                .price(price)
                .build();

        DeliveryOrder saved = orderRepository.save(order);
        log.info("Kreirana porudzbina ID {} sa pickupCode {}", saved.getId(), pickupCode);
        return mapToResponse(saved);
    }

    public OrderResponse getOrderById(Long id) {
        DeliveryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // PRIJEM U POSLOVNICI

    @Transactional
    public OrderResponse acceptOrder(Long orderId, Double weightConfirmed) {
        DeliveryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Porudzbina mora biti u statusu PENDING da bi bila prihvacena. Trenutni status: " + order.getStatus()
            );
        }

        if (weightConfirmed != null && weightConfirmed > 0) {
            order.setWeight(weightConfirmed);
            double newPrice = BASE_PRICE + (PRICE_PER_KG * weightConfirmed);
            order.setPrice(newPrice);
        }

        order.setStatus(OrderStatus.ACCEPTED_AT_BRANCH);
        DeliveryOrder saved = orderRepository.save(order);

        log.info("Porudzbina {} prihvacena u poslovnici", orderId);
        return mapToResponse(saved);
    }

    // DODJELA PAKETOMATA

    @Transactional
    public AssignLockerResponse assignLocker(Long orderId) {
        DeliveryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getDeliveryMethod() != DeliveryMethod.LOCKER_PICKUP) {
            throw new InvalidOrderStatusException(
                    "Porudzbina nije tipa LOCKER_PICKUP. Nacin dostave: " + order.getDeliveryMethod()
            );
        }

        if (order.getStatus() != OrderStatus.ACCEPTED_AT_BRANCH && order.getStatus() != OrderStatus.IN_TRANSIT) {
            throw new InvalidOrderStatusException(
                    "Porudzbina mora biti u statusu ACCEPTED_AT_BRANCH ili IN_TRANSIT. Trenutni status: " + order.getStatus()
            );
        }

        Long requestedLockerId = order.getSelectedLockerId();
        ParcelLocker selectedLocker = null;
        LockerCompartment compartment = null;

        if (requestedLockerId != null) {
            selectedLocker = lockerRepository.findById(requestedLockerId)
                    .orElseThrow(() -> new LockerNotFoundException(requestedLockerId));

            compartment = findFreeCompartment(requestedLockerId);

            if (compartment == null) {
                log.warn("Paketomat {} je pun. Trazim alternativu.", requestedLockerId);
                selectedLocker = null;
            }
        }

        if (selectedLocker == null) {
            selectedLocker = findNearestAvailableLocker(
                    order.getDropoffAddress(),
                    requestedLockerId
            );
            if (selectedLocker == null) {
                throw new LockerFullException("Nema slobodnih paketomata u blizini");
            }
            compartment = findFreeCompartment(selectedLocker.getId());
            if (compartment == null) {
                throw new LockerFullException("Odabrani paketomat je pun");
            }
        }

        compartment.setIsOccupied(true);
        compartment.setOrderId(orderId);
        compartment.setReservedAt(LocalDateTime.now());
        compartmentRepository.save(compartment);

        order.setSelectedLockerId(selectedLocker.getId());
        order.setStatus(OrderStatus.PLACED_IN_LOCKER);
        if (order.getPickupCode() == null) {
            order.setPickupCode(generatePickupCode());
        }
        orderRepository.save(order);

        log.info("Porudzbina {} smestena u paketomat {} sanducic {}. PickupCode: {}",
                orderId, selectedLocker.getId(), compartment.getCompartmentNumber(), order.getPickupCode());

        return AssignLockerResponse.builder()
                .orderId(orderId)
                .lockerId(selectedLocker.getId())
                .lockerName(selectedLocker.getLocationName())
                .compartmentNumber(compartment.getCompartmentNumber())
                .pickupCode(order.getPickupCode())
                .status(order.getStatus().name())
                .message("Paket smesten u paketomat " + selectedLocker.getLocationName())
                .build();
    }

    private LockerCompartment findFreeCompartment(Long lockerId) {
        List<LockerCompartment> free = compartmentRepository.findByLockerIdAndIsOccupiedFalse(lockerId);
        return free.isEmpty() ? null : free.get(0);
    }

    private ParcelLocker findNearestAvailableLocker(String dropoffAddress, Long excludeLockerId) {
        List<ParcelLocker> lockers = lockerRepository.findAll();
        for (ParcelLocker locker : lockers) {
            if (locker.getId().equals(excludeLockerId)) {
                continue;
            }
            if (locker.getActive() != null && locker.getActive()) {
                if (findFreeCompartment(locker.getId()) != null) {
                    return locker;
                }
            }
        }
        return null;
    }

    // PREDAJA U POSLOVNICI

    @Transactional
    public OrderResponse pickupFromBranch(String pickupCode) {
        DeliveryOrder order = orderRepository.findByPickupCode(pickupCode)
                .orElseThrow(() -> new InvalidPickupCodeException(
                        "Porudzbina sa pickupCode " + pickupCode + " nije pronadjena"
                ));

        if (order.getStatus() == OrderStatus.PICKED_UP_BY_CUSTOMER) {
            throw new InvalidOrderStatusException("Porudzbina je vec preuzeta");
        }

        if (order.getStatus() != OrderStatus.READY_FOR_BRANCH_PICKUP
                && order.getStatus() != OrderStatus.PLACED_IN_LOCKER) {
            throw new InvalidOrderStatusException(
                    "Porudzbina nije spremna za preuzimanje. Trenutni status: " + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.PICKED_UP_BY_CUSTOMER);
        order.setPickupCode(null); // Brisemo kod da ne moze ponovo
        order.setDeliveredAt(LocalDateTime.now());
        DeliveryOrder saved = orderRepository.save(order);

        log.info("Porudzbina {} preuzeta u poslovnici", order.getId());
        return mapToResponse(saved);
    }

    // OTVARANJE PAKETOMATA

    @Transactional
    public OrderResponse openLocker(Long orderId, String pickupCode) {
        DeliveryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PLACED_IN_LOCKER) {
            throw new InvalidOrderStatusException(
                    "Porudzbina nije u paketomatu. Trenutni status: " + order.getStatus()
            );
        }

        if (pickupCode == null || !pickupCode.equals(order.getPickupCode())) {
            throw new InvalidPickupCodeException("Pogresan pickup code");
        }

        compartmentRepository.findByOrderId(orderId).ifPresent(compartment -> {
            compartment.setIsOccupied(false);
            compartment.setOrderId(null);
            compartment.setReservedAt(null);
            compartmentRepository.save(compartment);
        });

        order.setStatus(OrderStatus.PICKED_UP_BY_CUSTOMER);
        order.setPickupCode(null);
        order.setDeliveredAt(LocalDateTime.now());
        DeliveryOrder saved = orderRepository.save(order);

        log.info("Porudzbina {} preuzeta iz paketomata", orderId);
        return mapToResponse(saved);
    }

    // PROMJENA STATUSA (KURIR)

    @Transactional
    public OrderResponse updateStatus(Long orderId, String newStatusStr) {
        DeliveryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Nepoznat status: " + newStatusStr);
        }

        if (FINAL_STATUSES.contains(order.getStatus())) {
            throw new InvalidOrderStatusException(
                    "Porudzbina je u finalnom statusu " + order.getStatus() + " i ne moze se menjati"
            );
        }

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        DeliveryOrder saved = orderRepository.save(order);

        log.info("Porudzbina {} promenila status na {}", orderId, newStatus);
        return mapToResponse(saved);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.ACCEPTED_AT_BRANCH || next == OrderStatus.CANCELLED;
            case ACCEPTED_AT_BRANCH -> next == OrderStatus.IN_SORTING
                    || next == OrderStatus.IN_TRANSIT
                    || next == OrderStatus.RETURN_TO_SENDER
                    || next == OrderStatus.PLACED_IN_LOCKER
                    || next == OrderStatus.READY_FOR_BRANCH_PICKUP;
            case IN_SORTING -> next == OrderStatus.IN_TRANSIT;
            case IN_TRANSIT -> next == OrderStatus.PLACED_IN_LOCKER
                    || next == OrderStatus.READY_FOR_BRANCH_PICKUP
                    || next == OrderStatus.OUT_FOR_DELIVERY;
            case PLACED_IN_LOCKER -> next == OrderStatus.PICKED_UP_BY_CUSTOMER
                    || next == OrderStatus.RETURN_TO_SENDER;
            case READY_FOR_BRANCH_PICKUP -> next == OrderStatus.PICKED_UP_BY_CUSTOMER
                    || next == OrderStatus.RETURN_TO_SENDER;
            case OUT_FOR_DELIVERY -> next == OrderStatus.DELIVERED
                    || next == OrderStatus.RETURN_TO_SENDER;
            default -> false;
        };

        if (!valid) {
            throw new InvalidOrderStatusException(
                    "Nije dozvoljen prelaz iz " + current + " u " + next
            );
        }
    }

    // AUTOMATSKI POVRATAK (scheduler)

    @Transactional
    public int returnExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        LocalDateTime lockerCutoff = now.minusDays(RETURN_LOCKER_DAYS);
        List<DeliveryOrder> lockerOrders = orderRepository.findByStatus(OrderStatus.PLACED_IN_LOCKER);
        for (DeliveryOrder order : lockerOrders) {
            if (order.getUpdatedAt() != null && order.getUpdatedAt().isBefore(lockerCutoff)) {
                releaseCompartment(order.getId());
                order.setStatus(OrderStatus.RETURN_TO_SENDER);
                order.setPickupCode(null);
                orderRepository.save(order);
                count++;
                log.info("Porudzbina {} vracena posiljaocu (istekao rok u paketomatu)", order.getId());
            }
        }

        LocalDateTime branchCutoff = now.minusDays(RETURN_BRANCH_DAYS);
        List<DeliveryOrder> branchOrders = orderRepository.findByStatus(OrderStatus.READY_FOR_BRANCH_PICKUP);
        for (DeliveryOrder order : branchOrders) {
            if (order.getUpdatedAt() != null && order.getUpdatedAt().isBefore(branchCutoff)) {
                order.setStatus(OrderStatus.RETURN_TO_SENDER);
                order.setPickupCode(null);
                orderRepository.save(order);
                count++;
                log.info("Porudzbina {} vracena posiljaocu (istekao rok u poslovnici)", order.getId());
            }
        }

        return count;
    }

    private void releaseCompartment(Long orderId) {
        compartmentRepository.findByOrderId(orderId).ifPresent(compartment -> {
            compartment.setIsOccupied(false);
            compartment.setOrderId(null);
            compartment.setReservedAt(null);
            compartmentRepository.save(compartment);
        });
    }


    private String generatePickupCode() {
        return String.valueOf(1000 + random.nextInt(9000));
    }

    private OrderResponse mapToResponse(DeliveryOrder order) {
        return OrderResponse.builder()
                .id(order.getId())
                .senderId(order.getSenderId())
                .customerId(order.getCustomerId())
                .senderName(order.getSenderName())
                .senderPhone(order.getSenderPhone())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .weight(order.getWeight())
                .pickupAddress(order.getPickupAddress())
                .dropoffAddress(order.getDropoffAddress())
                .status(order.getStatus())
                .deliveryMethod(order.getDeliveryMethod())
                .selectedBranchId(order.getSelectedBranchId())
                .selectedLockerId(order.getSelectedLockerId())
                .pickupCode(order.getPickupCode())
                .assignedCourierId(order.getAssignedCourierId())
                .price(order.getPrice())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .deliveredAt(order.getDeliveredAt())
                .build();
    }
}