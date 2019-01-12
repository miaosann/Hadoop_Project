package Servlet;

import Util.HbaseUtil;
import Util.Listener;
import Util.nearbyWords;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(name = "SearchServlet",urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String searchContent = request.getParameter("searchContent");
        System.out.println("searchContent："+searchContent);
        request.getSession().setAttribute("searchContent",searchContent);
        Map<String,String> map = null;
        map = HbaseUtil.search(Listener.table,100,searchContent);

        //推荐
        List<String> nearbywords = nearbyWords.nearbys(searchContent);
        int n = nearbywords.size();
        Map<String,String> nearbyMap = new TreeMap();
        Map<String,String> temp =null;
        for (int i = 1;i < n ; i++){
            String item = nearbywords.get(i);
            temp = HbaseUtil.search(Listener.table,100,item);
            if (temp == null){
                String gbk = URLEncoder.encode(item,"GBK");
                nearbyMap.put(item,"https://www.baidu.com/s?wd="+gbk);
            }else{
                String nearUrl = null;
                for (String nearKey : temp.keySet()){
                    nearUrl = nearKey.split("::")[0];
                    break;
                }
                System.out.println("nearUrl："+nearUrl);
                nearbyMap.put(item,nearUrl);
            }
        }
        request.getSession().setAttribute("nearbys",nearbyMap);
        //结束推荐

        System.out.println("map："+map);
        String GBK = URLEncoder.encode(searchContent,"GBK");
        if (map==null){
            response.sendRedirect("https://www.baidu.com/s?wd="+GBK);
        }else {
            request.getSession().setAttribute("result_map", map);
            request.getRequestDispatcher("infomation.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
