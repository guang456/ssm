app.controller("searchController",function ($scope, $location,searchService) {

    //定义搜索对象的结构
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNum':1,'pageSize':40,'sort':'','sortField':''}
    $scope.resultMap={};
    //===================================================================================================================================
    //搜索
    $scope.search=function () {

        $scope.searchMap.pageNum=parseInt(  $scope.searchMap.pageNum);//转换为数字

        searchService.search( $scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;////搜索返回的结果
              //  $scope.searchMap.pageNum=1;//每次查询后都显示第一页

                buildPageLabel(); //构建分页栏
            }
        )
    }
    //===================================================================================================================================
    //构建分页栏  pageNum 当前页
    buildPageLabel=function(){
        $scope.pageLabel=[];
        var firstPage=1;//开始的页码
        var lastPage=$scope.resultMap.totalPages;//截至的页码
        $scope.firstDot=true;//前面有点
        $scope. lastDot=true;//后边有点

        if ($scope.resultMap.totalPages>5){//如果页码数量大于5
            if ($scope.searchMap.pageNum<=3){//如果页码数量小于等于3显示前五页
                lastPage=5;
               $scope.firstDot=false;
            }else if ($scope.resultMap.pageNum>=$scope.resultMap.totalPages-2){//如果当前页大于等于总页数-2
                firstPage=$scope.resultMap.totalPages-4;
                $scope.lastDot=false;
            }else {//显示以当前页为中心的5页
                firstPage=$scope.resultMap.pageNum-2;
                lastPage=$scope.resultMap.pageNum+2;
            }
        }else {
            $scope.firstDot=false;//前面wu点
            $scope. lastDot=false;//后边wu点
        }

        //构建页码
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }



//===================================================================================================================================


    //添加搜索xian改变searMap的值
    $scope.addSearchItem=function (key, value) {
        if (key == 'category' || key == 'brand' || key=='price') {//如果用户点击的是分类或者品牌
            $scope.searchMap[key]=value;

        }else {//用户点击的是规格
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();//查询
    }
//===================================================================================================================================
    //撤销搜索条件
    $scope.removeSearchItem=function (key) {
        if (key == 'category' || key == 'brand'|| key=='price') {//如果用户点击的是分类或者品牌
            $scope.searchMap[key]="";

        }else {//用户点击的是规格
         delete $scope.searchMap.spec[key];

        }
        $scope.search();//查询
    }

    //===================================================================================================================================
        //分页查询
    $scope.queryByPage=function (pageNum) {
        //如果当前页小于1或者当前页大于总能够页数让他返回成为无效点击
        if (pageNum < 1 || pageNum > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNum=pageNum;
        $scope.search();//查询
    }

    //===================================================================================================================================
    //判断你当前页是否为第一页
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNum == 1) {
            return true;
        }else {
            return false;
        }
    }
    //判断你当前页是否为最后一页
    $scope.isEndPage=function () {
        if ($scope.searchMap.pageNum == $scope.resultMap.totalPages) {
            return true;
        }else {
            return false;
        }
    }

    //===================================================================================================================================
    //排序查询
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;

        $scope.search();
    }

    //===================================================================================================================================
    //判断关键字是否是品牌
    $scope.keywordsIsBrand=function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf( $scope.resultMap.brandList[i].text )>=0){
                return true;
            }
        } return false;
    }

    //===================================================================================================================================
    //接受关键字
    $scope.loadkeywords=function () {
       $scope.searchMap.keywords= $location.search()["keywords"];
       $scope.search();
    }
});