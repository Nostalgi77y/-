package com.cloudmeal.user.controller;

import com.cloudmeal.auth.security.JwtService;
import com.cloudmeal.auth.vo.LoginResponse;
import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.user.dto.WechatLoginRequest;
import com.cloudmeal.user.service.WechatLoginService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {
    private final JwtService jwtService;
    private final WechatLoginService wechatLoginService;
    public UserAuthController(JwtService jwtService, WechatLoginService wechatLoginService) {
        this.jwtService = jwtService;
        this.wechatLoginService = wechatLoginService;
    }

    @PostMapping("/wechat-login")
    public ApiResponse<LoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        return ApiResponse.success(wechatLoginService.login(request.code()));
    }

    @PostMapping("/demo-login")
    public ApiResponse<LoginResponse> demoLogin() {
        String token = jwtService.create(1L, "demo-openid", "USER");
        return ApiResponse.success(new LoginResponse(token, 1L, "演示用户", "USER"));
    }
}
