package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应的口味。一个菜品可以对应多种口味
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);//新增菜品完成了插入
        Long dishId = dish.getId();//dishId是主键值，代表菜品.一个菜品可以对应多种口味
        List<DishFlavor> dishFlavors = dishDTO.getFlavors(); //dishFlavors数组获取了菜品的口味
        // 一个口味对应多个菜品

        if(dishFlavors!=null && dishFlavors.size() >0){
            dishFlavors.forEach( dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(dishFlavors);
        }



    }

    /**
     * 菜品的分页查询
      * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }


    @Autowired
    /**
     * 对套餐表进行操作
     */
    private SetmealDishMapper setmealDishMapper;

    /**
     * 菜品批量删除
     * @param ids 菜品id
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for(Long id: ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品处于起售状态，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否被关联到套餐中，如果被关联，则删除
        /**
         * 这里是通过菜品id去查询，看是否在套餐中
         */
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size()>0){
            throw  new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        for(Long id:ids){
            dishMapper.deleteById(id);
            //这里需要删除菜品相关联的口味
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 查询菜品
      * @param id
     * @return
     */
    public DishVO getByIdwithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);

        List<DishFlavor>dishFlavors = dishFlavorMapper.getById(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;


    }

    /**
     * 修改套餐内容
      * @param dishDTO
     */
    public void updateFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.update(dish);

        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        List<DishFlavor> dishFlavors = dishDTO.getFlavors();//获取菜品口味情况
        if(dishFlavors != null && dishFlavors.size() >0){
            dishFlavors.forEach(flavor ->{
                flavor.setDishId(dishDTO.getId()); //这里获取菜品口味对应的dishid，并且将两个组装起来
            });
            dishFlavorMapper.insertBatch(dishFlavors);
        }


    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();
        for(Dish d: dishList){
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            List<DishFlavor>flavors = dishFlavorMapper.getById(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

       return dishVOList;
    }


}
