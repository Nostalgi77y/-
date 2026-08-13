package com.cloudmeal.order.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.order.dto.OrderSubmitRequest;
import com.cloudmeal.order.service.OrderService;
import com.cloudmeal.order.vo.OrderVO;
import com.cloudmeal.payment.service.PaymentService;
import com.cloudmeal.payment.vo.PaymentCreateVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/user/orders")
public class UserOrderController {
    private final OrderService service;
    private final PaymentService paymentService;
    public UserOrderController(OrderService service, PaymentService paymentService) {
        this.service = service;
        this.paymentService = paymentService;
    }
    @PostMapping public ApiResponse<OrderVO> submit(@Valid @RequestBody OrderSubmitRequest request) { return ApiResponse.success(service.submit(request)); }
    @GetMapping public ApiResponse<List<OrderVO>> list() { return ApiResponse.success(service.userOrders()); }
    @PostMapping("/{id}/payment") public ApiResponse<PaymentCreateVO> payment(@PathVariable Long id) { return ApiResponse.success(paymentService.create(id)); }
    @PostMapping("/{id}/payment/confirm") public ApiResponse<Void> confirmPayment(@PathVariable Long id) { paymentService.confirm(id); return ApiResponse.success(); }
    @DeleteMapping("/{id}") public ApiResponse<Void> delete(@PathVariable Long id) { service.hideForUser(id); return ApiResponse.success(); }
}
