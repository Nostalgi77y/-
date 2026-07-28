package com.cloudmeal.auth.controller;

import com.cloudmeal.auth.dto.LoginRequest;
import com.cloudmeal.auth.service.AuthService;
import com.cloudmeal.auth.vo.LoginResponse;
import com.cloudmeal.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
