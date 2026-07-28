package com.cloudmeal.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudmeal.product.entity.Dish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface DishMapper extends BaseMapper<Dish> {
    @Update("UPDATE dish SET stock = stock - #{quantity}, version = version + 1 " +
            "WHERE id = #{dishId} AND stock >= #{quantity} AND status = 1 AND deleted = 0")
    int deductStock(@Param("dishId") Long dishId, @Param("quantity") int quantity);

    @Update("UPDATE dish SET stock = stock + #{quantity}, version = version + 1 WHERE id = #{dishId} AND deleted = 0")
    int restoreStock(@Param("dishId") Long dishId, @Param("quantity") int quantity);
}
