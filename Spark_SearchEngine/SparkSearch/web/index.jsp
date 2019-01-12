<%--
  Created by IntelliJ IDEA.
  User: miaohualin
  Date: 2019/1/3 0003
  Time: 16:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
  <title>Bing</title>
  <link rel="stylesheet" type="text/css" href="css/default.css">
  <link rel="stylesheet" type="text/css" href="css/search-form.css">
</head>
<body>
<form action="/search" , method="post" class="Searchform">
  <div class="search-wrapper">
    <div class="input-holder">
      <input type="text" class="search-input" placeholder="Type to search" name="searchContent" />
      <button type="submit" class="search-icon" onclick="searchToggle(this, event);"><span></span></button>
    </div>
    <span class="close" onclick="searchToggle(this, event);"></span>
    <div class="result-container">
    </div>
  </div>
</form>
<script src="js/jquery-1.11.0.min.js" type="text/javascript"></script>
<script type="text/javascript">
    function searchToggle(obj, evt){
        var container = $(obj).closest('.search-wrapper');
        if(!container.hasClass('active')){
            container.addClass('active');
            evt.preventDefault();
        }
        else if(container.hasClass('active') && $(obj).closest('.input-holder').length == 0){
            container.removeClass('active');
            // clear input
            container.find('.search-input').val('');
            // clear and hide result container when we press close
            container.find('.result-container').fadeOut(100, function(){$(this).empty();});
            //$('#Searchform').submit();
        }
    }
    function submitFn(obj, evt){
        value = $(obj).find('.search-input').val().trim();
        html = "Yup yup! Your search text sounds like this: ";
        if(!value.length){
            _html = "Yup yup! Add some text friend :D";
        }
        else{
            _html += "<b>" + value + "</b>";
        }
        $(obj).find('.result-container').html('<span>' + _html + '</span>');
        $(obj).find('.result-container').fadeIn(100);
        evt.preventDefault();
    }
</script>
</body>
</html>
