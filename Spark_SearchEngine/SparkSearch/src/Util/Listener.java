package Util;

import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

public class Listener {
    /**
     * 用于监听，在Tomcat服务启动时随之加载，
     * 以减少以后的加载，提升Hbase的用户体验
     */
    public static Table table = null;
    static {
        try {
            table = HbaseUtil.loading();
            //HbaseUtil.search(null,table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("configration success!!");
    }


}