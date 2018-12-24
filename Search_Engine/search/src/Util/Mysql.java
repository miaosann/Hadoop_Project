package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mysql {
    private static String url = "jdbc:mysql://202.199.1.42:3306/hbase?useUnicode=true&characterEncoding=utf8";
    private static final String user = "root";
    private static final String sqlPassword = "miaohualin";
    private static Connection con = null;

    /**
     * 单例模式
     */
    private Mysql() {
    }

    private static void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, sqlPassword);

            if (con.isClosed()) {
                System.exit(1);
                System.out.println("database connet error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getCon() {
        if (con == null) {
            connect();
        }

        return con;
    }

    public static void closeCon() {
        try {
            con.close();
            con = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        connect();
        System.out.println(getCon());
    }
}
