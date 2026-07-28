package com.cloudmeal.cart.controller;

import com.cloudmeal.cart.dto.CartAddRequest;
import com.cloudmeal.cart.entity.ShoppingCart;
import com.cloudmeal.cart.service.CartService;
import com.cloudmeal.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/user/cart")
public class CartController {
    private final CartService service;
    public CartController(CartService service) { this.service = service; }
    @GetMapping public ApiResponse<List<ShoppingCart>> list() { return ApiResponse.success(service.list()); }
    @PostMapping public ApiResponse<Void> add(@Valid @RequestBody CartAddRequest request) { service.add(request); return ApiResponse.success(); }
    @DeleteMapping public ApiResponse<Void> clear() { service.clear(); return ApiResponse.success(); }
}
