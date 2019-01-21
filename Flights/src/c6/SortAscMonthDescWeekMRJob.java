package c6;

import org.apache.hadoop.conf.Configurable;
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

public class SortAscMonthDescWeekMRJob {
    public static class SortAscMonthDescWeekMapper extends Mapper<LongWritable, Text, MonthDoWWritable, DelaysWritable> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (!AirlineDataUtils.isHeader(value)) {
                String[] contents = value.toString().split(",");
                String month = AirlineDataUtils.getMonth(contents);
                String dow = AirlineDataUtils.getDayOfTheWeek(contents);
                MonthDoWWritable mw = new MonthDoWWritable();
                mw.month = new IntWritable(Integer.parseInt(month));
                mw.dayOfWeek = new IntWritable(Integer.parseInt(dow));
                //主要为了挑选出DelaysWritable中必须的属性，减少了无用属性的传输可以有效提高IO
                DelaysWritable dw = AirlineDataUtils.parseDelaysWritable(value.toString());
                context.write(mw, dw);
            }
        }
    }

    public static class SortAscMonthDescWeekReducer extends Reducer<MonthDoWWritable, DelaysWritable, NullWritable, Text> {
        public void reduce(MonthDoWWritable key, Iterable<DelaysWritable> values, Context context) throws IOException, InterruptedException {

            for (DelaysWritable val : values) {
                //将DelaysWritable中的各个属性值，连成String输出
                context.write(NullWritable.get(), new Text(AirlineDataUtils.parseDelaysWritableToText(val)));
            }
        }
    }

    public static class MonthDoWPartitioner extends Partitioner<MonthDoWWritable, Text> implements Configurable {
        private Configuration conf = null;
        private int indexRange = 0;

        /**
         * 计算key的范围，12个月份，每周七天，从0开始一共是 12*7 =84
         * @return
         */
        private int getDefaultRange() {
            int minIndex = 0;
            int maxIndex = 11 * 7 + 6;
            int range = (maxIndex - minIndex) + 1;
            return range;
        }

        // @Override
        public void setConf(Configuration conf) {
            this.conf = conf;
            this.indexRange = conf.getInt("key.range", getDefaultRange());
        }

        //@Override
        public Configuration getConf() {
            return this.conf;
        }

        public int getPartition(MonthDoWWritable key, Text value,
                                int numReduceTasks) {
            return AirlineDataUtils.getCustomPartition(key, indexRange,
                    numReduceTasks);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir","D:\\BigData_jar\\hadoop-2.7.6");
        Configuration conf = new Configuration();
        Job job = Job.getInstance();
        job.setJarByClass(SortAscMonthDescWeekMRJob.class);
        job.setJobName("SortAscMonthDescWeek");

        job.setMapperClass(SortAscMonthDescWeekMapper.class);
        job.setMapOutputKeyClass(MonthDoWWritable.class);
        job.setMapOutputValueClass(DelaysWritable.class);

        job.setReducerClass(SortAscMonthDescWeekReducer.class);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        job.setPartitionerClass(MonthDoWPartitioner.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.23.129:9000/C5input"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\Flights\\SortAscMonthDescWeek_Output"));
        job.waitForCompletion(true);
    }
}
