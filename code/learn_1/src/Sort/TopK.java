package Sort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class TopK {
    public class TopKMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //aaa 123
            String line = value.toString();
            String kv[] = line.split("\\s+");
            context.write(new Text(kv[0]), new IntWritable(Integer.parseInt(kv[1])));
        }
    }
}
