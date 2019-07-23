 //控制层 
 app.controller('goodsController' ,function($scope,$controller ,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id=$location.search()["id"];
		if (id == null) {
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
					//商品 介绍
				editor.html($scope.entity.goodsDesc.introduction);
				//商品图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//规格选择
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//转换sku
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
			}
		);				
	}


	
	//保存 
     //保存
     $scope.save=function(){
//提取文本编辑器的值
         $scope.entity.goodsDesc.introduction=editor.html();
         var serviceObject;//服务层对象
         if($scope.entity.goods.id!=null){//如果有 ID
             serviceObject=goodsService.update( $scope.entity ); //修改
         }else{
             serviceObject=goodsService.add( $scope.entity );//增加
         }
         serviceObject.success(
             function(response){
                 if(response.success){
                     alert('保存成功');
                   location.href="goods.html";
                 }else{
                     alert(response.message);
                 }
             }
         );
     }


	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//上传图片
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
				if (response.success) {
					$scope.image_entity.url=response.message;
				}else {
					alert(response.message);
				}
            });
    }
		$scope.entity={goodsDesc:{itemImages:[] ,specificationItems:[]}}
    //增加图片
	 $scope.add_image_entity=function () {
		 $scope.entity.goodsDesc.itemImages.push($scope.image_entity)
     }

     //移除图片
     $scope.remove_image_entity=function(index){
         $scope.entity.goodsDesc.itemImages.splice(index,1);
     }

     //获取1级下拉列表
	 $scope.selectItemCat1List=function () {
		//当id为1的时候代表的是一级数据
		 itemCatService.findByParentId(0).success(
		 	function (respones) {
				$scope.itemCat1List=respones;
            }
		 )
     }
     //查询二级商品分类列表

	 $scope.$watch("entity.goods.category1Id",function (newValue, oldValue) {
		 itemCatService.findByParentId(newValue).success(
		 	function (response) {
				$scope.itemCat2List=response;
            }
		 )
     })

	 //查询三级商品目录列表

	 $scope.$watch("entity.goods.category2Id",function (newValue, oldValue) {
		 itemCatService.findByParentId(newValue).success(
		 	function (response) {
				$scope.itemCat3List=response;
            }
		 )
     })

	 //查询模块id
	 $scope.$watch("entity.goods.category3Id",function (newValue, oldValue) {
		 itemCatService.findOne(newValue).success(
		 	function (responsse) {
				$scope.entity.goods.typeTemplateId=responsse.typeId;
            }
		 )
     });

     //模板 ID 选择后 更新模板对象
     $scope.$watch('entity.goods.typeTemplateId', function(newValue, oldValue) {
         typeTemplateService.findOne(newValue).success(
             function(response){
                 $scope.typeTemplate=response;//获取类型模板
                 $scope.typeTemplate.brandIds=
                     JSON.parse( $scope.typeTemplate.brandIds);//品牌列表
				 if ($location.search()["id"]==null) {
                 $scope.entity.goodsDesc.customAttributeItems=JSON.parse( $scope.typeTemplate.customAttributeItems);//扩展属性
                 }
             }
         );
//查询规格列表
         typeTemplateService.findSpecList(newValue).success(
             function(response){
                 $scope.specList=response;
             }
         );
     });


     $scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]} };

     		$scope.updateSpecAttribute=function ($event,name,value) {
				var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
				if (object != null) {
					if ($event.target.checked ){
                        object.attributeValue.push(value);
					}else {
						//取消勾选
                        object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);
                        if (object.attributeValue.length == 0) {
                            $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
						}
					}

				}else {
					$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
				}
            }



            //创建sku列表
	 $scope.createItemList=function () {
				//列表初始化
         $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];
     			//定义一个用户选择的变量
     			var items=	$scope.entity.goodsDesc.specificationItems;
     		//循环遍历
         for (var i = 0; i < items.length; i++) {
             $scope.entity.itemList=addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
         }
     }
     addColumn=function(list,columnName,conlumnValues){
         var newList=[];//新的集合
         for(var i=0;i<list.length;i++){
             var oldRow= list[i];
             for(var j=0;j<conlumnValues.length;j++){
                 var newRow= JSON.parse( JSON.stringify( oldRow ) );//深克隆
                 newRow.spec[columnName]=conlumnValues[j];
                 newList.push(newRow);
             }
         }
		 return newList;
     }

		//对应审核状态   0       1          2             3
     $scope.status=["未审核","已审核","审核未通过","已关闭"];


     		//获取对应的三级目录商品名称
	 $scope.itemCatList=[];
	 $scope.findItemCatList=function () {
		 itemCatService.findAll().success(
		 	function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
		 )
     }

     //判断规格于规格选项是否被勾选
     $scope.checkAttributeValue=function (specName, optionName) {

	 	var items=$scope.entity.goodsDesc.specificationItems;
	 	var object=$scope.searchObjectByKey(items,"attributeName",specName);
	 	if (object!=null) {
					if (object.attributeValue.indexOf(optionName)>=0) {//如果可以查询到规格选项
						return true;
					}else {
						return false;
					}
		}else {
	 		return false;
		}
     }



});	
