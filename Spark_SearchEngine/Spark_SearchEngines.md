### 基于Spark的实时动态搜索引擎

#### 1、 集群架构说明

![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/Spark集群架构.jpg)



在集群中，本项目使用三个了Spark节点进行SparkStreaming的计算；三个Zookeeper独立节点进行选举出Leader来协调Kafka和Hbase的工作；一个Tomcat服务器来进行Web端网站的工作。

基于以上的集群架构，所以工作流程为：

服务端：

首先Python爬虫进行连续不断的爬取数据，本项目中设置为每十秒进行一次爬取并将结果传输给Kafka进行缓存，而于此同时Spark集群中的Streaming也在实时读取Kafka中的数据，并对其进行分词处理，然后再建立倒排索引，最后存储至Hbase，值得注意的是Habse的RowKey由Map来实现，所以重复关键字会覆盖，所以一旦有相同关键字在已有的列族中添加小列来解决。最终Tomcat服务器去Hbase中请求数据，进行展示。

客户端：

首先，用户输入想要查询的内容，客户端程序会对用户所输入的内容进行处理，过滤掉无用的字符和乱码，将过滤后的结果进行查询，同时也会根据用户查询的关键字进行推荐相关的内容。最后把结果展示在网页中。

#### 2、算法说明

- ##### 数据获取

  - 爬虫

    在爬虫中，实时爬取了腾讯网的新闻，由于该网站的内容不是写好的html而是通过js传的json数据，所以省去了爬取html的过程，直接爬取返回的json数据，并使用json对象来解析出新闻title、新闻url和新闻的内容。每十秒中进行一次爬取，将内容实时返回。

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/1.png)

  - 生产者

    将以上实时爬取的数据，从python中实时传到Kafka中进行缓存，所以本项目中使用了Kafka的python包进行处理，但是中文存在乱码，所以要对即将进行传递的数据进行encoder转码。

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/2.png)

- ##### 数据计算

  对Kafka中缓存到的数据进行实时的分词计算，然后再将其建立倒排索引，后传入Hbase中进行保存。

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/3.png)

- ##### 数据持久化

  将倒排索引后的内容直接存储到Hbase中，且后面的关键字若有重复的RowKey，如果直接存储，会出现覆盖的现象，所以这里，一旦存在重复关键字，直接在之前已经存储的位置添加新的一列即可。

- ##### 检索

  在用户端输入检索内容，首先使用对用户输入内容进行过滤，去除掉不合理的乱码字符，然后对其进行检索，如果检索不到，会自动对该关键字进行切词，将切到的词组进行查询，若依然没有，那么直接给用户转入百度查询，并将查询结果跳转给用户。

- ##### 推荐

  对用户的关键词进行synonyms的相似词距离算法，通过计算后，返回list的方式存储所计算的几个相似词语，并且按照距离的长度由近及远排列，nearby_words_score是nearby_words中**对应位置**的词的距离的分数，分数在(0-1)区间内，越接近于1，代表越相近。

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/4.png)



  其中该算法的原理是通过计算各个词中的词向量间的距离所示，从词林中获取其频度等创立词向量，然后对其进行距离的计算，排序后返回距离最接近1的前几个即为最相近的一系列关键词。

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/5.png)



  然后，使用Jython来直接调用python推荐的算法，并获取其返回的结果即可。

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/6.png)


#### 3、程序结构

![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/Spark实时搜索功能点.jpg)



程序结构如上图所示，分为集群后台和用户端两大部分，集群后台结构再细分则是爬虫、Kafka、SparkStreaming、Hbase，而客户端的结构则是Tomcat+前端。

#### 4、运行说明

如果用户输入的内容有特殊符号乱码及不和谐的数字字母等会被程序直接过滤掉，这样的搜索和直接搜索哈登得到的结果是完全相同的。这样大大提升了用户的使用体验，以及程序的鲁棒性。

![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/7.png)



同时检索和推荐的内容如图所示：

![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/8.png)



该搜索引擎是动态的，如下图所示，我们第一次查询只有7条结果，而稍待片刻后查询相同内容‘哈登’，则返回11条结果：

![](https://github.com/miaosann/Hadoop_Project/blob/master/Spark_SearchEngine/img/9.png)



此外，在Java后台中，加入了监听器，在Tomcat开启的时候提前加载数据和建立连接，提升了程序查询时候的效率。
