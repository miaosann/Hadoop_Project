### 基于关键字倒排索引（标题置顶）的简易搜索引擎

- ##### 爬虫

  使用爬虫随机爬取网页，由于很多网站带有反爬虫机制，所以我们要进行随机更换用户代理的方式。最终爬得500个HTML页面，作为我们的搜索引擎基础数据。

- ##### 倒排索引

  首先我们将爬得数据通过Java进行整理并中文分词，将其每个页面分为一份的txt文本，并以try_+标题名.txt进行命名。

  然后将其导入HDFS存储，使用MapReduce进行处理，最终得到倒排索引的形式。

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Search_Engine/img/4.png)

- ##### Hbase存储

  以关键字为Row_Key，并建立一个列族将倒排索引的后半部分存入；

- ##### JavaWeb实现 

  - 排序算法：

    基于关键字检索（标题优先）的方式，即如果标题中包含所搜索的关键字，则将其优先排列在上方。

  - 模糊检索：

    如果Hbase中没有所输入的字符串，那么将所搜索的关键字进行分词按最大匹配，返回结果。

  - null值处理：

    如果用户搜索的内容，数据库中不存在，那么将重定向的百度进行查取结果并返回。

  - 监听器：

    由于Hbase连接极其缓慢，所以这里使用监听器，在Tomcat启动时进行连接创立，有效加快了速度，提高了用户体验。

- ##### 成功运行

  搜索结果（title优先级高于关键字次数）
  
  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Search_Engine/img/2.png)
  
  演示视频
  
  <video id="video" controls="" preload="none" poster="https://github.com/miaosann/Hadoop_Project/blob/master/Search_Engine/img/3.png">
        <source id="mp4" src="https://github.com/miaosann/Hadoop_Project/blob/master/Search_Engine/img/example.mp4" type="video/mp4">
        <p>Your user agent does not support the HTML5 Video element.</p>
      </video>


