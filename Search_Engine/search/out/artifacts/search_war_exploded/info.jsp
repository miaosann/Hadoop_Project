<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Entity.urlTitle" %><%--
  Created by IntelliJ IDEA.
  User: miaohualin
  Date: 2018/12/23 0023
  Time: 16:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>info</title>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="css/info.css">
    <link rel="stylesheet" type="text/css" href="css/normalize.css">
    <%
        Map<String,String> map = (Map<String, String>) request.getSession().getAttribute("result_map");
        System.out.println("map："+map);
        List<urlTitle> urllist = new ArrayList();
        urlTitle urlTitle = null;
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
    <%--<style>
        body{
            background:url("img/image.jpg");
        }
    </style>--%>
</head>
<body>
<h2 style="text-align:center;margin-top:30px;">搜索结果</h2>
<div class=" background-overlay grid-overlay-" style="background-color:transparent;width:100%;margin-top:100px;margin-left:50px;">
    <ul>
        <c:forEach var="urls" items="${sessionScope.result_list}">
        <li data-postid="44769" data-animaleinbegin="bottom-in-view" data-animalename="qfyfadeInUp" data-delay="0" data-duration="" class="isotope-item qfy_item_post vc_span12 vc_span_mobile vc_span_mobile12 grid-cat-649 grid-cat-648 grid-cat-501 grid-cat-647 "
            style="max-width:99.8%;margin-bottom:10px;padding-bottom:10px;">
            <div class="itembody  itempcbody " style="">
                <div class="blog-content wf-td" style="display:block;word-break:break-all">
                    <div class="post-title" style="font-weight:normal;color:#000000;line-height:22px; vertical-align: top;  ">
                        <a data-title="true" style="color:#000000;font-size:22px;font-family:微软雅黑;line-height:22px" href=${urls.url} class="bitImageAhoverlink_title">${urls.title}</a>
                    </div>

                    <div class="entry-content post_excerpt" style="color:#8c8c8c;font-size:14px;width:70%;">
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;<strong>关键字出现次数：</strong>${urls.num} 次</p><br>
                    </div>
                </div>

            </div>
        </li>
        </c:forEach>
    </ul>
</div>

</body>
</html>
