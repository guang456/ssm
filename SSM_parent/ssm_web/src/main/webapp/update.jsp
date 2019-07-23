<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
  <h1>修改页面</h1>
  <form action="/student/update.do" method="post">
      <input type="hidden" name="id" value="${stu.id}"><br/>
      学生姓名：<input type="text" name="name" value="${stu.name}"><br/>
      学生年龄：<input type="text" name="age" value="${stu.age}"><br/>
      学生性别：<input type="text" name="sex" value="${stu.sex}"><br/>
      学生班级：<input type="text" name="classhome" value="${stu.classhome}"><br/>
      学生地址：<input type="text" name="address" value="${stu.address}"><br/>
      <button type="submit">修改</button>
  </form>
</body>
</html>
