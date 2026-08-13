package com.cloudmeal.marketing.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.marketing.entity.Coupon;
import com.cloudmeal.marketing.service.CouponService;
import com.cloudmeal.marketing.vo.UserCouponVO;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController @RequestMapping("/user/coupons")
public class CouponController {
    private final CouponService service;
    public CouponController(CouponService service) { this.service = service; }
    @GetMapping("/available") public ApiResponse<List<Coupon>> available() { return ApiResponse.success(service.available()); }
    @PostMapping("/{couponId}/receive") public ApiResponse<Void> receive(@PathVariable Long couponId) { service.receive(couponId); return ApiResponse.success(); }
    @GetMapping("/mine") public ApiResponse<List<UserCouponVO>> mine(@RequestParam(required = false) BigDecimal orderAmount) {
        return ApiResponse.success(service.mine(orderAmount));
    }
}
