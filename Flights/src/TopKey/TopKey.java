package TopKey;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class TopKey {
    public class TopKMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //aaa 123
            String line = value.toString();
            String kv[] = line.split("\\s+");
            context.write(new Text(kv[0]), new IntWritable(Integer.parseInt(kv[1])));
        }
    }

    public class TopKReduce extends Reducer<Text, IntWritable, Text, Text> {
        TreeMap tm = new TreeMap();

        /**
         * 初始化方法，在reduce前执行
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) {
            int keySum=0;
            for (IntWritable val : values) {
                keySum+=val.get();
            }

            tm.put(keySum, key.toString());

            if(tm.size()>10) {
                tm.remove(tm.firstKey());
            }


        }

        /**
         * 清除方法，在reduce后执行
         * @param context
         */
        @Override
        public void cleanup(Context context) {
            try {
                Set<Integer> kSet = tm.keySet();
                Iterator<Integer> it = kSet.iterator();
                while(it.hasNext()) {
                    Integer key = it.next();
                    context.write(new Text(tm.get(key).toString()), new Text(key.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
