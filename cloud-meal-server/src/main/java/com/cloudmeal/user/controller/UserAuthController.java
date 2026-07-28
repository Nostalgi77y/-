package com.cloudmeal.user.controller;

import com.cloudmeal.auth.security.JwtService;
import com.cloudmeal.auth.vo.LoginResponse;
import com.cloudmeal.common.api.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {
    private final JwtService jwtService;
    public UserAuthController(JwtService jwtService) { this.jwtService = jwtService; }

    @PostMapping("/demo-login")
    public ApiResponse<LoginResponse> demoLogin() {
        String token = jwtService.create(1L, "demo-openid", "USER");
        return ApiResponse.success(new LoginResponse(token, 1L, "演示用户", "USER"));
    }
}
