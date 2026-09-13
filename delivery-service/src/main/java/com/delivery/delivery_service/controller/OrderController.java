package com.delivery.delivery_service.controller;

import com.delivery.delivery_service.dto.*;
import com.delivery.delivery_service.entity.OrderStatus;
import com.delivery.delivery_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status) {
        if (status != null) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(status));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<OrderResponse> acceptOrder(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Double> body) {
        Double weightConfirmed = body != null ? body.get("weightConfirmed") : null;
        return ResponseEntity.ok(orderService.acceptOrder(id, weightConfirmed));
    }

    @PostMapping("/{id}/assign-locker")
    public ResponseEntity<AssignLockerResponse> assignLocker(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.assignLocker(id));
    }

    @PatchMapping("/pickup")
    public ResponseEntity<OrderResponse> pickupFromBranch(@Valid @RequestBody PickupRequest request) {
        return ResponseEntity.ok(orderService.pickupFromBranch(request.getPickupCode()));
    }

    @PostMapping("/{id}/open-locker")
    public ResponseEntity<OrderResponse> openLocker(
            @PathVariable Long id,
            @Valid @RequestBody PickupRequest request) {
        return ResponseEntity.ok(orderService.openLocker(id, request.getPickupCode()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }
}