package com.cloudmeal.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressSaveRequest(
        @NotBlank @Size(max = 50) String consignee,
        @NotBlank @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
        @Size(max = 50) String province,
        @Size(max = 50) String city,
        @Size(max = 50) String district,
        @NotBlank @Size(max = 255) String detail,
        boolean isDefault) {}
