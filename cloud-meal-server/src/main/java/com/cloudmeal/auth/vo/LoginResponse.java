package com.cloudmeal.auth.vo;

public record LoginResponse(String token, Long userId, String name, String role) {}
