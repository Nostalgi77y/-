package com.cloudmeal.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderSubmitRequest(
        @NotBlank @Size(max = 64) String clientOrderNo,
        @NotNull Long addressBookId,
        Long userCouponId,
        @Size(max = 500) String remark) {}
