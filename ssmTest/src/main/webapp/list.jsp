<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

  <script>
      function dels() {
          var elementsByName = document.getElementsByName("ids");
          var arr=new Array()
          for (var i = 0; i <elementsByName.length ; i++) {
              if(elementsByName[i].checked)  {
                  console.log( elementsByName[i].value);
                  arr.push( elementsByName[i].value);
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
      <th></th>
      <th>学生id</th>
      <th>学生名称</th>
      <th>学生性别</th>
      <th>学生年龄</th>
      <th>居住地址</th>
      <th>操作</th>
    </tr>
    <c:forEach items="${list}" var="stu"  >
    <tr>
      <td><input type="checkbox" value="${stu.id}" name="ids"></td>
      <td>${stu.id}</td>
      <td>${stu.name}</td>
      <td>
          <%--  <c:if test="${stu.sex==0}">男</c:if>
       <c:if test="${stu.sex==1}">女</c:if>--%>
           ${stu.sex==0?'男':'女'}
       <%-- <input type="radio" value=" ${stu.sex==0}" name="男">
        <input type="radio" value=" ${stu.sex==1}" name="女">--%>
      </td>
      <td>${stu.age}</td>
      <td>${stu.address}</td>
      <td>
          <a  href="/student/findOne.do?id=${stu.id}">修改</a>
          &nbsp;&nbsp;
          <a  href="/student/del.do?ids=${stu.id}">删除</a>
        </td>
    </tr>
    </c:forEach>
  </table>
</body>
</html>
