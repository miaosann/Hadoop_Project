package search_enginer


import org.apache.spark.{SparkConf, SparkContext}

object invertIndex {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf() //创建SparkConf对象，全局只有一个SparkConf
    conf.setAppName("my spark app") //设置应用程序的名称，在程序的监控界面可以看得到名称
    conf.setMaster("local[*]") //此时程序在本地运行，不需要Spark集群
    val sc = new SparkContext(conf) //创建SpackContext对象，通过传入SparkConf实例来定制Spark运行的具体参数的配置信息
    val lines = sc.textFile("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\test.txt", 1)

    val words = lines.map(x=>x.split("\\*\\*\\*\\*")).map(x=>{
      val title = x(0)
      val url = x(1)
      val keys = url+"::"+title
      val keywords = Jieba.cutWords(String.valueOf(x.apply(2)),keys)
      keywords
    }).flatMap(x=>x.split("\\*\\*")).map((_,1)).reduceByKey(_+_)
  }

}

