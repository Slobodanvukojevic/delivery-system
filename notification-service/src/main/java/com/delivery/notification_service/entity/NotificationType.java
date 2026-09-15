package com.delivery.notification_service.entity;

public enum NotificationType {
    ORDER_CREATED,
    ORDER_STATUS_CHANGED,
    LOCKER_ASSIGNED,
    READY_FOR_PICKUP,
    DELIVERED,
    RETURN_TO_SENDER
}