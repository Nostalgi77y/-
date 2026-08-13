package com.cloudmeal.marketing.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudmeal.marketing.entity.Coupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
public interface CouponMapper extends BaseMapper<Coupon> {
    @Update("UPDATE coupon SET received_count=received_count+1 WHERE id=#{id} AND status=1 AND received_count<total_count")
    int takeOne(@Param("id") Long id);
}
