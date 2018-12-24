package Dao;

import Util.Mysql;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class uploadDao {
    /**
     * 将txt读取上传至mysql数据库
     * @throws SQLException
     * @throws IOException
     */
    public static void upload() throws SQLException, IOException {
        File file = new File("bingdb.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader buff = new BufferedReader(reader);
        String line = null;
        while ((line = buff.readLine())!=null){
            System.out.println(line);
            String[] items = line.split("\\s+");
            String keyWord = items[0];
            String bings = items[1];
            insert(keyWord,bings);
        }
    }

    /**
     * 插入mysql表中数据
     * @param row_key
     * @param bings
     * @throws SQLException
     */
    public static void insert(String row_key,String bings) throws SQLException {
        Connection connection = Mysql.getCon();
        String sql = "insert into bingdb2 values (?,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1 , row_key);
        preparedStatement.setString(2 , bings);
        preparedStatement.executeUpdate();
        Mysql.closeCon();
    }

    /**
     * mysql中模糊检索
     * @param keyword
     * @return
     * @throws SQLException
     */
    public static List<String> query(String keyword) throws SQLException {
        Connection connection = Mysql.getCon();
        String sql = "select row_key,bings from bingdb2 where row_key like '"+keyword+"';";
        //System.out.println(sql);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        List<String> urls = new ArrayList<String>();
        while (rs.next()) {
            String row_key = rs.getString("row_key");
            String bings = rs.getString("bings");
            urls.add(bings);
        }
        Mysql.closeCon();
        return urls;
    }

    public static void main(String[] args){
        try {
           // upload();
            List<String> result = query("之");
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
