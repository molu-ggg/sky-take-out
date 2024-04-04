package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.primary.DishMapper;
import com.sky.mapper.primary.SetmealMapper;
import com.sky.mapper.primary.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    @Qualifier("primary") // 指定注入 primary 数据源
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //创建一个购物车类型 将 Bean DTO <----购物车 shoppingCartDTO 是一件商品（菜或者套餐）的信息 shoppingCart 也仅仅是购物车的一条数据！！ 而不是商品的集合！！
        System.out.println(shoppingCartDTO.toString());
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart); //是将 shoppingCartDTO 对象的属性值复制到 shoppingCart 对象中
        shoppingCart.setUserId(BaseContext.getCurrentId());//TODO BaseContext.getCurrentId()：ThreadLocal已经从拦截器获取到userid 注意是Base 而不是Bean
        System.out.println(BaseContext.getCurrentId());
        //数据库：判断当前商品是否在购物车中
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart); // 可能会存在多个购物车，下面的逻辑是选择第一个购物车

        if (shoppingCartList != null && shoppingCartList.size() == 1) {
            //如果已经存在，就更新数量，数量加1
            shoppingCart = shoppingCartList.get(0);// TODO 为什么得到第一个元素? 因为上述条件保证了智能查到最多一条数据，这样这几mapper单纯是为了代码复用
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        } else {
            //如果不存在，插入数据，数量就是1
            //判断当前添加到购物车的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //添加到购物车的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //添加到购物车的是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }
    /**
     * 查看购物车
     * @return
     */

    @Override
    public List<ShoppingCart> showShoppingCart() {
          Long userId = BaseContext.getCurrentId();
//        ShoppingCart shoppingCart = new ShoppingCart();
//        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build(); // TODO 分析 这个跟上面两种方法
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        return shoppingCartList ;
    }

    @Override
    public void cleanShoppingCart() {
        Long user_id = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(user_id);

    }
    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long user_id = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(user_id);

        //TODO  先查询 后删除  这样做 是为什么？
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && list.size() > 0){
            shoppingCart = list.get(0);
//        shoppingCartMapper.subShoppingCard(shoppingCart); //TODO 你没有考虑到时删除，还是sub 减法，下面是别人的做法
            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }

    }

}

