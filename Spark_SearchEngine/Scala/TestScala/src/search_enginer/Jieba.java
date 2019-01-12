package search_enginer;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.ArrayList;
import java.util.List;

public class Jieba {
    public static String cutWords(String words,String keys) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
//        String[] items = words.split("\\*\\*\\*\\*");
//        String title = items[0];
//        String url = items[1];
//        String keyWords = items[2];
        List<String> sentences = segmenter.sentenceProcess(words);
        List<String> sentenceList = new ArrayList<>();
        for (String word : sentences){
            sentenceList.add(word+"::"+keys);
        }
//        String retStr = title+"::"+url+"::"+listToString(sentences,"**");
        return listToString(sentenceList,"**");
    }
    public static String listToString(List list, String separator) {
        return org.apache.commons.lang.StringUtils.join(list.toArray(), separator);
    }
    public static void main(String args[]){

    }
}
