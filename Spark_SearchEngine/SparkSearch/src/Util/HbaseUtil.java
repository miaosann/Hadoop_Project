package Util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HbaseUtil {
    /**
     * 监听器自动加载函数
     * @return 返回table
     * @throws IOException
     */
    public static Table loading () throws IOException {
        System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.6");

        //创建配置对象
        Configuration config = HBaseConfiguration.create();
        //配置zookeeper集群
        config.set("hbase.zookeeper.quorum", "192.168.217.145,192.168.217.146,192.168.217.147");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        //配置hmaster地址
        config.set("hbase.master", "192.168.217.138:16010");
        //获取hbase数据库的链接
        Connection connection = ConnectionFactory.createConnection(config);
        //根据表名获取表
        Table table = connection.getTable(TableName.valueOf("test:GoogleDB"));
        System.out.println("loading........");
        return table;
    }

    /**
     * 扫描Hbase表,查询关键字
     * @param table Hbase数据表
     * @param limit 限定数目
     * @throws IOException
     */
    private static Map<String,String> scantable(Table table, int limit,String keyWord) throws IOException {
        int i=0;
        Scan scan = new Scan();
        //过滤器：RowFilter筛选出匹配的所有行
        //BinaryComparator可以筛选出具有某个行键的行
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(keyWord)));
        //Filter filter = new PrefixFilter(Bytes.toBytes("01"));
        //Filter filter = new ValueFilter(CompareOp.EQUAL, new SubstringComparator("19"));

        scan.setFilter(filter);

        ResultScanner rs = table.getScanner(scan);
        Map<String,String> treemap = new TreeMap<String, String>();
        Map<String,String> sortedMap = null;
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
                    String valueStr = Bytes.toString(me.getValue());
                    String[] values = valueStr.split("::");
                    String url = values[0];
                    String title = values[1];
                    String num = values[2];
                    treemap.put(url+"::"+title,num);
                    System.out.print(Bytes.toString(me.getKey())+":"+Bytes.toString(me.getValue())+",");
                }
                System.out.print("]\t");
            }

            System.out.println("");
            i++;
        }
        sortedMap = sortMapByValue(treemap);
        System.out.println("sortedMap："+sortedMap);
        return sortedMap;
    }

    /**
     * 使用关键字双次检索加切词的方式实现最佳匹配
     * @param table
     * @param limit
     * @param keyWord
     * @return
     * @throws IOException
     */
    public static Map<String,String> search(Table table, int limit,String keyWord) throws IOException {
        Map<String,String> result = scantable(table,limit,keyWord);
        if (result==null){
            String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？a-zA-Z0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(keyWord);
            String filter = m.replaceAll("").trim();
            System.out.println("filter："+filter);
            JiebaSegmenter segmenter = new JiebaSegmenter();
            List<String> sentences = segmenter.sentenceProcess(filter);
            System.out.println(sentences);
            Map<String,String> wordsMap = new TreeMap<String, String>();
            if (sentences.size()>0) {
                for (String word : sentences){
                    wordsMap.put(word, String.valueOf(word.length()));
                }
            }
            String jieba = null;
            Map<String,String> sortedMap = sortMapByValue(wordsMap);
            System.out.println(sortedMap);
            for (String key : sortedMap.keySet()){
                jieba = key;
                break;
            }
            System.out.println("jieba："+jieba);
            result = scantable(table,limit,jieba);
        }
        System.out.println("result："+result);
        return result;
    }

    /**
     * 按照Value进行排序
     * @param oriMap 需要排序的map
     * @return
     */
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

            return -(Integer.parseInt(me1.getValue())-Integer.parseInt(me2.getValue()));
        }
    }
}
