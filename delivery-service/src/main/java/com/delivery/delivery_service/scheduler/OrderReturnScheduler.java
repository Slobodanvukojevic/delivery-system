package com.delivery.delivery_service.scheduler;

import com.delivery.delivery_service.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderReturnScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrderReturnScheduler.class);

    private final OrderService orderService;

    public OrderReturnScheduler(OrderService orderService) {
        this.orderService = orderService;
    }


    // Produkcija: "0 0 2 * * *" 2 ujutro
    // Demo: "0 */2 * * * *" svaka 2 minuta
    @Scheduled(cron = "${delivery.return-check-cron:0 0 2 * * *}")
    public void checkExpiredOrders() {
        log.info("Pokrecem proveru isteklih porudzbina");
        try {
            int returned = orderService.returnExpiredOrders();
            log.info("Provera zavrsena. Vraceno {} porudzbina posiljaocu", returned);
        } catch (Exception e) {
            log.error("Greska pri proveri isteklih porudzbina", e);
        }
    }
}