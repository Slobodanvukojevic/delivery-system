package com.delivery.delivery_service.service;

import com.delivery.delivery_service.dto.CreateOrderRequest;
import com.delivery.delivery_service.dto.OrderResponse;
import com.delivery.delivery_service.entity.DeliveryOrder;
import com.delivery.delivery_service.entity.OrderStatus;
import com.delivery.delivery_service.repository.DeliveryOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final double BASE_PRICE = 200.0;
    private static final double PRICE_PER_KG = 50.0;

    private final DeliveryOrderRepository orderRepository;
    private final Random random = new Random();

    public OrderService(DeliveryOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
        return mapToResponse(saved);
    }

    public OrderResponse getOrderById(Long id) {
        DeliveryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Porudzbina sa ID " + id + " nije pronadjena"));
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