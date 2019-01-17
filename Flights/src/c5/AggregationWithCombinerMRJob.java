package c5;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
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

public class AggregationWithCombinerMRJob {
    public static final IntWritable RECORD=new IntWritable(0);
    public static final IntWritable ARRIVAL_DELAY=new IntWritable(1);
    public static final IntWritable ARRIVAL_ON_TIME=new IntWritable(2);
    public static final IntWritable DEPARTURE_DELAY=new IntWritable(3);
    public static final IntWritable DEPARTURE_ON_TIME=new IntWritable(4);
    public static final IntWritable IS_CANCELLED=new IntWritable(5);
    public static final IntWritable IS_DIVERTED=new IntWritable(6);

    public static final IntWritable TYPE=new IntWritable(0);//代表了标记类型
    public static final IntWritable VALUE=new IntWritable(1);

    private static MapWritable getMapWritable(IntWritable type,IntWritable value){
        MapWritable map = new MapWritable();
        map.put(TYPE, type);
        map.put(VALUE, value);
        return map;
    }

    public static class aggregationMap extends Mapper<LongWritable, Text,Text, MapWritable>{
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (!AirlineDataUtils.isHeader(value)) {
                String[] contents = value.toString().split(",");
                String month = AirlineDataUtils.getMonth(contents);
                int arrivalDelay = AirlineDataUtils.parseMinutes(AirlineDataUtils.getArrivalDelay(contents), 0);
                int departureDelay = AirlineDataUtils.parseMinutes(AirlineDataUtils.getDepartureDelay(contents), 0);
                boolean isCancelled = AirlineDataUtils.parseBoolean(AirlineDataUtils.getCancelled(contents), false);
                boolean isDiverted = AirlineDataUtils.parseBoolean(AirlineDataUtils.getDiverted(contents), false);
                context.write(new Text(month), getMapWritable(RECORD,new IntWritable(1)));
                if(arrivalDelay>0){
                    context.write(new Text(month), getMapWritable(ARRIVAL_DELAY,new IntWritable(1)));
                }else{
                    context.write(new Text(month), getMapWritable(ARRIVAL_ON_TIME,new IntWritable(1)));
                }
                if(departureDelay>0){
                    context.write(new Text(month), getMapWritable(DEPARTURE_DELAY,new IntWritable(1)));
                }else{
                    context.write(new Text(month), getMapWritable(DEPARTURE_ON_TIME,new IntWritable(1)));
                }
                if(isCancelled){
                    context.write(new Text(month), getMapWritable(IS_CANCELLED,new IntWritable(1)));
                }
                if(isDiverted){
                    context.write(new Text(month), getMapWritable(IS_DIVERTED,new IntWritable(1)));
                }
            }
        }

    }
    public static class aggregationCombiner extends Reducer<Text,MapWritable,Text,MapWritable>{
        public void reduce(Text key,Iterable<MapWritable> values,Context context) throws IOException, InterruptedException {
            int totalRecords = 0;
            int arrivalOnTime = 0;
            int arrivalDelays = 0;
            int departureOnTime = 0;
            int departureDelays = 0;
            int cancellations = 0;
            int diversions = 0;
            for(MapWritable v:values){
                IntWritable type = (IntWritable)v.get(TYPE);
                IntWritable value = (IntWritable)v.get(VALUE);
                if(type.equals(RECORD)){
                    totalRecords=totalRecords+value.get();
                }
                if(type.equals(ARRIVAL_ON_TIME)){
                    arrivalOnTime=arrivalOnTime+value.get();
                }
                if(type.equals(ARRIVAL_DELAY)){
                    arrivalDelays=arrivalDelays+value.get();
                }
                if(type.equals(DEPARTURE_ON_TIME)){
                    departureOnTime=departureOnTime+value.get();
                }
                if(type.equals(DEPARTURE_DELAY)){
                    departureDelays=departureDelays+value.get();
                }
                if(type.equals(IS_CANCELLED)){
                    cancellations=cancellations+value.get();
                }
                if(type.equals(IS_DIVERTED)){
                    diversions=diversions+value.get();
                }
            }
            context.write(key, getMapWritable(RECORD,new IntWritable(totalRecords)));
            context.write(key, getMapWritable(ARRIVAL_ON_TIME,new IntWritable(arrivalOnTime)));
            context.write(key, getMapWritable(ARRIVAL_DELAY,new IntWritable(arrivalDelays)));
            context.write(key, getMapWritable(DEPARTURE_ON_TIME,new IntWritable(departureOnTime)));
            context.write(key, getMapWritable(DEPARTURE_DELAY,new IntWritable(departureDelays)));
            context.write(key, getMapWritable(IS_CANCELLED,new IntWritable(cancellations)));
            context.write(key, getMapWritable(IS_DIVERTED,new IntWritable(diversions)));
        }

    }
    public static class aggregationReduce extends Reducer<Text,MapWritable, NullWritable,Text>{
        public void reduce(Text key,Iterable<MapWritable> values,Context context) throws IOException, InterruptedException {
            double totalRecords = 0;
            double arrivalOnTime = 0;
            double arrivalDelays = 0;
            double departureOnTime = 0;
            double departureDelays = 0;
            double cancellations = 0;
            double diversions = 0;
            for(MapWritable v:values){
                IntWritable type = (IntWritable)v.get(TYPE);
                IntWritable value = (IntWritable)v.get(VALUE);
                if(type.equals(RECORD)){
                    totalRecords=totalRecords+value.get();
                }
                if(type.equals(ARRIVAL_ON_TIME)){
                    arrivalOnTime=arrivalOnTime+value.get();
                }
                if(type.equals(ARRIVAL_DELAY)){
                    arrivalDelays=arrivalOnTime+value.get();
                }
                if(type.equals(DEPARTURE_ON_TIME)){
                    departureOnTime=departureOnTime+value.get();
                }
                if(type.equals(DEPARTURE_DELAY)){
                    departureDelays=departureDelays+value.get();
                }
                if(type.equals(IS_CANCELLED)){
                    cancellations=cancellations+value.get();
                }
                if(type.equals(IS_DIVERTED)){
                    diversions=diversions+value.get();
                }

            }
            DecimalFormat df = new DecimalFormat( "0.0000" );
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
        job.setJobName("aggregationWithCombiner");

        job.setMapperClass(aggregationMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(MapWritable.class);

        job.setCombinerClass(aggregationCombiner.class);

        job.setReducerClass(aggregationReduce.class);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.23.129:9000/C5input"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\Flights\\AggregationWithCombiner_Output"));
        job.waitForCompletion(true);
    }

}
