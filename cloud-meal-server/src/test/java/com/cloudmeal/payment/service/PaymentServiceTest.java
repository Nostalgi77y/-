package com.cloudmeal.payment.service;

import com.cloudmeal.notification.OrderNotificationHandler;
import com.cloudmeal.order.entity.Order;
import com.cloudmeal.order.mapper.OrderMapper;
import com.cloudmeal.payment.config.PaymentProperties;
import com.cloudmeal.payment.config.WechatProperties;
import com.cloudmeal.user.mapper.UserMapper;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.TransactionAmount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {
    private final PaymentProperties paymentProperties = new PaymentProperties();
    private final WechatProperties wechatProperties = new WechatProperties();
    private final OrderMapper orderMapper = mock(OrderMapper.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final OrderNotificationHandler notifier = mock(OrderNotificationHandler.class);
    private final ObjectProvider<JsapiServiceExtension> jsapiProvider = mock(ObjectProvider.class);
    private final ObjectProvider<NotificationParser> parserProvider = mock(ObjectProvider.class);
    private PaymentService service;

    @BeforeEach
    void setUp() {
        paymentProperties.setMode("MOCK");
        service = new PaymentService(paymentProperties, wechatProperties, orderMapper, userMapper, notifier,
                jsapiProvider, parserProvider);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user", null);
        authentication.setDetails(1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void mockModeUsesUnifiedPaymentAndAtomicallyMarksPaid() {
        Order order = pendingOrder();
        when(orderMapper.selectOne(any())).thenReturn(order);
        when(orderMapper.markPaidByOrderNumber(order.getOrderNumber(), "MOCK", "MOCK-" + order.getOrderNumber()))
                .thenReturn(1);

        var result = service.create(order.getId());

        assertEquals("MOCK", result.mode());
        assertEquals("PAID", result.status());
        verify(notifier).broadcast("ORDER_PAID", order.getId(), "订单已支付，请及时接单");
    }

    @Test
    void verifiedWechatCallbackChecksAndRecordsTransaction() {
        paymentProperties.setMode("WECHAT");
        paymentProperties.setMerchantId("1900000001");
        wechatProperties.setAppId("wx-app-id");
        Order order = pendingOrder();
        Transaction transaction = new Transaction();
        transaction.setTradeState(Transaction.TradeStateEnum.SUCCESS);
        transaction.setAppid("wx-app-id");
        transaction.setMchid("1900000001");
        transaction.setOutTradeNo(order.getOrderNumber());
        transaction.setTransactionId("4200000000001");
        TransactionAmount amount = new TransactionAmount();
        amount.setTotal(2800);
        transaction.setAmount(amount);
        NotificationParser parser = mock(NotificationParser.class);
        when(parserProvider.getIfAvailable()).thenReturn(parser);
        when(parser.parse(any(), eq(Transaction.class))).thenReturn(transaction);
        when(orderMapper.selectOne(any())).thenReturn(order);
        when(orderMapper.markPaidByOrderNumber(order.getOrderNumber(), "WECHAT", transaction.getTransactionId()))
                .thenReturn(1);

        service.handleNotification("serial", "timestamp", "nonce", "signature", "{}");

        verify(orderMapper).markPaidByOrderNumber(order.getOrderNumber(), "WECHAT", transaction.getTransactionId());
        verify(notifier).broadcast("ORDER_PAID", order.getId(), "订单已支付，请及时接单");
    }

    private Order pendingOrder() {
        Order order = new Order();
        order.setId(9L);
        order.setUserId(1L);
        order.setOrderNumber("202608130001");
        order.setStatus("PENDING_PAYMENT");
        order.setPayStatus("UNPAID");
        order.setAmount(new BigDecimal("28.00"));
        order.setCreatedTime(LocalDateTime.now());
        return order;
    }
}
