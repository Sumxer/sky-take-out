package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishSerivce;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishSerivce.saveWithFlavor(dishDTO);

        //清除缓存数据.在第一次插入的时候清空缓存数据，然后在后续调用的时候，就会直接从数据库加载出来。
        String key = "dish_" + dishDTO.getCategoryId();
        cleancache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询:{}",dishPageQueryDTO);
        PageResult pageResult = dishSerivce.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除:{}", ids);
        dishSerivce.deleteBatch(ids);

        cleancache("dish_*");
        return Result.success();
    }

    /**
     * 根据ID进行菜品信息查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("菜品信息查询")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据ID进行菜品查询:{}",id);
        DishVO dishVO=dishSerivce.getByIdwithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息");
        dishSerivce.updateFlavor(dishDTO);
        cleancache("dish_*");
        return Result.success();
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 清理缓存
     */
    private void cleancache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
