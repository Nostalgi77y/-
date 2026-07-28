package com.cloudmeal.config;

import com.cloudmeal.notification.OrderNotificationHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final OrderNotificationHandler handler;
    public WebSocketConfig(OrderNotificationHandler handler) { this.handler = handler; }
    @Override public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/orders").setAllowedOriginPatterns("*");
    }
}
