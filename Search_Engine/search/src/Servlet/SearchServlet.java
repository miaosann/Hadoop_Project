package Servlet;

import Util.HbaseUtil;
import Util.Listener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@WebServlet(name = "SearchServlet",urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String searchContent = request.getParameter("searchContent");
        request.getSession().setAttribute("searchContent",searchContent);
        System.out.println(searchContent);
        Map<String,String> map = null;
        try {
            map = HbaseUtil.search(searchContent, Listener.table);
            for (String key :map.keySet()){
                if(key.contains(searchContent)){
                   int Keynum = Integer.parseInt(map.get(key))-1000000;
                   map.put(key, String.valueOf(Keynum));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(map);
        String GBK = URLEncoder.encode(searchContent,"GBK");
        if (map==null){
            response.sendRedirect("https://www.baidu.com/s?wd="+GBK);
            //System.out.println("https://www.baidu.com/s?wd="+searchContent+"&ie=utf-8&tn=91003609_hao_pg");
        }else {
            request.getSession().setAttribute("result_map", map);
            request.getRequestDispatcher("infomation.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

}
