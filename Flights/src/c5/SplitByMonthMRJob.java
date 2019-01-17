package c5;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import utils.AirlineDataUtils;

import java.io.IOException;

public class SplitByMonthMRJob {
    public static class SplitByMonthMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (!AirlineDataUtils.isHeader(value)) {
                int month = Integer.parseInt(AirlineDataUtils.getMonth(value
                        .toString().split(",")));
                context.write(new IntWritable(month), value);
            }
        }
        public static class SplitByMonthReducer extends Reducer<IntWritable, Text, NullWritable, Text> {
            public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
                for (Text output : values) {
                    context.write(NullWritable.get(), new Text(output.toString()));
                }
            }
        }
        //计划使用12个reducer，每个reducer的索引为month-1
        public static class MonthPartioner extends Partitioner<IntWritable, Text> {
            @Override
            public int getPartition(IntWritable month, Text value, int numPartitions) {
                return (month.get() - 1);
            }
        }
        public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
            System.setProperty("hadoop.home.dir","D:\\BigData_jar\\hadoop-2.7.6");
            Configuration conf = new Configuration();
            Job job = Job.getInstance();
            job.setJarByClass(WhereClauseMRJob.class);
            job.setJobName("Split");

            job.setMapperClass(SplitByMonthMapper.class);
            job.setMapOutputKeyClass(IntWritable.class);
            job.setMapOutputValueClass(Text.class);

            job.setReducerClass(SplitByMonthReducer.class);
            job.setNumReduceTasks(12);
            job.setPartitionerClass(MonthPartioner.class);

            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TextOutputFormat.class);
            FileInputFormat.addInputPath(job, new Path("hdfs://192.168.23.129:9000/C5input"));
            FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\Flights\\Split_Output"));
            job.waitForCompletion(true);
        }
    }
}
