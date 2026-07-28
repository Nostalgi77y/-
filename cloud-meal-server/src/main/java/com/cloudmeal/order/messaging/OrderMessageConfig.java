package com.cloudmeal.order.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMessageConfig {
    public static final String DELAY_EXCHANGE = "order.delay.exchange";
    public static final String DELAY_QUEUE = "order.delay.queue";
    public static final String CLOSE_EXCHANGE = "order.close.exchange";
    public static final String CLOSE_QUEUE = "order.close.queue";

    @Bean DirectExchange orderDelayExchange() { return new DirectExchange(DELAY_EXCHANGE, true, false); }
    @Bean DirectExchange orderCloseExchange() { return new DirectExchange(CLOSE_EXCHANGE, true, false); }
    @Bean Queue orderDelayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE)
                .ttl(15 * 60 * 1000)
                .deadLetterExchange(CLOSE_EXCHANGE)
                .deadLetterRoutingKey("close")
                .build();
    }
    @Bean Queue orderCloseQueue() { return QueueBuilder.durable(CLOSE_QUEUE).build(); }
    @Bean Binding delayBinding() { return BindingBuilder.bind(orderDelayQueue()).to(orderDelayExchange()).with("delay"); }
    @Bean Binding closeBinding() { return BindingBuilder.bind(orderCloseQueue()).to(orderCloseExchange()).with("close"); }
}
