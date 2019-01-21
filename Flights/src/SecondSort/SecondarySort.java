package SecondSort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

/**
 * 二次排序
 * 重写类：IntPair、FirstPartitioner、GroupingComparator、SortRightComparator
 * 其中IntPair构成了传输的key
 * FirstPartitioner是自定义分区
 * 定义GroupingComparator类，实现分区内的数据分组
 * 没有自定义SortComparator的话，是使用IntPair中compareTo方法进行排序
 */
public class SecondarySort {
    public static class SecondarySortMap extends Mapper<LongWritable, Text, IntPair, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (line!=null) {
                System.out.println(line);
                String[] contents = line.split("\\s+");
                int left = 0;
                int right = 0;
                left = Integer.parseInt(contents[0]);
                right = Integer.parseInt(contents[1]);
                context.write(new IntPair(left, right), new IntWritable(right));
                System.out.println("---------");
            }else {
                System.out.println("NULL......");
            }
        }
    }

    /**
     * 自定义分区函数类FirstPartitioner，根据 IntPair中的first实现分区
     */
    public static class FirstPartitioner extends Partitioner<IntPair, IntWritable> {
        @Override
        public int getPartition(IntPair key, IntWritable intWritable, int numPartitions) {
            //return Math.abs(key.getFirst() * 127) % numPartitions;  //原版仅使用一个reduce
            return key.getFirst()/10 - 3;
        }
    }

    /**
     * 自定义GroupingComparator类，实现分区内的数据分组
     */
    public static class GroupingComparator extends WritableComparator {
        protected GroupingComparator(){
            super(IntPair.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            IntPair ip1 = (IntPair) a;
            IntPair ip2 = (IntPair) b;
            int l = ip1.getFirst();
            int r = ip2.getFirst();
            return l == r ? 0 : (l < r ? -1 : 1);
        }
    }

    public static class SecondarySortReduce extends Reducer<IntPair, IntWritable, Text, IntWritable> {
        public void reduce(IntPair key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            for (IntWritable val : values) {
                context.write(new Text(Integer.toString(key.getFirst())), val);
            }
        }
    }

    public static class SortRightComparator extends WritableComparator{
        public SortRightComparator(){
            super(IntPair.class,true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            IntPair first = (IntPair) a;
            IntPair second = (IntPair) b;
            if (first.getFirst()==second.getFirst()){//倒叙
                return first.getSecond()==second.getSecond()?0:(first.getSecond()<second.getSecond()?1:-1);
            }else {//正序
                return first.getFirst()==second.getFirst()?0:(first.getFirst()<second.getFirst()?-1:1);
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir","D:\\BigData_jar\\hadoop-2.7.6");
        Configuration conf = new Configuration();
        Job job = Job.getInstance();
        job.setJarByClass(SecondarySort.class);
        job.setJobName("SecondarySort");

        job.setMapperClass(SecondarySortMap.class);
        job.setMapOutputKeyClass(IntPair.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 分区函数
        job.setPartitionerClass(FirstPartitioner.class);
        // 分组函数
        job.setGroupingComparatorClass(GroupingComparator.class);

        job.setReducerClass(SecondarySortReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(3); //将所有的保存在三个输出里3个reduce

        // 本示例并没有自定义SortComparator，而是使用IntPair中compareTo方法进行排序
        // job.setSortComparatorClass();
        job.setSortComparatorClass(SortRightComparator.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\SecondarySort.txt"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\SecondarySort_Output"));
        job.waitForCompletion(true);
    }
}
