package c5;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import utils.AirlineDataUtils;

import java.io.IOException;
import java.text.DecimalFormat;

public class AggregationMRJob {
    public static final IntWritable RECORD=new IntWritable(0);
    public static final IntWritable ARRIVAL_DELAY=new IntWritable(1);
    public static final IntWritable ARRIVAL_ON_TIME=new IntWritable(2);
    public static final IntWritable DEPARTURE_DELAY=new IntWritable(3);
    public static final IntWritable DEPARTURE_ON_TIME=new IntWritable(4);
    public static final IntWritable IS_CANCELLED=new IntWritable(5);
    public static final IntWritable IS_DIVERTED=new IntWritable(6);

    public static class aggregationMap extends Mapper<LongWritable, Text,Text, IntWritable>{
        public void map(LongWritable key,Text value,Context context) throws IOException, InterruptedException {
            if (!AirlineDataUtils.isHeader(value)){
                String[] contents = value.toString().split(",");
                String month = AirlineDataUtils.getMonth(contents);
                //通过左侧填充0，将格式确定,然后转为int
                int arrivalDelay = AirlineDataUtils.parseMinutes(
                        AirlineDataUtils.getArrivalTime(contents),0
                );
                int departureDelay = AirlineDataUtils.parseMinutes(
                        AirlineDataUtils.getDepartureTime(contents),0
                );
                boolean isCancelled = AirlineDataUtils.parseBoolean(
                        AirlineDataUtils.getCancelled(contents),false
                );
                boolean isDiverted = AirlineDataUtils.parseBoolean(
                        AirlineDataUtils.getDiverted(contents),false
                );
                context.write(new Text(month), RECORD); //计算每月航班的总数量
                if(arrivalDelay>0){
                    context.write(new Text(month), ARRIVAL_DELAY); //到达延迟
                }else{
                    context.write(new Text(month), ARRIVAL_ON_TIME); //准时到达
                }
                if(departureDelay>0){
                    context.write(new Text(month), DEPARTURE_DELAY); //起飞延迟
                }else{
                    context.write(new Text(month), DEPARTURE_ON_TIME); //准时起飞
                }
                if(isCancelled){
                    context.write(new Text(month), IS_CANCELLED); //航班取消
                }
                if(isDiverted){
                    context.write(new Text(month), IS_DIVERTED); //航班改航
                }
            }
        }
    }

    public static class aggregationReduce extends Reducer<Text, IntWritable, NullWritable, Text>{
        public void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
            double totalRecords = 0;
            double arrivalOnTime = 0;
            double arrivalDelays = 0;
            double departureOnTime = 0;
            double departureDelays = 0;
            double cancellations = 0;
            double diversions = 0;
            for (IntWritable v : values){
                if(v.equals(RECORD)){
                    totalRecords++;
                }
                if(v.equals(ARRIVAL_ON_TIME)){
                    arrivalOnTime++;
                }
                if(v.equals(ARRIVAL_DELAY)){
                    arrivalDelays++;
                }
                if(v.equals(DEPARTURE_ON_TIME)){
                    departureOnTime++;
                }
                if(v.equals(DEPARTURE_DELAY)){
                    departureDelays++;
                }
                if(v.equals(IS_CANCELLED)){
                    cancellations++;
                }
                if(v.equals(IS_DIVERTED)){
                    diversions++;
                }
            }
            DecimalFormat df = new DecimalFormat("0.0000");
            StringBuilder output = new StringBuilder(key.toString());
            output.append(",").append(totalRecords);
            output.append(",").append(df.format(arrivalOnTime/totalRecords));
            output.append(",").append(df.format(arrivalDelays/totalRecords));
            output.append(",").append(df.format(departureOnTime/totalRecords));
            output.append(",").append(df.format(departureDelays/totalRecords));
            output.append(",").append(df.format(cancellations/totalRecords));
            output.append(",").append(df.format(diversions/totalRecords));
            context.write(NullWritable.get(), new Text(output.toString()));
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir","D:\\BigData_jar\\hadoop-2.7.6");
        Configuration conf = new Configuration();
        Job job = Job.getInstance();
        job.setJarByClass(AggregationMRJob.class);
        job.setJobName("aggregation");

        job.setMapperClass(aggregationMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(aggregationReduce.class);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.23.129:9000/C5input"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\Flights\\Aggregation_Output"));
        job.waitForCompletion(true);
    }
}
