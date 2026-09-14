package com.delivery.delivery_service.controller;

import com.delivery.delivery_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/internal")
public class InternalOrderController {

    private final OrderService orderService;

    public InternalOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatusInternal(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        orderService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}