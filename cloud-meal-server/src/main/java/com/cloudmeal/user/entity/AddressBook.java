package com.cloudmeal.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudmeal.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true) @TableName("address_book")
public class AddressBook extends BaseEntity {
    private Long userId;
    private String consignee;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;

    public String fullAddress() {
        return String.join("", value(province), value(city), value(district), value(detail));
    }
    private String value(String value) { return value == null ? "" : value; }
}
