//创建控制器
//$scope是连接控制器和表达式的桥梁
app.controller("brandController",function ($scope,$http,brandService,$controller) {

    $controller("baseController",{$scope:$scope});//伪继承,让baseController的scope等于当前控制器中的sccope

    //查询品牌列表
    $scope.findAll=function () {
        brandService.findAll().success(
            function (responce) {
                $scope.list=responce;
            })
    }



    //分页
    $scope.findPage=function(page,size){
        /* $http.get('../brand/findPage.do?page='+page+'&size='+size)*/
        brandService.findPage(page,size).success(
            function(response){
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            });
    }
    /*
    * 增加品牌的方法
    *保存的点击事件名改为  ng-click="save"
    * 将增加的add和修改的update方法同意的封装在save方法中,定义var变量methodName的值为add在添加的方法中拼接字符串"../brand/"+methodName+".do", $scope.entity
    * 判断entityid部位空的时候让methodName的值为update
    * */
    $scope.save = function () {
        var object=null;//方法名
        if ($scope.entity.id!=null){
            object= brandService.update($scope.entity);
        }else {
            object= brandService.add($scope.entity);
        }
        /*$http.post("../brand/"+methodName+".do", $scope.entity)*/
        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//如果添加成功刷新
                } else {
                    alert(response.message);//失败就弹出消息
                }
            } )
    }

    /*
    * 根据id查询实体
    * response变量代表着被封装的数值集合
    * */
    $scope.findOne=function (id) {
        /*$http.get("../brand/findOne.do?id="+id)*/
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    }

    /*
* 删除
*
* */
    $scope.del=function () {
        /*$http.get("../brand/delete.do?ids="+$scope.selectIds)*/
        brandService.del($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//如果添加成功刷新
                } else {
                    alert(response.message);//失败就弹出消息
                }
            }
        );
    }
    /*
    * 根据条件分页查询
    *
    * */
    //如果是条件查询searchEntity有值，如果不是条件查询searchEntity是没有值的，但是也是不能为null
    $scope.searchEntity={};//初始化数据，避免传递服务器的数据为null而报错
    $scope.search=function (page, size) {
        /* $http.post('../brand/search.do?page='+page+'&size='+size,$scope.searchEntity)*/
        brandService.search(page,size,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            });
    }


});