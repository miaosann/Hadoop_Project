package c5;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import utils.AirlineDataUtils;

import java.io.IOException;

public class SelectClauseMRJob {
    public static class selectMap extends Mapper<LongWritable, Text, NullWritable, Text> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            System.out.println("value："+value);
            if (!AirlineDataUtils.isHeader(value)){ //如果不是header的话
                //把切分好的数组转为String，使用‘，’作为切割符号
                StringBuilder output = AirlineDataUtils.mergeStringArray(
                        AirlineDataUtils.getSelectResultsPerRow(value),//获取需要属性的数组
                        ","
                );
                System.out.println(output);
                context.write(NullWritable.get(),new Text(output.toString()));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir","D:\\BigData_jar\\hadoop-2.7.6");
        Configuration conf = new Configuration();
        Job job = Job.getInstance();
        job.setJarByClass(SelectClauseMRJob.class);
        job.setJobName("select");
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(selectMap.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.23.129:9000/C5input"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\Flights\\Select_Output"));
        job.waitForCompletion(true);
    }
}
