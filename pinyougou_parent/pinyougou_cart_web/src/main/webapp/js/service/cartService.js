app.service("cartService",function ($http) {
    //购物车列表
 this.findCartList=function () {
     return $http.get("/cart/findCartList.do");
    }

    //购物车数量的增减yu移除
    this.addGoodsToCartList=function(itemId,num){
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }



    this.sum=function(cartList){
        var totalValue={totalNum:0, totalMoney:0.00 };//合计实体
        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];//购物策车
            for(var j=0;j<cart.orderItemList.length;j++){
                var orderItem=cart.orderItemList[j];//购物车明细
                totalValue.totalNum+=orderItem.num;//累加数量
                totalValue.totalMoney+= orderItem.totalFee;//累加金额
            }
        }
        return totalValue;
    }




    this.findAddressList=function () {
     return  $http.get("address/findListByLoginUser.do")
    }

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }

});