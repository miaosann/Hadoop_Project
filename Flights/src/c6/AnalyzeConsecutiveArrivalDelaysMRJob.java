package c6;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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

public class AnalyzeConsecutiveArrivalDelaysMRJob {
    public static class AnalyzeConsecutiveDelaysMapper extends Mapper<LongWritable, Text, ArrivalFlightKey, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (!AirlineDataUtils.isHeader(value)) {
                String[] contents = value.toString().split(",");
                String arrivingAirport = AirlineDataUtils.getDestination(contents);
                String arrivingDtTime =  AirlineDataUtils.getArrivalDateTime(contents);
                int arrivalDelay = AirlineDataUtils.parseMinutes(
                        AirlineDataUtils.getArrivalDelay(contents),0);
                if(arrivalDelay>0){
                    ArrivalFlightKey afk = new ArrivalFlightKey(new Text(arrivingAirport),
                            new Text(arrivingDtTime));
                    context.write(afk, value);
                }
            }
        }
    }

    public static class AnalyzeConsecutiveDelaysReducer extends Reducer<ArrivalFlightKey, Text, NullWritable, Text> {
        public void reduce(ArrivalFlightKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Text previousRecord = null;
            //格式：<Arrival_delay_record>|<Previous_arrival_delay_record>
            for (Text v : values) {
                StringBuilder out = new StringBuilder("");
                if(previousRecord==null){
                    out.append(v.toString()).append("|");
                }
                else{
                    out.append(v.toString()).append("|").append(previousRecord.toString());
                }
                context.write(NullWritable.get(), new Text(out.toString()));
                //Remember to not use references as the same Text instance
                //is utilized across iterations
                previousRecord=new Text(v.toString());
            }
        }
    }

    public static class ArrivalFlightKeyBasedPartioner extends Partitioner<ArrivalFlightKey, Text> {
        @Override
        public int getPartition(ArrivalFlightKey key, Text value, int numPartitions) {
            return Math.abs(key.destinationAirport.hashCode() % numPartitions);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir","D:\\BigData_jar\\hadoop-2.7.6");
        Configuration conf = new Configuration();
        Job job = Job.getInstance();
        job.setJarByClass(AnalyzeConsecutiveArrivalDelaysMRJob.class);
        job.setJobName("AnalyzeConsecutiveArrivalDelaysMRJob");

        job.setMapperClass(AnalyzeConsecutiveDelaysMapper.class);
        job.setMapOutputKeyClass(ArrivalFlightKey.class);
        job.setMapOutputValueClass(Text.class);

        job.setReducerClass(AnalyzeConsecutiveDelaysReducer.class);
        //You can set any number of reducers you want. 12 is just a number I picked.
        job.setNumReduceTasks(12);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        job.setPartitionerClass(ArrivalFlightKeyBasedPartioner.class);

        job.setSortComparatorClass(ArrivalFlightKeySortingComparator.class);
        job.setGroupingComparatorClass(ArrivalFlightKeyGroupingComparator.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.23.129:9000/C5input"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\Flights\\AnalyzeConsecutiveArrivalDelaysMRJob_Output"));
        job.waitForCompletion(true);
    }

}
