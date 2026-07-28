package com.cloudmeal.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employee")
public class Employee extends BaseEntity {
    private String username;
    private String password;
    private String name;
    private String role;
    private Integer status;
}
