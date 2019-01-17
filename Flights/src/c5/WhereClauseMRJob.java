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

public class WhereClauseMRJob {
    public static class whereMap extends Mapper<LongWritable, Text, NullWritable,Text>{
        private int delayInMinutes = 10;
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (AirlineDataUtils.isHeader(value)) {
                return;
            }
            String[] arr = AirlineDataUtils.getSelectResultsPerRow(value);
            String depDel = arr[8];//起飞延迟时间
            String arrDel = arr[9];//到达延迟时间
            //将String类型的时间转变为int型，如果异常则返回0
            int iDepDel = AirlineDataUtils.parseMinutes(depDel,0);
            int iArrDel = AirlineDataUtils.parseMinutes(arrDel,0);
            StringBuilder out = AirlineDataUtils.mergeStringArray(arr,",");
            if (iDepDel>=this.delayInMinutes&&iArrDel>=this.delayInMinutes){
                out.append(",").append("B");//both
                context.write(NullWritable.get(),new Text(out.toString()));
            }else if(iDepDel>=this.delayInMinutes){
                out.append(",").append("O");//Origin
                context.write(NullWritable.get(),new Text(out.toString()));
            }else if(iArrDel>=this.delayInMinutes){
                out.append(",").append("D");//Destination
                context.write(NullWritable.get(),new Text(out.toString()));
            }
        }

    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir","D:\\BigData_jar\\hadoop-2.7.6");
        Configuration conf = new Configuration();
        Job job = Job.getInstance();
        job.setJarByClass(WhereClauseMRJob.class);
        job.setJobName("Where");
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(whereMap.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.23.129:9000/C5input"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\Flights\\Where_Output"));
        job.waitForCompletion(true);
    }
}
