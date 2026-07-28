package com.cloudmeal.order.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {
    @Test void allowsExpectedTransitions() {
        assertThat(OrderStatus.PENDING_PAYMENT.canTransitionTo(OrderStatus.PENDING_ACCEPTANCE)).isTrue();
        assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.PENDING_DELIVERY)).isTrue();
        assertThat(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
    }
}
