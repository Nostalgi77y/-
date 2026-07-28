package com.cloudmeal.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @TableName("order_detail")
public class OrderDetail {
    @TableId private Long id;
    private Long orderId;
    private Long dishId;
    private String name;
    private String image;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal amount;
    private LocalDateTime createdTime;
}
