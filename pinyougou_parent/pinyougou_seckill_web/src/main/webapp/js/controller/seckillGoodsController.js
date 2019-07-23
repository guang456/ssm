app.controller("seckillGoodsController",function ($scope, $location,$interval,seckillGoodsService) {
    $scope.findList=function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.seckillList=response;
            }
        )
    }
//查询商品
    $scope.findOne=function () {
//获取参数id
        var id = $location.search()["id"];
        seckillGoodsService.findOne(id).success(
            function (response) {
                $scope.entity=response;

                //开始倒计时
                //1:获取当前日期的毫秒值
                allSecond=Math.floor((new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000);
                time= $interval(function () {
                    allSecond=allSecond-1;
                 $scope.timeString=convertTimeString(allSecond)
                    if (allSecond<=0) {
                        $interval.cancel(time);//结束倒计时cancel
                    }
                },1000);///每次减一秒
            }
        )
    }

    /*
    小demo:时间倒计时练习
    采用angularjs里的$interval来做
    $scope.second=10;
   time= $interval(function () {
        $scope.second=$scope.second-1;
        if ($scope.second<=0) {
            $interval.cancel(time);//结束倒计时cancel
        }
    },1000);*///每次减一秒


    //转换秒为 天小时分钟秒格式 XXX 天 10:22:33   allSecond:总秒数
     convertTimeString=function (allSecond) {
        var days=Math.floor(allSecond/(60*60*24)); //天数
        var hours=Math.floor(  (allSecond-days*60*60*24)/(60*60)  );//小时数
        var minutes=Math.floor(  (allSecond-days*60*60*24-hours*60*60)/60 );//分钟数
        var seconds=allSecond - days*60*60*24 - hours*60*60 - minutes*60;//秒数

        var timeString="";
        if (days>0){
            timeString=days+"天";
        }
        return timeString+hours+":"+minutes+":"+seconds;

    }
//提交订单
    $scope.submitOrder=function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
                if (response.success){
                    alert("请在五分钟之内完成支付")
                    location.href="pay.html";//如果成功跳转支付页面
                } else {
                    alert(response.message)
                }
            }
        )
    }




})