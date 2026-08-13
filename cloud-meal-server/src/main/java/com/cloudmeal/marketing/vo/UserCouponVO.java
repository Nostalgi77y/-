package com.cloudmeal.marketing.vo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record UserCouponVO(Long userCouponId, Long couponId, String name, BigDecimal thresholdAmount,
                           BigDecimal discountAmount, LocalDateTime validUntil, String status, boolean usable) {}
