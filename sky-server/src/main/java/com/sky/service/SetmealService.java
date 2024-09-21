package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 查询得到的菜品的选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    /**
     * 新增套餐以及包含的菜品
     * @param setmealDTO
     */
    void insertSetmealWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐的分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult getPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 套餐的批量删除
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 通过id获取套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐内容
     * @param setmealDTO
     */
    void updateSetmealWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐的起售停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
