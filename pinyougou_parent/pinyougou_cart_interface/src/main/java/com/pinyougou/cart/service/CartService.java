package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

public interface CartService {

    //添加商品到购物车

    public List<Cart>addGoodsToCartList (List<Cart> cartList,Long itemId,Integer number);



    /**
     * 从 redis 中查询购物车
     */
    public List<Cart> findCartListFromRedis(String username);
    /**
     * 将购物车保存到 redis
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);


    //合并购物车
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
