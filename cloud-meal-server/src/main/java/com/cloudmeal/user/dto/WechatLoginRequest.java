package com.cloudmeal.user.dto;

import jakarta.validation.constraints.NotBlank;

public record WechatLoginRequest(@NotBlank String code) {
}
