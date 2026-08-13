package com.cloudmeal.cart.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudmeal.cart.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    int deleteAllByUserId(@Param("userId") Long userId);
}
