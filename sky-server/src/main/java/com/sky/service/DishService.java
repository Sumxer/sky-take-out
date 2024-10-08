package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询菜品
     * @param id
     * @return
     */
    DishVO getByIdwithFlavor(Long id);

    /**
     * 修改口味
     * @param dishDTO
     */
    void updateFlavor(DishDTO dishDTO);
    /**
     * 条件查询菜品以及口味
     * @param dish
     * @return
     */
     List<DishVO> listWithFlavor(Dish dish);
}
