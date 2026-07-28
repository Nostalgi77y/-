package com.cloudmeal.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data @EqualsAndHashCode(callSuper = true) @TableName("dish")
public class Dish extends BaseEntity {
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer stock;
    private Integer status;
    @Version private Integer version;
}
