package com.cloudmeal.payment.config;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(prefix = "cloud-meal.payment", name = "mode", havingValue = "WECHAT")
public class WechatPayConfiguration {
    @Bean
    RSAAutoCertificateConfig wechatPayConfig(PaymentProperties payment, WechatProperties wechat) {
        require(wechat.getAppId(), "WECHAT_APP_ID");
        require(payment.getMerchantId(), "WECHAT_PAY_MERCHANT_ID");
        require(payment.getMerchantSerialNumber(), "WECHAT_PAY_MERCHANT_SERIAL_NUMBER");
        require(payment.getApiV3Key(), "WECHAT_PAY_API_V3_KEY");
        require(payment.getPrivateKeyPath(), "WECHAT_PAY_PRIVATE_KEY_PATH");
        require(payment.getNotifyUrl(), "WECHAT_PAY_NOTIFY_URL");
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(payment.getMerchantId())
                .privateKeyFromPath(payment.getPrivateKeyPath())
                .merchantSerialNumber(payment.getMerchantSerialNumber())
                .apiV3Key(payment.getApiV3Key())
                .build();
    }

    @Bean
    JsapiServiceExtension jsapiService(RSAAutoCertificateConfig config) {
        return new JsapiServiceExtension.Builder().config(config).signType("RSA").build();
    }

    @Bean
    NotificationParser notificationParser(RSAAutoCertificateConfig config) {
        return new NotificationParser(config);
    }

    private void require(String value, String environmentName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException("真实微信支付已开启，但缺少环境变量 " + environmentName);
        }
    }
}
