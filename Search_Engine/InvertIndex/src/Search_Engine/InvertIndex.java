package Search_Engine;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class InvertIndex {
    public static class InvertIndexMap extends Mapper<LongWritable, Text, Text, Text> {
        private Text keyInfo = new Text();  // 存储单词和URI的组合
        private Text valueInfo = new Text(); //存储词频
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            System.out.println("line："+line);
            String[] items = line.split(" \\*\\*\\*\\* ");
            System.out.println("items_length："+items.length);
            //String[] titles = items[0].split(",");
            String url = items[0];
            String title = items[1];
            String[] words = items[2].split(",");
//            for (String title : titles){
//                System.out.println(title);
//                keyInfo.set(title+":"+url);
//                valueInfo.set("2");
//                System.out.println("key"+keyInfo);
//                System.out.println("value"+valueInfo);
//                context.write(keyInfo, valueInfo);
//            }
            for (String word : words) {
                System.out.println(word);
                keyInfo.set(word+":"+url+"**"+title);
                valueInfo.set("1");
                System.out.println("key"+keyInfo);
                System.out.println("value"+valueInfo);
                context.write(keyInfo, valueInfo);
            }
        }
    }

    public static class InvertIndexCombiner extends Reducer<Text, Text, Text, Text> {
        private Text info = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            //统计词频
            int sum = 0;
            for (Text value : values) {
                sum += Integer.parseInt(value.toString());
            }

            int splitIndex = key.toString().indexOf(":");

            //重新设置value值由URI和词频组成
            info.set( key.toString().substring( splitIndex + 1) +"::"+sum );

            //重新设置key值为单词
            key.set( key.toString().substring(0,splitIndex));

            context.write(key, info);
            System.out.println("key"+key);
            System.out.println("value"+info);
        }
    }

    public static class InvertIndexReduce extends Reducer<Text, Text, Text, Text> {
        private Text result = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            //生成文档列表
            String fileList = new String();
            for (Text value : values) {
                System.out.println(value);
                fileList += value.toString()+";";
            }
            result.set(fileList);

            context.write(key, result);
//            int sum = 0;
//            for (Text value : values) {
//                sum += Integer.parseInt(value.toString());
//            }
//            context.write(key, new Text(String.valueOf(sum)));

        }
    }
    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir","D:\\hadoop-2.7.6");
        //获取配置对象信息
        Configuration conf = new Configuration();
        //获取job对象
        Job job = Job.getInstance(conf,"InvertIndex");
        //设置job运行的主类
        job.setJarByClass(search.class);
        //对Maper阶段进行设置
        job.setMapperClass(InvertIndexMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        //对Combiner阶段进行设置
        job.setCombinerClass(InvertIndexCombiner.class);
        //对reducer阶段进行设置
        job.setReducerClass(InvertIndexReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
		
        FileInputFormat.addInputPath(job, new Path("hdfs://192.168.217.138:9000/bingdb"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\learn_1\\InvertedIndex_OUT"+System.currentTimeMillis()+"\\"));
        //提交job作业并打印信息
        int isOk = job.waitForCompletion(true) ? 0 : 1;
        //退出job
        System.exit(isOk);
    }
}
