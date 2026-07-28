package com.cloudmeal.product.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.common.exception.BusinessException;
import com.cloudmeal.product.dto.DishSaveRequest;
import com.cloudmeal.product.entity.Category;
import com.cloudmeal.product.entity.Dish;
import com.cloudmeal.product.mapper.CategoryMapper;
import com.cloudmeal.product.mapper.DishMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final CategoryMapper categoryMapper;
    private final DishMapper dishMapper;
    public ProductService(CategoryMapper categoryMapper, DishMapper dishMapper) {
        this.categoryMapper = categoryMapper; this.dishMapper = dishMapper;
    }

    @Cacheable(cacheNames = "categories", key = "'enabled'")
    public List<Category> categories() {
        return categoryMapper.selectList(Wrappers.<Category>lambdaQuery()
                .eq(Category::getStatus, 1).orderByAsc(Category::getSort));
    }

    @Cacheable(cacheNames = "dishes", key = "#categoryId ?: 'all'")
    public List<Dish> enabledDishes(Long categoryId) {
        return dishMapper.selectList(Wrappers.<Dish>lambdaQuery()
                .eq(categoryId != null, Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, 1).orderByDesc(Dish::getCreatedTime));
    }

    public List<Dish> allDishes() {
        return dishMapper.selectList(Wrappers.<Dish>lambdaQuery().orderByDesc(Dish::getCreatedTime));
    }

    @Transactional
    @CacheEvict(cacheNames = "dishes", allEntries = true)
    public Dish create(DishSaveRequest request) {
        if (categoryMapper.selectById(request.categoryId()) == null) {
            throw new BusinessException("CATEGORY_NOT_FOUND", "分类不存在");
        }
        Dish dish = toEntity(request);
        dishMapper.insert(dish);
        return dish;
    }

    @Transactional
    @CacheEvict(cacheNames = "dishes", allEntries = true)
    public Dish update(Long id, DishSaveRequest request) {
        Dish existing = dishMapper.selectById(id);
        if (existing == null) throw new BusinessException("DISH_NOT_FOUND", "菜品不存在");
        Dish dish = toEntity(request);
        dish.setId(id);
        dish.setVersion(existing.getVersion());
        dishMapper.updateById(dish);
        return dishMapper.selectById(id);
    }

    private Dish toEntity(DishSaveRequest request) {
        Dish dish = new Dish();
        dish.setCategoryId(request.categoryId()); dish.setName(request.name());
        dish.setPrice(request.price()); dish.setImage(request.image());
        dish.setDescription(request.description()); dish.setStock(request.stock());
        dish.setStatus(request.status()); dish.setVersion(0);
        return dish;
    }
}
