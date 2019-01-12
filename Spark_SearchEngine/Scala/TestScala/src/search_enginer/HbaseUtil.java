package search_enginer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.util.*;
public class HbaseUtil {
   /* private static Connection connection = null;
    private static Table table = null;
    private static Configuration config = null;*/
    /**
     * 扫描Hbase表
     * @param table Hbase数据表
     * @param limit 限定数目
     * @throws IOException
     */
    public static void scantable(Table table, int limit,String keyWord) throws IOException {
        int i=0;
        Scan scan = new Scan();
        //过滤器：RowFilter筛选出匹配的所有行
        //BinaryComparator可以筛选出具有某个行键的行
        Filter filter = new RowFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(keyWord)));
        //Filter filter = new PrefixFilter(Bytes.toBytes("01"));
        //Filter filter = new ValueFilter(CompareOp.EQUAL, new SubstringComparator("19"));

        scan.setFilter(filter);

        ResultScanner rs = table.getScanner(scan);
        for (Result result : rs) {
            if(i>limit) {
                break;
            }
            //byte[] jn=result.getValue(Bytes.toBytes("stuno"), Bytes.toBytes("year"));
            //System.out.println(Bytes.toString(jn));

            System.out.print("RowKey:["+Bytes.toString(result.getRow())+"]");
            System.out.print("\t");

            //Map<byte[],Map<byte[],byte[]>>
            Map map=result.getNoVersionMap();
            Set set = map.keySet();
            Iterator it = set.iterator();
            while(it.hasNext()) {
                byte[] cfkey = (byte[])it.next();
                System.out.print(Bytes.toString(cfkey)+":[");

                Map kvMap = (Map)map.get(cfkey);
                Set<Map.Entry> kvs = kvMap.entrySet();
                Iterator kvit = kvs.iterator();
                while(kvit.hasNext()) {
                    Map.Entry<byte[],byte[]> me = (Map.Entry)kvit.next();
                    System.out.print(Bytes.toString(me.getKey())+":"+Bytes.toString(me.getValue())+",");
                }
                System.out.print("]\t");
            }

            System.out.println("");
            i++;
        }
    }

    /**
     * 插入表中数据
     * @param table Hbase表
     * @throws IOException
     */
    public static void insert(Table table,String keyWord,String values,String url) throws IOException {
        //实例化Put类
        Put put = new Put(Bytes.toBytes(keyWord));
        put.addColumn(Bytes.toBytes("Google_baike"), Bytes.toBytes(url), Bytes.toBytes(values));
        table.put(put);
    }

    /**
     * 将SparkStreaming处理后得分词进行Hbase保存
     * @param key_values
     * @throws IOException
     */
    public static void insertFromStream(String key_values,int num) throws IOException {
        System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.6");

        //创建配置对象
        Configuration config = HBaseConfiguration.create();
        //配置zookeeper集群
        config.set("hbase.zookeeper.quorum", "192.168.217.145,192.168.217.146,192.168.217.147");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        //配置hmaster地址
        config.set("hbase.master", "192.168.217.138:16010");

        try {
            //获取hbase数据库的链接
            Connection connection = ConnectionFactory.createConnection(config);
            Table table = connection.getTable(TableName.valueOf("test:GoogleDB"));

            String[] items = key_values.split("::");
            String key = items[0];
            String url = items[1];
            String title = items[2];
            insert(table,key,url+"::"+title+"::"+String.valueOf(num),url);
            System.out.println("key："+key+"  value："+url+"::"+title+"::"+String.valueOf(num));
            table.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建Hbase表
     * @param connection Hbase数据库的链接
     * @throws IOException
     */
    private static void createTable(Connection connection) throws IOException {
        //创建数据库管理对象
        Admin admin = connection.getAdmin();// hbase表管理类
        //创建表对象
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("test:GoogleDB"));
        //创建列族对象
        HColumnDescriptor nohcd = new HColumnDescriptor("Google_baike");
        //把列族加入表
        htd.addFamily(nohcd);
        //创建表
        admin.createTable(htd);
        admin.close();
    }

    /**
     * 开局调用loading先加载好
     * @return 返回加载好得table
     */
    /*public static Table loading(){
        System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.6");

        //创建配置对象
        config = HBaseConfiguration.create();
        //配置zookeeper集群
        config.set("hbase.zookeeper.quorum", "192.168.217.145,192.168.217.146,192.168.217.147");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        //配置hmaster地址
        config.set("hbase.master", "192.168.217.138:16010");

        try {
            //获取hbase数据库的链接
            connection = ConnectionFactory.createConnection(config);
            table = connection.getTable(TableName.valueOf("test:GoogleDB"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Loading is successed!!");
        return table;
    }*/
    public static void main(String[] args){
        System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.6");

        //创建配置对象
        Configuration config = HBaseConfiguration.create();
        //配置zookeeper集群
        config.set("hbase.zookeeper.quorum", "192.168.217.145,192.168.217.146,192.168.217.147");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        //配置hmaster地址
        config.set("hbase.master", "192.168.217.138:16010");

        try {
            //获取hbase数据库的链接
            Connection connection = ConnectionFactory.createConnection(config);
            Table table = connection.getTable(TableName.valueOf("test:GoogleDB"));

            scantable(table,100,"腾讯网");
            table.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
