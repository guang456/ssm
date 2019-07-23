package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import entity.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {


    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 从cookie中提取购物车
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取当前登陆人名称
        String username =SecurityContextHolder.getContext().getAuthentication().getName();
        //创建一个人cookie
        String cartListString = util.CookieUtil.getCookieValue(request, "cartList","UTF-8");
        if(cartListString==null || cartListString.equals("")){
            cartListString="[]";
        }
            //cookie购物车
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if(username.equals("anonymousUser")){//如果未登录
            //从cookie中提取购物车
            System.out.println("从cookie中提取购物车");
            return cartList_cookie;
        }else{
            List<Cart> cartList_redis =cartService.findCartListFromRedis(username);//从 redis 中提取
            if(cartList_cookie.size()>0){//如果本地购物车存在数据
                  //合并购物车
                List<Cart> cartList = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //将合并后的数据存入 redis
                cartService.saveCartListToRedis(username, cartList);
                 //清除本地 cookie 的本地购物车
                util.CookieUtil.deleteCookie(request, response, "cartList");
                System.out.println("执行合并购物车的方法");
                    return cartList;
            }
                 return cartList_redis;
        }
    }

    /**
     * 添加购物车
     */
        @RequestMapping("/addGoodsToCartList")
        //Spring4.2之后可以用注解解决跨域
        @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")

    public Result addGoodsToCartList(Long itemId,Integer num){
       /* //设置头信息;如果不访问cookie,这句话就可以
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
            //如果操作cookie必须加上本句话
            response.setHeader("Access-Control-Allow-Credentials", "true");*/

            //当前登陆人
    String name = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            //从cokie中提取购物车
            List<Cart> cartList = findCartList();
            //调用服务方法操作购物策划
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (name.equals("anonymousUser")){
                //将新的购物车存入cookie
                String cartListString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",cartListString,3600*24,"UTF-8");
                System.out.println("向 cookie 存入购物车 ");
            }else {
                cartService.saveCartListToRedis(name,cartList);

            }
           return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
          return   new Result(false,"添加失败");
        }

    }
}
