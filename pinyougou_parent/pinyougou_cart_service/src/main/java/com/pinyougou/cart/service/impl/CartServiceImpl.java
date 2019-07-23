package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 添加商品到购物车列表
     * cartList ： 购物车列表
     * itemId ： 商品的id
     * num ： 商品的数量
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据skuID查询商品明细SKU的对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法");
        }
        //2.根据SKU对象得到商家ID
        String sellerId = item.getSellerId();//商家ID

        //判断购物车列表是否有商家
        //3.根据商家ID在购物车列表中查询购物车对象
        Cart cart = searchCartBySellerId(cartList,sellerId);

        if(cart==null){//4.如果购物车列表中不存在该商家的购物车

            //4.1 创建一个新的购物车对象
            cart=new Cart();
            cart.setSellerId(sellerId);//商家ID
            cart.setSellerName(item.getSeller());//商家名称

            List<TbOrderItem> orderItemList=new ArrayList<>();//创建购物车明细列表，保存商品信息的;l
            TbOrderItem orderItem = createOrderItem(item,num);	//获取商品信息
            orderItemList.add(orderItem);//将商品的信息保存到集合中

            cart.setOrderItemList(orderItemList);//保存购物车明细列表

            //4.2将新的购物车对象添加到购物车列表中
            cartList.add(cart);

        }else{//5.如果购物车列表中存在该商家的购物车对象
            // 判断该商品是否在商家的购物车明细列表中
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            if(orderItem==null){
                //5.1  如果不存在  ，创建新的购物车明细对象，并添加到该购物车的明细列表中
                orderItem=createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);

            }else{
                //5.2 如果存在，在原有的数量上添加数量 ,并且更新金额
                orderItem.setNum(orderItem.getNum()+num);//更改数量
                //金额
                orderItem.setTotalFee( new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum() ) );

                //当明细的数量小于等于0，从明细列表中移除明细对象
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //当商家的购物车明细列表为空了，表示该商家已经没有商品在购物车中，将商家从购物车列表中删除
                if(cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }

        }

        return cartList;
    }

    /**
     * 判断购物车列表中是否存在商家
     * 根据商家ID在购物车列表中查询购物车对象
     * @param cartList   购物车列表
     * @param sellerId   商家的id
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for(Cart cart:cartList){
            //判断商家的id是否在购物车列表中，在直接返回该商家的购物车对象信息
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据skuID在购物车明细列表中查询购物车明细对象
     * @param orderItemList  购物车明细列表（商品的图片，商品的标题....）
     * @param itemId  商品id
     * @return
     */
    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        //判断购物车对象明细列表中的商品的id和添加的商品的id是否一致，一致返回该商品，表示有商品信息
        for(TbOrderItem orderItem:orderItemList){
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        //判断数量是否合法
        if(num<=0){
            throw new RuntimeException("数量非法");
        }
        //创建新的购物车明细对象
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(  new BigDecimal(item.getPrice().doubleValue()*num) );
        return orderItem;
    }













    @Autowired
    private RedisTemplate redisTemplate;

    //从redis中获取购物车列表，有，获取，没有创建新的
    //和登录关系：如果没有登录，从cookie中获取购物车列表，如果登录从redis中获取购物车列表
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车"+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }

    //将购物车列表存放到redis中
    //和登录关系：如果没有登录，将购物车列表存储到cookie，如果登录将购物车列表存储到redis中
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis中存入购物车"+username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);

    }

    /**
     * 合并操作
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        //A[1,2,3]   B[2,3,4]   A.addAll(B)  A[1,2,3,2,3,4]
        // cartList1.addAll(cartList2);  不能简单合并
        for(Cart cart:cartList2){
            for( TbOrderItem orderItem :cart.getOrderItemList() ){
                cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

}
