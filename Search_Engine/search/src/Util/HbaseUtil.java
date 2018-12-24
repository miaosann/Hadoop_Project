package Util;


import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HbaseUtil {

    /**
     * 查询关键字(自带词分割过滤模糊匹配)
     * @param table Hbase中的表
     * @param keyWord 搜索的关键字
     * @return
     * @throws IOException
     */
    public static String quaryWord (Table table,String keyWord) throws IOException {
        //创建get对象，构造方法传入rk
        Get get = new Get(Bytes.toBytes(keyWord));
        //通过Table的get方法获取结果集
        Result result = table.get(get);
        //根据列族，列名获取值
        byte[] r=result.getValue(Bytes.toBytes("Bing_baike"), Bytes.toBytes("bings"));
        String value = Bytes.toString(r);
        System.out.println("former:"+value);
        if (value==null){
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
            //创建get对象，构造方法传入rk
            Get get1 = new Get(Bytes.toBytes(jieba));
            //通过Table的get方法获取结果集
            Result result1 = table.get(get1);
            //根据列族，列名获取值
            byte[] r1=result1.getValue(Bytes.toBytes("Bing_baike"), Bytes.toBytes("bings"));
            value = Bytes.toString(r1);
        }
        System.out.println("later:"+value);
        return value;

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
        Table table = connection.getTable(TableName.valueOf("test:BingDB2"));
        System.out.println("loading........");
        return table;
    }

    /**
     * 搜索
     * @param keyword 关键字
     * @param table table
     * @return 返回，map
     */
    public static Map<String,String> search (String keyword,Table table){
        HbaseUtil hbaseUtil = new HbaseUtil();
        Map<String, String> sortedMap = null;
        try {
            String result = quaryWord(table,keyword);
            System.out.println("result："+result);
            if (result!=null) {
                //转换并进行排序
                Map<String, String> map = new TreeMap<String, String>();
                String[] values = result.split(";");
                int Intnum = 0;
                for (String item : values) {
                    String[] urlNum = item.split("::");
                    String url = urlNum[0];
                    String title = urlNum[1];
                    String num = urlNum[2];
                    System.out.println(title+"："+title.contains(keyword));
                    if (title.contains(keyword)){
                        Intnum = Integer.parseInt(num)+1000000;
                        map.put(url+"::"+title, String.valueOf(Intnum));
                    }else {
                        map.put(url + "::" + title, num);
                    }
                }
                sortedMap = hbaseUtil.sortMapByValue(map);

                System.out.println("sortedMap："+sortedMap);
            }else {
                return null;
            }
            //table.close();
            //connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sortedMap;
    }
    public static void main(String[] args){
        //search("之家",Listener.configuration);
    }
}
