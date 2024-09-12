package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ID查询对应的套餐结果
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
