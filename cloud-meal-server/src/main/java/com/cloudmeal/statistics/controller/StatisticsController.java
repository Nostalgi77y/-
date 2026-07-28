package com.cloudmeal.statistics.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.order.mapper.OrderMapper;
import com.cloudmeal.product.entity.Dish;
import com.cloudmeal.product.mapper.DishMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {
    private final OrderMapper orderMapper;
    private final DishMapper dishMapper;
    public StatisticsController(OrderMapper orderMapper, DishMapper dishMapper) {
        this.orderMapper = orderMapper; this.dishMapper = dishMapper;
    }

    @GetMapping("/today")
    public ApiResponse<TodayStatistics> today() {
        long onSale = dishMapper.selectCount(Wrappers.<Dish>lambdaQuery().eq(Dish::getStatus, 1));
        return ApiResponse.success(new TodayStatistics(orderMapper.todayRevenue(), orderMapper.todayOrderCount(),
                onSale, orderMapper.pendingOrderCount()));
    }

    public record TodayStatistics(BigDecimal revenue, long orderCount, long onSaleDishCount, long pendingOrderCount) {}
}
