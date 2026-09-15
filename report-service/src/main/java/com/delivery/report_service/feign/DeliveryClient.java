package com.delivery.report_service.feign;

import com.delivery.report_service.dto.OrderStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/api/orders/internal/stats")
    OrderStatsResponse getStats(
            @RequestParam("branchId") Long branchId,
            @RequestParam("date") String date
    );
}