<%@ page import="java.util.Map" %>
<%@ page import="Entity.urlTitle" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: miaohualin
  Date: 2018/12/24 0024
  Time: 15:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>info</title>
    <meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0" name="viewport" />
    <meta content="yes" name="apple-mobile-web-app-capable" />
    <meta content="black" name="apple-mobile-web-app-status-bar-style" />
    <meta content="telephone=no" name="format-detection" />
    <link href="css/style.css" rel="stylesheet" type="text/css" />
</head>
<%
    Map<String,String> map = (Map<String, String>) request.getSession().getAttribute("result_map");
    System.out.println("map："+map);
    List<urlTitle> urllist = new ArrayList();
    urlTitle urlTitle = null;
    String searchContent = (String) request.getSession().getAttribute("searchContent");
    for (String url : map.keySet()){
        urlTitle = new urlTitle();
        System.out.println(url);
        String num = map.get(url);
        String[] urlTitles = url.split("::");
        urlTitle.setUrl(urlTitles[0]);
        urlTitle.setTitle(urlTitles[1]);
        urlTitle.setNum(num);
        urllist.add(urlTitle);
    }
    System.out.println("list："+urllist);
    request.getSession().setAttribute("result_list",urllist);
%>
<body>



<section class="aui-flexView" style="margin-bottom: 30px;margin-top:10px;">
    <header class="aui-navBar aui-navBar-fixed b-line">
        <a href="index.jsp" class="aui-navBar-item">
            <i class="icon icon-return"></i>
        </a>
        <div class="aui-center aui-center-clear">
            <div class="aui-search-result">
                <form class="aui-search-result-box" method="post" action="/search">
                    <input type="text" placeholder="搜索从这里开始...." name="searchContent">
                    <i class="icon icon-search-tn"></i>
                </form>
            </div>
        </div>

    </header>
    <section class="aui-scrollView" style="width:90%;margin-left:30px;">
        <div class="aui-search-list">
            <div class="aui-search-hots">
                <h2> <i class="icon icon-hots"></i> 搜索趋势</h2>
            </div>
            <%
                int i = 1;
                request.getSession().setAttribute("i",i);
            %>
            <c:forEach var="urls" items="${sessionScope.result_list}">
                <a href="${urls.url}" class="aui-flex b-line">
                    <c:if test="${i<=3}">
                        <div class="aui-flex-head red"><%=i%></div>
                    </c:if>
                    <c:if test="${i>3}">
                        <div class="aui-flex-head"><%=i%></div>
                    </c:if>
                    <%
                        i++;
                        request.getSession().setAttribute("i",i);
                    %>
                    <div class="aui-flex-box">
                        <h2>${urls.title} <em>关键字次数：${urls.num}次</em></h2>
                    </div>
                </a>
            </c:forEach>

        </div>
    </section>
</section>

</body>

</html>
