<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style >
        #names{font-family: 仿宋;}

    </style>
</head>
<body>
  <h1>登陆页面</h1>
  <form action="user/login.do" method="post">
    用户名：<input type="text" name="name" id="names"><br/>
    密码：   <input type="text" name="password" id="pwd"><br/>
    <button type="submit">登陆</button>
      ${msg}
  </form>
</body>
</html>
