package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {

    /**
     * 菜品添加
     * @param dishDTO
     */
    void addDish(DishDTO dishDTO);
}
