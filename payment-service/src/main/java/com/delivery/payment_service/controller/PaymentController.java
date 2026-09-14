package com.delivery.payment_service.controller;

import com.delivery.payment_service.dto.*;
import com.delivery.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getInvoiceByOrderId(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getUserTransactions(userId));
    }

    @PostMapping("/promo-codes")
    public ResponseEntity<PromoCodeResponse> createPromoCode(
            @Valid @RequestBody CreatePromoCodeRequest request) {
        PromoCodeResponse response = paymentService.createPromoCode(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/promo-codes")
    public ResponseEntity<List<PromoCodeResponse>> getAllPromoCodes() {
        return ResponseEntity.ok(paymentService.getAllPromoCodes());
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long transactionId) {
        return ResponseEntity.ok(paymentService.refundPayment(transactionId));
    }
}