package com.delivery.payment_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @PatchMapping("/api/orders/internal/{id}/status")
    void updateOrderStatus(@PathVariable("id") Long orderId, @RequestBody StatusUpdateRequest request);
}