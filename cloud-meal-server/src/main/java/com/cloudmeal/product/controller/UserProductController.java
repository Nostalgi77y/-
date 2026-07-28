package com.cloudmeal.product.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.product.entity.Category;
import com.cloudmeal.product.entity.Dish;
import com.cloudmeal.product.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/user")
public class UserProductController {
    private final ProductService service;
    public UserProductController(ProductService service) { this.service = service; }
    @GetMapping("/categories") public ApiResponse<List<Category>> categories() { return ApiResponse.success(service.categories()); }
    @GetMapping("/dishes") public ApiResponse<List<Dish>> dishes(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(service.enabledDishes(categoryId));
    }
}
