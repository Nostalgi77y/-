package com.cloudmeal.payment.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.common.exception.BusinessException;
import com.cloudmeal.common.security.CurrentUser;
import com.cloudmeal.notification.OrderNotificationHandler;
import com.cloudmeal.order.entity.Order;
import com.cloudmeal.order.mapper.OrderMapper;
import com.cloudmeal.payment.config.PaymentProperties;
import com.cloudmeal.payment.config.WechatProperties;
import com.cloudmeal.payment.vo.PaymentCreateVO;
import com.cloudmeal.user.entity.User;
import com.cloudmeal.user.mapper.UserMapper;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentService {
    private final PaymentProperties paymentProperties;
    private final WechatProperties wechatProperties;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final OrderNotificationHandler notifier;
    private final ObjectProvider<JsapiServiceExtension> jsapiProvider;
    private final ObjectProvider<NotificationParser> parserProvider;

    public PaymentService(PaymentProperties paymentProperties, WechatProperties wechatProperties,
                          OrderMapper orderMapper, UserMapper userMapper, OrderNotificationHandler notifier,
                          ObjectProvider<JsapiServiceExtension> jsapiProvider,
                          ObjectProvider<NotificationParser> parserProvider) {
        this.paymentProperties = paymentProperties;
        this.wechatProperties = wechatProperties;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.notifier = notifier;
        this.jsapiProvider = jsapiProvider;
        this.parserProvider = parserProvider;
    }

    @Transactional
    public PaymentCreateVO create(Long orderId) {
        Long userId = CurrentUser.id();
        Order order = userOrder(orderId, userId);
        if ("PAID".equals(order.getPayStatus())) {
            throw new BusinessException("ORDER_ALREADY_PAID", "订单已经支付，请勿重复付款");
        }
        ensurePayable(order);

        if (order.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            markPaid(order, "COUPON", "FREE-" + order.getOrderNumber());
            return PaymentCreateVO.freePaid();
        }
        if (!paymentProperties.isWechat()) {
            markPaid(order, "MOCK", "MOCK-" + order.getOrderNumber());
            return PaymentCreateVO.mockPaid();
        }
        LocalDateTime paymentDeadline = order.getCreatedTime().plusMinutes(14);
        if (!LocalDateTime.now().isBefore(paymentDeadline)) {
            throw new BusinessException("ORDER_PAYMENT_EXPIRED", "订单已超过支付时间，请重新下单");
        }

        User user = userMapper.selectById(userId);
        if (user == null || user.getOpenid() == null || "demo-openid".equals(user.getOpenid())) {
            throw new BusinessException("WECHAT_LOGIN_REQUIRED", "真实支付前请先使用微信账号登录");
        }
        PrepayRequest request = new PrepayRequest();
        request.setAppid(wechatProperties.getAppId());
        request.setMchid(paymentProperties.getMerchantId());
        request.setDescription("云膳外卖订单 " + order.getOrderNumber());
        request.setOutTradeNo(order.getOrderNumber());
        request.setNotifyUrl(paymentProperties.getNotifyUrl());
        request.setTimeExpire(paymentDeadline.atZone(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        Amount amount = new Amount();
        amount.setTotal(toCents(order.getAmount()));
        amount.setCurrency("CNY");
        request.setAmount(amount);
        Payer payer = new Payer();
        payer.setOpenid(user.getOpenid());
        request.setPayer(payer);

        PrepayWithRequestPaymentResponse response = jsapi().prepayWithRequestPayment(request);
        orderMapper.recordPrepay(order.getId(), "WECHAT", prepayId(response.getPackageVal()));
        return new PaymentCreateVO("WECHAT", "PENDING", response.getAppId(), response.getTimeStamp(),
                response.getNonceStr(), response.getPackageVal(), response.getSignType(), response.getPaySign());
    }

    @Transactional
    public void confirm(Long orderId) {
        Long userId = CurrentUser.id();
        Order order = userOrder(orderId, userId);
        if ("PAID".equals(order.getPayStatus())) return;
        if (!paymentProperties.isWechat()) {
            throw new BusinessException("PAYMENT_MODE_INVALID", "当前未开启微信支付");
        }
        settle(query(order.getOrderNumber()));
    }

    @Transactional
    public void handleNotification(String serial, String timestamp, String nonce, String signature, String body) {
        RequestParam request = new RequestParam.Builder()
                .serialNumber(serial).timestamp(timestamp).nonce(nonce).signature(signature).body(body).build();
        Transaction transaction = parser().parse(request, Transaction.class);
        settle(transaction);
    }

    @Transactional
    public boolean settleOrCloseForTimeout(Order order) {
        if (!paymentProperties.isWechat() || !"WECHAT".equals(order.getPaymentChannel())
                || order.getPrepayId() == null) return false;
        Transaction transaction = query(order.getOrderNumber());
        if (transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
            settle(transaction);
            return true;
        }
        if (transaction.getTradeState() == Transaction.TradeStateEnum.CLOSED
                || transaction.getTradeState() == Transaction.TradeStateEnum.REVOKED
                || transaction.getTradeState() == Transaction.TradeStateEnum.PAYERROR
                || transaction.getTradeState() == Transaction.TradeStateEnum.REFUND) {
            return false;
        }
        CloseOrderRequest closeRequest = new CloseOrderRequest();
        closeRequest.setMchid(paymentProperties.getMerchantId());
        closeRequest.setOutTradeNo(order.getOrderNumber());
        try {
            jsapi().closeOrder(closeRequest);
            return false;
        } catch (RuntimeException closeError) {
            Transaction latest = query(order.getOrderNumber());
            if (latest.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
                settle(latest);
                return true;
            }
            throw closeError;
        }
    }

    private Transaction query(String orderNumber) {
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setMchid(paymentProperties.getMerchantId());
        request.setOutTradeNo(orderNumber);
        return jsapi().queryOrderByOutTradeNo(request);
    }

    private void settle(Transaction transaction) {
        if (transaction == null || transaction.getTradeState() != Transaction.TradeStateEnum.SUCCESS) {
            throw new BusinessException("PAYMENT_NOT_SUCCESS", "微信支付尚未成功，请稍后刷新订单");
        }
        if (!wechatProperties.getAppId().equals(transaction.getAppid())
                || !paymentProperties.getMerchantId().equals(transaction.getMchid())) {
            throw new BusinessException("PAYMENT_MERCHANT_MISMATCH", "支付商户信息不匹配");
        }
        Order order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getOrderNumber, transaction.getOutTradeNo()));
        if (order == null) throw new BusinessException("ORDER_NOT_FOUND", "支付对应的订单不存在");
        if (transaction.getAmount() == null || transaction.getAmount().getTotal() == null
                || transaction.getAmount().getTotal() != toCents(order.getAmount())) {
            throw new BusinessException("PAYMENT_AMOUNT_MISMATCH", "微信支付金额与订单金额不一致");
        }
        if ("PAID".equals(order.getPayStatus())) {
            if (!transaction.getTransactionId().equals(order.getTransactionId())) {
                throw new BusinessException("PAYMENT_TRANSACTION_MISMATCH", "订单支付流水号不一致");
            }
            return;
        }
        markPaid(order, "WECHAT", transaction.getTransactionId());
    }

    private void markPaid(Order order, String channel, String transactionId) {
        if (orderMapper.markPaidByOrderNumber(order.getOrderNumber(), channel, transactionId) != 1) {
            Order latest = orderMapper.selectById(order.getId());
            if (latest == null || !"PAID".equals(latest.getPayStatus())
                    || !transactionId.equals(latest.getTransactionId())) {
                throw new BusinessException("ORDER_STATUS_INVALID", "当前订单不能支付");
            }
            return;
        }
        notifier.broadcast("ORDER_PAID", order.getId(), "订单已支付，请及时接单");
    }

    private Order userOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getId, orderId).eq(Order::getUserId, userId));
        if (order == null) throw new BusinessException("ORDER_NOT_FOUND", "订单不存在");
        return order;
    }

    private void ensurePayable(Order order) {
        if (!"PENDING_PAYMENT".equals(order.getStatus()) || !"UNPAID".equals(order.getPayStatus())) {
            throw new BusinessException("ORDER_STATUS_INVALID", "当前订单不能支付");
        }
    }

    private int toCents(BigDecimal amount) {
        return amount.movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).intValueExact();
    }

    private String prepayId(String packageValue) {
        return packageValue != null && packageValue.startsWith("prepay_id=")
                ? packageValue.substring("prepay_id=".length()) : packageValue;
    }

    private JsapiServiceExtension jsapi() {
        JsapiServiceExtension service = jsapiProvider.getIfAvailable();
        if (service == null) throw new BusinessException("WECHAT_PAY_NOT_CONFIGURED", "微信支付服务未正确配置");
        return service;
    }

    private NotificationParser parser() {
        NotificationParser parser = parserProvider.getIfAvailable();
        if (parser == null) throw new BusinessException("WECHAT_PAY_NOT_CONFIGURED", "微信支付回调验签服务未正确配置");
        return parser;
    }
}
