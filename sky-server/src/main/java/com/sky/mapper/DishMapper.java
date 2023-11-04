package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 菜品保存
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into dish(name, description, category_id, price, image, status,create_Time,update_time,create_user,update_user) " +
            "values(#{name},#{description},#{categoryId},#{price},#{image},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void add(Dish dish);

}
