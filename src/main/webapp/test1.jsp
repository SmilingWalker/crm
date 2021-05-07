<%--
  Created by IntelliJ IDEA.
  User: 廖成
  Date: 2021/4/28
  Time: 17:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>

<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
    <script>
        $.ajax({
            url:,
            type:,
            dataType:,
            data:{
            },
            success:function (data) {
            }
        })
    </script>
</head>
<body>


</body>
</html>
