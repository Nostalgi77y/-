package com.cloudmeal.order.model;

import java.util.Set;

public enum OrderStatus {
    PENDING_PAYMENT,
    PENDING_ACCEPTANCE,
    PREPARING,
    PENDING_DELIVERY,
    DELIVERING,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case PENDING_PAYMENT -> Set.of(PENDING_ACCEPTANCE, CANCELLED).contains(target);
            case PENDING_ACCEPTANCE -> Set.of(PREPARING, CANCELLED).contains(target);
            case PREPARING -> target == PENDING_DELIVERY;
            case PENDING_DELIVERY -> target == DELIVERING;
            case DELIVERING -> target == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
