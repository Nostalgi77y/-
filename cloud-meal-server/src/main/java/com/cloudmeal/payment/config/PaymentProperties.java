package com.cloudmeal.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloud-meal.payment")
public class PaymentProperties {
    private String mode = "MOCK";
    private String merchantId = "";
    private String merchantSerialNumber = "";
    private String apiV3Key = "";
    private String privateKeyPath = "";
    private String notifyUrl = "";

    public boolean isWechat() {
        return "WECHAT".equalsIgnoreCase(mode);
    }
}
