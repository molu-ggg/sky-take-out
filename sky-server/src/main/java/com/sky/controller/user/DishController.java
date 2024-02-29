package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        String key = "dish_" + categoryId ; // TODO 数字可以直接加？
        List<DishVO> redis_list = (List<DishVO>)redisTemplate.opsForValue().get(key);// TODO 如何保证返回值与DishVO 一样，用什么格式存储的？
        if(redis_list !=null && redis_list.size() > 0){
            return Result.success(redis_list);
        }

        Dish dish = new Dish(); // TODO 这里怎么又用了new
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        List<DishVO> list = dishService.listWithFlavor(dish);

        redisTemplate.opsForValue().set(key,list); //TODO 为什么list 用opsForValue
        return Result.success(list);
    }

}