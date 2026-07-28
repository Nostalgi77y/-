package com.cloudmeal.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderNotificationHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;
    public OrderNotificationHandler(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @Override public void afterConnectionEstablished(WebSocketSession session) { sessions.add(session); }
    @Override public void afterConnectionClosed(WebSocketSession session, CloseStatus status) { sessions.remove(session); }

    public void broadcast(String type, Long orderId, String message) {
        try {
            TextMessage text = new TextMessage(objectMapper.writeValueAsString(Map.of(
                    "type", type, "orderId", orderId, "message", message)));
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) session.sendMessage(text);
            }
        } catch (IOException ignored) {
            // 单次实时提醒失败不影响订单主流程，管理端仍会定时刷新最终状态。
        }
    }
}
