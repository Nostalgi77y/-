package com.cloudmeal.cart.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.cart.dto.CartAddRequest;
import com.cloudmeal.cart.entity.ShoppingCart;
import com.cloudmeal.cart.mapper.ShoppingCartMapper;
import com.cloudmeal.common.exception.BusinessException;
import com.cloudmeal.common.security.CurrentUser;
import com.cloudmeal.product.entity.Dish;
import com.cloudmeal.product.mapper.DishMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CartService {
    private final ShoppingCartMapper cartMapper;
    private final DishMapper dishMapper;
    public CartService(ShoppingCartMapper cartMapper, DishMapper dishMapper) { this.cartMapper = cartMapper; this.dishMapper = dishMapper; }

    public List<ShoppingCart> list() {
        return cartMapper.selectList(Wrappers.<ShoppingCart>lambdaQuery().eq(ShoppingCart::getUserId, CurrentUser.id()));
    }

    @Transactional
    public void add(CartAddRequest request) {
        Long userId = CurrentUser.id();
        Dish dish = dishMapper.selectById(request.dishId());
        if (dish == null || dish.getStatus() != 1) throw new BusinessException("DISH_UNAVAILABLE", "菜品不存在或已下架");
        ShoppingCart existing = cartMapper.selectOne(Wrappers.<ShoppingCart>lambdaQuery()
                .eq(ShoppingCart::getUserId, userId).eq(ShoppingCart::getDishId, request.dishId()));
        int target = request.quantity() + (existing == null ? 0 : existing.getQuantity());
        if (target > dish.getStock()) throw new BusinessException("STOCK_NOT_ENOUGH", "菜品库存不足");
        if (existing == null) {
            ShoppingCart cart = new ShoppingCart(); cart.setUserId(userId); cart.setDishId(dish.getId());
            cart.setDishName(dish.getName()); cart.setImage(dish.getImage()); cart.setUnitPrice(dish.getPrice());
            cart.setQuantity(request.quantity()); cartMapper.insert(cart);
        } else {
            existing.setQuantity(target); cartMapper.updateById(existing);
        }
    }

    public void clear() {
        // Shopping-cart rows are temporary data. A physical delete also releases the
        // (user_id, dish_id) unique key so the same dish can be added again later.
        cartMapper.deleteAllByUserId(CurrentUser.id());
    }
}
