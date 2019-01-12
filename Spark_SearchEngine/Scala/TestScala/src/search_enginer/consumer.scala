package search_enginer

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils

object consumer {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.6")

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test")
    val scc = new StreamingContext(sparkConf, Duration(5000))
    scc.sparkContext.setLogLevel("ERROR")
    scc.checkpoint("./consumer") // 因为使用到了updateStateByKey,所以必须要设置checkpoint
    val topics = Set("test1") //我们需要消费的kafka数据的topic
    val brokers = "192.168.217.145:9092,192.168.217.146:9092,192.168.217.147:9092"
    val kafkaParam = Map[String, String](
      "zookeeper.connect" -> "192.168.217.145:2181",
      "metadata.broker.list" -> brokers, // kafka的broker list地址
      "serializer.class" -> "kafka.serializer.StringEncoder")

    //加载Hbase
    //val table : Table= HbaseUtil.loading()

    val stream: InputDStream[(String, String)] = createStream(scc, kafkaParam, topics)

    val words = stream.map(_._2).map(x=>x.split("\\*\\*\\*\\*")).map(x=>{
      val titleKeys = x(0)
      val url = x(1)
      val keys = url+"::"+titleKeys
      val keywords = Jieba.cutWords(String.valueOf(x.apply(2)),keys)
      keywords
    }).flatMap(x=>x.split("\\*\\*")).map((_,1)).reduceByKey(_+_).foreachRDD(x=>{
      x.map(x=>(x._2,x._1)).sortByKey(false).map(x=>(x._2,x._1)).foreach(x=>{
        HbaseUtil.insertFromStream(x._1,x._2)
      })
    })

    scc.start() // 真正启动程序
    scc.awaitTermination() //阻塞等待
  }

  val updateFunc = (currentValues: Seq[Int], preValue: Option[Int]) => {
    val curr = currentValues.sum
    val pre = preValue.getOrElse(0)
    Some(curr + pre)
  }
  /**
    * 创建一个从kafka获取数据的流.
    */

  def createStream(scc: StreamingContext, kafkaParam: Map[String, String], topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](scc, kafkaParam, topics)
  }
}
