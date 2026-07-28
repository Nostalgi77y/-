package com.cloudmeal.cart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data @EqualsAndHashCode(callSuper = true) @TableName("shopping_cart")
public class ShoppingCart extends BaseEntity {
    private Long userId;
    private Long dishId;
    private String dishName;
    private String image;
    private BigDecimal unitPrice;
    private Integer quantity;
}
