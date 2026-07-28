package com.cloudmeal.order.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.order.model.OrderStatus;
import com.cloudmeal.order.service.OrderService;
import com.cloudmeal.order.vo.OrderVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/admin/orders")
public class AdminOrderController {
    private final OrderService service;
    public AdminOrderController(OrderService service) { this.service = service; }
    @GetMapping public ApiResponse<List<OrderVO>> list() { return ApiResponse.success(service.adminOrders()); }
    @PutMapping("/{id}/status/{status}") public ApiResponse<Void> transition(@PathVariable Long id, @PathVariable OrderStatus status) {
        service.transition(id, status); return ApiResponse.success();
    }
}
