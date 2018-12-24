import com.huaban.analysis.jieba.JiebaSegmenter;
import java.io.*;
import java.util.List;

public class jieba {

	/**
     * 读取txt内容,并将爬虫数据处理成多个文件txt
     * @param path 路径
     * @throws IOException
     */
    public void convertTxt(String path) throws IOException {
        File file = new File(path);
        // 建立一个输入流对象reader
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(file),"UTF-8" //此处编码，防止读入中文乱码
        );
        // 建立一个对象，它把文件内容转成计算机能读懂的语言
        BufferedReader buff = new BufferedReader(reader);
        String line = null;
        while ((line = buff.readLine()) != null) {
            System.out.println(line);
            String[] item = line.split(" \\*\\*\\*\\* ");
            String txtName = "bingdb/try_"+item[0]+".txt";
            String url = item[1];
            String words = item[2];
            writeTxt(txtName,url+" **** "+item[0]+" **** "+words);
        }
        buff.close();
    }

    /**
     * 读取txt内容,并将分词好的内容写回txt
     * @param path 路径
     * @throws IOException
     */
    public void resultTxt(String path) throws IOException {
        File file = new File(path);
        // 建立一个输入流对象reader
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(file),"GBK" //此处编码，防止读入中文乱码
        );
        // 建立一个对象，它把文件内容转成计算机能读懂的语言
        BufferedReader buff = new BufferedReader(reader);
        String line = null;
        while ((line = buff.readLine()) != null){
            if (!line.equals("")){
                System.out.println("line："+line);
                String[] item = line.split("\\*\\*\\*\\*\\*");
//                String title = item[0];
//                String url = item[1];
//                String words = item[2];
                List<String> wordList = splitWords(item[2]);
                String words = String.join(",",wordList);
                String newItem = item[0]+" **** "+item[1]+" **** "+words;
                //writeTxt(newItem);
            }
        }
        buff.close();
    }

    /**
     * 写入txt，换行且不覆盖
     * @param str 写入内容
     * @throws IOException
     */
    public void writeTxt(String path,String str) throws IOException {
        //String path = "BINGDB1.txt";
        File file = new File(path);
        if (!file.exists())
            file.createNewFile();
        //第二个参数表示是否在文件原内容上追加，默认是否，会把原文件的内容直接覆盖
        FileWriter fileWriter = new FileWriter(file,true);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        writer.write(str);
        writer.newLine();
        writer.flush();
        writer.close();
    }

    /**
     * 分词
     * @param words 传入的句子
     * @return 返回一个分词后的list
     */
    public List<String> splitWords(String words) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> sentences = segmenter.sentenceProcess(words);
        //System.out.println(sentences);
        return sentences;
    }

    public static void main(String[] args){
        jieba jieba = new jieba();
        try {
            //jieba.resultTxt("bingdb.txt");
            jieba.convertTxt("BINGDB1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
