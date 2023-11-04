package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Resource
    DishMapper dishMapper;

    @Resource
    DishFlavorMapper dishFlavorMapper;

    /**
     * 菜肴添加
     * @param dishDTO
     */
    @Transactional
    @Override
    public void addDish(DishDTO dishDTO) {
        //比较下DTO中的属性与数据库属性字段
        //DishDTO为前端数据与控制层数据的交互 Dish为数据库与java后端的交互

        //========第一部分存放普通菜肴数据============
        //BeanUtils的作用前提是两个类相同属性的名称一样
        //将DishDTO中的值遍历至bean中(剩余的部分可以用公共字段解决)
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);


        //调用方法add（菜品基本信息存入）
        dishMapper.add(dish);

        //========第二部分存放口味数据============
        //获取口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //获得菜肴id
        Long dishId = dish.getId();
        System.out.println("菜肴id为" + dishId);

        //给口味类设置菜肴id  存放口味
        if (flavors != null && flavors.size() > 0){
            //设置菜肴id
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            //给口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }



        //获取菜肴的id（菜肴存入会自动生成菜肴id--自增）


    }
}
