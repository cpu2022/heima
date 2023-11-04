package com.sky.controller.admin;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.enumeration.OperationType;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜肴各类接口")
@Slf4j
public class DishController {


    @Autowired
    DishService dishService;

    /**
     * 添加菜肴(目的--》将菜肴存放--》以及将口味存放至数据库)
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "添加菜肴")
    @AutoFill(OperationType.INSERT)
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("接收到的菜肴数据是{}", dishDTO);
        dishService.addDish(dishDTO);
        //todo
        return Result.success();
    }
}
