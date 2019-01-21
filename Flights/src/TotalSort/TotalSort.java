package TotalSort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import javax.naming.Context;
import java.io.IOException;

public class TotalSort {
    static class TotalSortMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

        protected void TotalSortMap(LongWritable key, Text value,
                           Context context) throws IOException, InterruptedException {
            IntWritable intWritable = new IntWritable(Integer.parseInt(value.toString()));
            context.write(intWritable, intWritable);
        }
    }

    static class TotalSortReducer extends Reducer<IntWritable, IntWritable, IntWritable, NullWritable> {

        protected void reduce(IntWritable key, Iterable<IntWritable> values,
                              Context context) throws IOException, InterruptedException {
            for (IntWritable value : values)
                context.write(value, NullWritable.get());
        }
    }

    public static class IteblogPartitioner extends Partitioner<IntWritable, IntWritable> {

        public int getPartition(IntWritable key, IntWritable value, int numPartitions) {
            int keyInt = Integer.parseInt(key.toString());
            if (keyInt < 10000) {
                return 0;
            } else if (keyInt < 20000) {
                return 1;
            } else {
                return 2;
            }
        }
    }


}
