package com.cloudmeal.marketing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data @EqualsAndHashCode(callSuper = true) @TableName("user_coupon")
public class UserCoupon extends BaseEntity {
    private Long userId;
    private Long couponId;
    private String status;
    private Long usedOrderId;
    private LocalDateTime receivedTime;
    private LocalDateTime usedTime;
}
