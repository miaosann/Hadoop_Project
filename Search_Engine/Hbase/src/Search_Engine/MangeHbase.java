package Search_Engine;

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
import org.junit.Test;

import java.io.*;
import java.util.*;


public class MangeHbase {

    /**
     * 扫描Hbase表
     * @param table Hbase数据表
     * @param limit 限定数目
     * @throws IOException
     */
    private static void scantable(Table table, int limit,String keyWord) throws IOException {
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
    private static void insert(Table table,String keyWord,String values) throws IOException {
        //实例化Put类
        Put put = new Put(Bytes.toBytes(keyWord));
        put.addColumn(Bytes.toBytes("Bing_baike"), Bytes.toBytes("bings"), Bytes.toBytes(values));
        table.put(put);
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
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("test:BingDB2"));
        //创建列族对象
        HColumnDescriptor nohcd = new HColumnDescriptor("Bing_baike");
        //把列族加入表
        htd.addFamily(nohcd);
        //创建表
        admin.createTable(htd);
        admin.close();
    }

    /**
     * 查询年份
     * @param table Hbase表
     * @throws IOException
     */
    private static String quaryWord (Table table,String keyWord) throws IOException {
        //创建get对象，构造方法传入rk
        Get get = new Get(Bytes.toBytes(keyWord));
        //通过Table的get方法获取结果集
        Result result = table.get(get);
        //根据列族，列名获取值
        byte[] r=result.getValue(Bytes.toBytes("Bing_baike"), Bytes.toBytes("bings"));
        String value = Bytes.toString(r);
        System.out.println(value);
        return value;
    }
    @Test
    public void readTxt() throws IOException {
        File file = new File("bingdb.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader buff = new BufferedReader(reader);
        String line = null;
        while ((line = buff.readLine())!=null){
            String[] items = line.split("\\s+");
            String keyWord = items[0];
            String bings = items[1];
            //insert(table,keyWord,bings);
            System.out.println("key："+keyWord+" bings："+bings);
        }
    }
    public static void main(String[] args) {

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

            //createTable(connection);

            //根据表名获取表
            Table table = connection.getTable(TableName.valueOf("test:BingDB2"));

            File file = new File("bingdb.txt");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"UTF-8");
            BufferedReader buff = new BufferedReader(reader);
            String line = null;
            while ((line = buff.readLine())!=null){
                String[] items = line.split("\\s+");
                String keyWord = items[0];
                String bings = items[1];
                insert(table,keyWord,bings);
                System.out.println("key："+keyWord+" bings："+bings);
            }


            //insert(table,"哈哈哈","urlPOUYYYY");
            String result = quaryWord(table,"之");

            //insert(table);

            //scantable(table,1,"之");

            //转换并进行排序
            Map<String, String> map = new TreeMap<String, String>();
            String[] values = result.split(";");
            for (String item : values){
                String[] urlNum = item.split("::");
                String url = urlNum[0];
                String num = urlNum[1];
                map.put(url,num);
            }
            Map<String, String> sortedMap = sortMapByValue(map);
            System.out.println(sortedMap);
            table.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }
    static class MapKeyComparator implements Comparator<String>{

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }
    public static Map<String, String> sortMapByValue(Map<String, String> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(
                oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, String>> iter = entryList.iterator();
        Map.Entry<String, String> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }
    static class MapValueComparator implements Comparator<Map.Entry<String, String>> {

        @Override
        public int compare(Map.Entry<String, String> me1, Map.Entry<String, String> me2) {

            return -(me1.getValue().compareTo(me2.getValue()));
        }
    }
}
