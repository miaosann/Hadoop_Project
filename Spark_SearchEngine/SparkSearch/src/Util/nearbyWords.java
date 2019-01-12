package Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class nearbyWords {
    /**
     * 调用pthon中的近似词距离计算算法，并返回近似词
     * @param keyWord
     * @return
     */
    public static List<String> nearbys(String keyWord){
        String regEx1="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？a-zA-Z0-9]";
        Pattern p1 = Pattern.compile(regEx1);
        Matcher m1 = p1.matcher(keyWord);
        String filter1 = m1.replaceAll("").trim();
        System.out.println("filter1："+filter1);

        Process proc;
        List<String> nearbyWords = new ArrayList<>();
        List<String> wordList = new ArrayList<>();
        try {
            String[] keywords = new String[] { "python" ,"C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\SparkSearch\\src\\Util\\nearbyWords.py", filter1 };

            proc = Runtime.getRuntime().exec(keywords);// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GBK"));
            String line = null;
            int i = 0;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (i>3){
                    nearbyWords.add(line);
                }
                i++;
            }
            String regEx="[''\\[\\]]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(nearbyWords.get(0));
            String filter = m.replaceAll("").trim();

            System.out.println("filter："+filter);
            String[] words = filter.split(", ");
            for (String word : words){
                wordList.add(word);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wordList;
    }
    public static void main(String[] args) {
        nearbys("周杰伦ssasda14");
    }

}
