package com.cloudmeal.order.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.order.dto.OrderSubmitRequest;
import com.cloudmeal.order.service.OrderService;
import com.cloudmeal.order.vo.OrderVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/user/orders")
public class UserOrderController {
    private final OrderService service;
    public UserOrderController(OrderService service) { this.service = service; }
    @PostMapping public ApiResponse<OrderVO> submit(@Valid @RequestBody OrderSubmitRequest request) { return ApiResponse.success(service.submit(request)); }
    @GetMapping public ApiResponse<List<OrderVO>> list() { return ApiResponse.success(service.userOrders()); }
    @PostMapping("/{id}/mock-pay") public ApiResponse<Void> mockPay(@PathVariable Long id) { service.mockPay(id); return ApiResponse.success(); }
}
