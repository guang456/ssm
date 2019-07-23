app.controller("seckillGoodsController",function ($scope, $location,seckillGoodsService) {
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
            }
        )
    }

})