package com.cloudmeal.marketing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @EqualsAndHashCode(callSuper = true) @TableName("coupon")
public class Coupon extends BaseEntity {
    private String name;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private Integer totalCount;
    private Integer receivedCount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer status;
}
