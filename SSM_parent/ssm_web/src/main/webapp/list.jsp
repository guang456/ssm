<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
  <script>

    function deleteAll() {
        var elementsByName = document.getElementsByName("ids");
        for (var i = 0; i <elementsByName.length ; i++) {
            if(elementsByName[i].checked){
                elementsByName[i].removeAttribute("checked");
            }else {
                elementsByName[i].setAttribute("checked","checked");
            }

        }

    }


    function dels() {
        var elementsByName = document.getElementsByName("ids");
        var arr=new Array();
        for (var i = 0; i <elementsByName.length ; i++) {
            if(elementsByName[i].checked){
                arr.push(elementsByName[i].value)
            }
        }
        location.href="${pageContext.request.contextPath}/student/del.do?ids="+arr;
    }
  </script>
  
</head>
<body>
  <h1>学生信息列表</h1>
  <a href="/add.jsp">新增</a>     
  &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;
  <button onclick="dels()" >批量删除</button>
  
  <table border="1" cellspacing="0">
    <tr>
      <th><input type="checkbox" onclick="deleteAll()"></th>
      <th>学生id</th>
      <th>学生名称</th>
      <th>学生年龄</th>
      <th>学生性别</th>
      <th>学生班级</th>
      <th>居住地址</th>
      <th>操作</th>
    </tr>
    <c:forEach items="${list}"  var="stu">
    <tr>
      <td><input type="checkbox" name="ids" value="${stu.id}"></td>
      <td>${stu.id}</td>
      <td>${stu.name}</td>
      <td>${stu.age}</td>
      <td>${stu.sex}</td>
      <th>${stu.classhome}</th>
      <td>${stu.address}</td>
      <td>
          <a  href="findOne.do?id=${stu.id}">修改</a>
          &nbsp;&nbsp;
          <a  href="/student/del.do?ids=${stu.id}">删除</a>
        </td>
    </tr>
    </c:forEach>
  </table>
</body>
</html>
