package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车
     *
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     *
     * @param
     * @return
     */
    void clean();

    /**
     * 减少购物车食物的数量
     *
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
