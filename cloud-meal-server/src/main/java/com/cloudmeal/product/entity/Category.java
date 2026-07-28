package com.cloudmeal.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true) @TableName("category")
public class Category extends BaseEntity {
    private String name;
    private Integer type;
    private Integer sort;
    private Integer status;
}
