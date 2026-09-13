package com.delivery.delivery_service.entity;

public enum OrderStatus {
    PENDING,
    ACCEPTED_AT_BRANCH,
    IN_SORTING,
    IN_TRANSIT,
    PLACED_IN_LOCKER,
    READY_FOR_BRANCH_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    PICKED_UP_BY_CUSTOMER,
    RETURN_TO_SENDER,
    CANCELLED
}