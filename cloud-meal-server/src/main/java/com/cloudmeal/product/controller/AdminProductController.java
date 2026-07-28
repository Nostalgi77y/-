package com.cloudmeal.product.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.product.dto.DishSaveRequest;
import com.cloudmeal.product.entity.Dish;
import com.cloudmeal.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/admin/dishes")
public class AdminProductController {
    private final ProductService service;
    public AdminProductController(ProductService service) { this.service = service; }
    @GetMapping public ApiResponse<List<Dish>> list() { return ApiResponse.success(service.allDishes()); }
    @PostMapping public ApiResponse<Dish> create(@Valid @RequestBody DishSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}") public ApiResponse<Dish> update(@PathVariable Long id, @Valid @RequestBody DishSaveRequest request) {
        return ApiResponse.success(service.update(id, request));
    }
}
