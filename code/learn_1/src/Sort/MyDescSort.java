package Sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Random;

public class MyDescSort {
    public static class descSortMap extends Mapper<Object, Text, IntWritable, IntWritable> {
        //Map阶段的两个压入context的参数（也就是第3、4个参数）类型皆修改为IntWritable，而不是Text
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //将输入文件的每一行转换成IntWritable
            IntWritable data = new IntWritable(Integer.parseInt(value.toString()));
            //搞个随机数
            IntWritable random = new IntWritable(new Random().nextInt());
            context.write(data,random);
        }
    }
    public static class descSortReduce extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        //将reducer得到的两个数据类型（第1、2个参数）标识为IntWritable，而不是Text
        //将reducer写到文件的两个数据类型（第3、4个参数）标识为IntWritable，而不是Text
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //遍历values，有1个随机数，输出一次key
            while (values.iterator().hasNext()){
                context.write(key,null);
                //记得遍历的时候，将游标（迭代器）向后推
                values.iterator().next();
            }
        }
    }
    // 使Sort阶段的Key降序排列的比较器
    public static class IntWritableDecreasingComparator extends IntWritable.Comparator {
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -super.compare(b1, s1, l1, b2, s2, l2);
        }
    }
    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir","D:\\hadoop-2.7.6");
        //获取配置对象信息
        Configuration conf = new Configuration();
        //获取job对象
        Job job = Job.getInstance(conf,"descSort");
        //设置job运行的主类
        job.setJarByClass(MyDescSort.class);
        //对Map阶段进行设置
        job.setMapperClass(descSortMap.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.217.138:9000/sort"));
        //对Reduce阶段进行设置
        job.setReducerClass(descSortReduce.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.217.138:9000/ascOut"));
        job.setSortComparatorClass(IntWritableDecreasingComparator.class);// 设置Sort阶段使用比较器
        //提交job作业并打印信息
        int isOk = job.waitForCompletion(true) ? 0 : 1;
        //退出job
        System.exit(isOk);
    }
}
