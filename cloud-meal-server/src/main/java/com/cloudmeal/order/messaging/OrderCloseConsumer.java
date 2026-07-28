package com.cloudmeal.order.messaging;

import com.cloudmeal.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCloseConsumer {
    private final OrderService orderService;
    public OrderCloseConsumer(OrderService orderService) { this.orderService = orderService; }
    @RabbitListener(queues = OrderMessageConfig.CLOSE_QUEUE)
    public void close(String orderId) { orderService.closeIfUnpaid(Long.parseLong(orderId)); }
}
