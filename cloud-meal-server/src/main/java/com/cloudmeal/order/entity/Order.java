package com.cloudmeal.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @EqualsAndHashCode(callSuper = true) @TableName("orders")
public class Order extends BaseEntity {
    private String orderNumber;
    private String clientOrderNo;
    private Long userId;
    private Long addressBookId;
    private String status;
    private String payStatus;
    private String paymentChannel;
    private String transactionId;
    private String prepayId;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private Long userCouponId;
    private BigDecimal amount;
    private String consignee;
    private String phone;
    private String address;
    private String remark;
    private LocalDateTime paymentTime;
    private String cancelReason;
    private Integer userVisible;
    @Version private Integer version;
}
