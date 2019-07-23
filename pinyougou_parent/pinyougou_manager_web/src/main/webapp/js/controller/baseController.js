app.controller("baseController",function ($scope) {


    //分页控件配置
    // currentPage:当前页 totalItems:总记录数  itemsPerPage每页记录数 perPageOptions分页选项
    //onChange当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();
        }
    };

    //刷新列表
    $scope.reloadList=function(){
        $scope.search( $scope.paginationConf.currentPage ,  $scope.paginationConf.itemsPerPage );
    };

    /*
      * 用户选中要删除的复选框id
      *
      * */
    //当用户进行勾选操作的时候将id存放到数组中
    //$event : 事件对象，点击事件/选中事件...
    $scope.selectIds=[];//选中的id存放的集合
    $scope.updateSelection=function ($event,id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);//采用push方法向集合中添加元素
        }else {
            var index = $scope.selectIds.indexOf(id);//查找值的位置
            $scope.selectIds.splice(index,1);//参数1:移除的位置   参数2:移除的个数
        }

    }

    $scope.jsonToString=function (jsonString,key) {
         var json=   JSON.parse(jsonString);
         var value="";
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value+=","
            }
            value+=json[i][key];
        }
         return value;
    }

})