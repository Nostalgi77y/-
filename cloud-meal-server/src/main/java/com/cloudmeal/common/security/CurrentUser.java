package com.cloudmeal.common.security;

import com.cloudmeal.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {}

    public static Long id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getDetails() instanceof Number number)) {
            throw new BusinessException("UNAUTHORIZED", "请先登录");
        }
        return number.longValue();
    }
}
