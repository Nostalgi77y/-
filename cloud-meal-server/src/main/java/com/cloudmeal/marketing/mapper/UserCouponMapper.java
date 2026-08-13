package com.cloudmeal.marketing.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudmeal.marketing.entity.UserCoupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
    @Update("UPDATE user_coupon SET status='USED', used_order_id=#{orderId}, used_time=NOW(), updated_time=NOW() " +
            "WHERE id=#{id} AND user_id=#{userId} AND status='UNUSED' AND deleted=0")
    int markUsed(@Param("id") Long id, @Param("userId") Long userId, @Param("orderId") Long orderId);
    @Update("UPDATE user_coupon SET status='UNUSED', used_order_id=NULL, used_time=NULL, updated_time=NOW() " +
            "WHERE id=#{id} AND used_order_id=#{orderId} AND status='USED' AND deleted=0")
    int release(@Param("id") Long id, @Param("orderId") Long orderId);
}
