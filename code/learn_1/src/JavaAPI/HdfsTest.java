package JavaAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HdfsTest {

    /***
     * 读取文件
     * @param path 文件的详细路径
     * @throws IOException
     */
    public static void readFile(String path) throws IOException{
        //获取配置
        Configuration conf = new Configuration();
        //配置
        conf.set("fs.defaultFS","hdfs://192.168.217.138:9000");
        //获取hdfs文件系统的操作对象
        FileSystem fs = FileSystem.get(conf);
        //具体对文件的操作
        FSDataInputStream fis = fs.open(new Path(path));
        IOUtils.copyBytes(fis,System.out,4096,true);
        fis.close();

    }

    /***
     * 下载文件
     * @param path 文件的详细路径
     * @throws Exception
     */
    public static void downloadFile(String path) throws Exception{
        //获取配置
        Configuration conf = new Configuration();
        //获取hdfs文件系统的操作对象
        FileSystem fs = FileSystem.get(new URI("hdfs://192.168.217.138:9000"),conf,"root");
        //具体对文件的操作
        FSDataInputStream fis = fs.open(new Path(path));
        OutputStream out = new FileOutputStream(new File("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\readme.txt"));
        IOUtils.copyBytes(fis,out,4096,true);
        fis.close();
        out.close();
    }

    /***
     * 上传文件
     * @throws Exception
     */
    public static void uploadFile() throws Exception{
        //获取配置
        Configuration conf = new Configuration();
        //获取hdfs文件系统的操作对象
        FileSystem fs = FileSystem.get(new URI("hdfs://192.168.217.138:9000"),conf,"root");
        //具体对文件的操作
        fs.copyFromLocalFile(new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\uploadfile.txt"),new Path("/input"));
        System.out.println("finished...");
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir","D:\\hadoop-2.7.6");
        readFile("/input/uploadfile.txt");
        //downloadFile("/input/README.txt");
        //uploadFile();
    }

}
