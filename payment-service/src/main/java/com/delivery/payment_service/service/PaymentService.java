package com.delivery.payment_service.service;

import com.delivery.payment_service.dto.*;
import com.delivery.payment_service.entity.*;
import com.delivery.payment_service.exception.*;
import com.delivery.payment_service.feign.DeliveryClient;
import com.delivery.payment_service.feign.StatusUpdateRequest;
import com.delivery.payment_service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentTransactionRepository transactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final DeliveryClient deliveryClient;
    private final Random random = new Random();

    public PaymentService(PaymentTransactionRepository transactionRepository,
                          InvoiceRepository invoiceRepository,
                          PromoCodeRepository promoCodeRepository,
                          DeliveryClient deliveryClient) {
        this.transactionRepository = transactionRepository;
        this.invoiceRepository = invoiceRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.deliveryClient = deliveryClient;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        BigDecimal originalAmount = request.getAmount();
        BigDecimal discount = BigDecimal.ZERO;
        PromoCode appliedPromo = null;

        if (request.getPromoCode() != null && !request.getPromoCode().isBlank()) {
            appliedPromo = applyPromoCode(request.getPromoCode());
            discount = originalAmount
                    .multiply(BigDecimal.valueOf(appliedPromo.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal finalAmount = originalAmount.subtract(discount);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(originalAmount)
                .discountApplied(discount)
                .currency(request.getCurrency() != null ? request.getCurrency() : "RSD")
                .status(PaymentStatus.CREATED)
                .promoCodeId(appliedPromo != null ? appliedPromo.getId() : null)
                .build();

        PaymentTransaction saved = transactionRepository.save(transaction);

        try {
            saved.setStatus(PaymentStatus.COMPLETED);
            saved = transactionRepository.save(saved);

            String invoiceNumber = generateInvoiceNumber();
            Invoice invoice = Invoice.builder()
                    .transactionId(saved.getId())
                    .invoiceNumber(invoiceNumber)
                    .pdfUrl("/api/payments/invoice/download/" + invoiceNumber)
                    .issuedDate(LocalDate.now())
                    .build();
            invoiceRepository.save(invoice);

            if (appliedPromo != null) {
                appliedPromo.setUsedCount(appliedPromo.getUsedCount() + 1);
                promoCodeRepository.save(appliedPromo);
            }

            try {
                deliveryClient.updateOrderStatus(
                        request.getOrderId(),
                        new StatusUpdateRequest("ACCEPTED_AT_BRANCH")
                );
                log.info("Delivery Service azuriran za order {}", request.getOrderId());
            } catch (Exception e) {
                log.warn("Greska pri pozivu Delivery Service-a: {}", e.getMessage());
            }

            log.info("Placanje uspesno. Transakcija: {}, iznos: {} {}", saved.getId(), finalAmount, saved.getCurrency());

            return PaymentResponse.builder()
                    .transactionId(saved.getId())
                    .orderId(saved.getOrderId())
                    .originalAmount(originalAmount)
                    .discountApplied(discount)
                    .finalAmount(finalAmount)
                    .currency(saved.getCurrency())
                    .status(saved.getStatus())
                    .invoiceNumber(invoiceNumber)
                    .timestamp(saved.getCreatedAt())
                    .message("Placanje uspesno izvrseno")
                    .build();

        } catch (Exception e) {
            saved.setStatus(PaymentStatus.FAILED);
            transactionRepository.save(saved);
            throw new PaymentFailedException("Placanje nije uspelo: " + e.getMessage());
        }
    }

    private PromoCode applyPromoCode(String code) {
        PromoCode promo = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new InvalidPromoCodeException("Promo kod " + code + " ne postoji"));

        if (!promo.getActive()) {
            throw new InvalidPromoCodeException("Promo kod " + code + " nije aktivan");
        }

        if (promo.getExpiryDate().isBefore(LocalDate.now())) {
            throw new InvalidPromoCodeException("Promo kod " + code + " je istekao");
        }

        if (promo.getUsedCount() >= promo.getUsageLimit()) {
            throw new PromoCodeExhaustedException("Promo kod " + code + " je dostigao limit koriscenja");
        }

        return promo;
    }

    public InvoiceResponse getInvoiceByOrderId(Long orderId) {
        List<PaymentTransaction> transactions = transactionRepository.findByOrderId(orderId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(orderId);
        }
        PaymentTransaction transaction = transactions.get(transactions.size() - 1);

        Invoice invoice = invoiceRepository.findByTransactionId(transaction.getId())
                .orElseThrow(() -> new TransactionNotFoundException(transaction.getId()));

        return InvoiceResponse.builder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .issuedDate(invoice.getIssuedDate())
                .totalAmount(transaction.getAmount().subtract(transaction.getDiscountApplied()))
                .pdfUrl(invoice.getPdfUrl())
                .build();
    }

    public List<PaymentResponse> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse refundPayment(Long transactionId) {
        PaymentTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        if (transaction.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentFailedException("Samo kompletirane transakcije mogu biti refundirane");
        }

        if (transaction.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new PaymentFailedException("Refundacija je moguca samo u prvih 24h");
        }

        transaction.setStatus(PaymentStatus.REFUNDED);
        transactionRepository.save(transaction);

        log.info("Transakcija {} refundirana", transactionId);
        return mapToResponse(transaction);
    }

    public PromoCodeResponse createPromoCode(CreatePromoCodeRequest request) {
        if (promoCodeRepository.existsByCode(request.getCode())) {
            throw new InvalidPromoCodeException("Promo kod " + request.getCode() + " vec postoji");
        }

        PromoCode promo = PromoCode.builder()
                .code(request.getCode().toUpperCase())
                .discountPercent(request.getDiscountPercent())
                .expiryDate(request.getExpiryDate())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .active(true)
                .build();

        PromoCode saved = promoCodeRepository.save(promo);
        log.info("Kreiran promo kod: {}", saved.getCode());
        return mapToPromoResponse(saved);
    }

    public List<PromoCodeResponse> getAllPromoCodes() {
        return promoCodeRepository.findAll().stream()
                .map(this::mapToPromoResponse)
                .collect(Collectors.toList());
    }

    private String generateInvoiceNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        int number = 1000 + random.nextInt(9000);
        return "INV-" + year + "-" + number;
    }

    private PaymentResponse mapToResponse(PaymentTransaction tx) {
        return PaymentResponse.builder()
                .transactionId(tx.getId())
                .orderId(tx.getOrderId())
                .originalAmount(tx.getAmount())
                .discountApplied(tx.getDiscountApplied())
                .finalAmount(tx.getAmount().subtract(
                        tx.getDiscountApplied() != null ? tx.getDiscountApplied() : BigDecimal.ZERO))
                .currency(tx.getCurrency())
                .status(tx.getStatus())
                .timestamp(tx.getCreatedAt())
                .build();
    }

    private PromoCodeResponse mapToPromoResponse(PromoCode promo) {
        return PromoCodeResponse.builder()
                .id(promo.getId())
                .code(promo.getCode())
                .discountPercent(promo.getDiscountPercent())
                .expiryDate(promo.getExpiryDate())
                .usageLimit(promo.getUsageLimit())
                .usedCount(promo.getUsedCount())
                .active(promo.getActive())
                .build();
    }
}