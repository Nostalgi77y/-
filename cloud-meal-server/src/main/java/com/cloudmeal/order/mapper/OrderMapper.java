package com.cloudmeal.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudmeal.order.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;

public interface OrderMapper extends BaseMapper<Order> {
    @Update("UPDATE orders SET status=#{target}, updated_time=NOW(), version=version+1 " +
            "WHERE id=#{id} AND status=#{expected} AND deleted=0")
    int transition(@Param("id") Long id, @Param("expected") String expected, @Param("target") String target);

    @Update("UPDATE orders SET status='PENDING_ACCEPTANCE', pay_status='PAID', payment_time=NOW(), " +
            "updated_time=NOW(), version=version+1 WHERE id=#{id} AND user_id=#{userId} " +
            "AND status='PENDING_PAYMENT' AND pay_status='UNPAID' AND deleted=0")
    int markPaid(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT COALESCE(SUM(amount),0) FROM orders WHERE pay_status='PAID' AND DATE(created_time)=CURRENT_DATE AND deleted=0")
    BigDecimal todayRevenue();

    @Select("SELECT COUNT(*) FROM orders WHERE DATE(created_time)=CURRENT_DATE AND status <> 'CANCELLED' AND deleted=0")
    long todayOrderCount();

    @Select("SELECT COUNT(*) FROM orders WHERE status IN ('PENDING_ACCEPTANCE','PREPARING','PENDING_DELIVERY','DELIVERING') AND deleted=0")
    long pendingOrderCount();
}
