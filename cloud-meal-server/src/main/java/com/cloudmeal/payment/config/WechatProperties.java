package com.cloudmeal.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloud-meal.wechat")
public class WechatProperties {
    private String appId = "";
    private String appSecret = "";
}
