package com.cloudmeal.cart.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudmeal.cart.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    int deleteAllByUserId(@Param("userId") Long userId);

    @Update("""
            UPDATE shopping_cart
            SET dish_name = #{dishName}, image = #{image}, unit_price = #{unitPrice},
                quantity = #{quantity}, deleted = 0, updated_time = NOW()
            WHERE user_id = #{userId} AND dish_id = #{dishId} AND deleted = 1
            """)
    int restoreDeleted(@Param("userId") Long userId, @Param("dishId") Long dishId,
                       @Param("dishName") String dishName, @Param("image") String image,
                       @Param("unitPrice") java.math.BigDecimal unitPrice, @Param("quantity") Integer quantity);
}
