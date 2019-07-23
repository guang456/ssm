 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService) {
    /**
     *
     * 注册
     */

    $scope.reg = function () {
        //判断注册时两次输入的密码是否一致
        if ($scope.password != $scope.entity.password) {
            alert("两次输入的密码不一致请确认后重新输入");
            //密码不一致清空密码输入框
            $scope.password = "";
            $scope.entity.password = "";
            return;
        }

        userService.add($scope.entity,$scope.smsCode).success(
            function (response) {
                alert(response.message);
            }
        )
    }


    /*
    * 发送短信
    * */
    $scope.sendCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone == "") {
            alert("请填写手机号")
            return "";
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        )
    }

});

