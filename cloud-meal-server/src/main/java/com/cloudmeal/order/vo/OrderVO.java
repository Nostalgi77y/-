package com.cloudmeal.order.vo;

import com.cloudmeal.order.entity.OrderDetail;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderVO(Long id, String orderNumber, String status, String payStatus, BigDecimal originalAmount,
                      BigDecimal discountAmount, BigDecimal amount,
                      String consignee, String phone, String address, String remark,
                      LocalDateTime createdTime, List<OrderDetail> details) {}
