#### MapReduce开发基础

- ##### 数据集

  数据来自航空公司数据集，从1987~2008美国国内商业航班目的地和起飞的航班信息，这个数据大概有120万条，非压缩格式下大小为120G，当然由于设别限制我只找了几十条进行在自己笔记本中搭建的Hadoop集群中测试学习。

- ##### 知识点

  - ##### `SELECT`

    只有Map阶段

  - ##### `WHERE`

    只有Map阶段

  - ##### `AGGREGATION`

    - 第一种为Map和Reduce两个阶段
    - 第二种可以加入Combiner阶段，主要为了减小从Map到Reduce的IO传输的压力，将Combiner放到Map的集群上，先将数据进行处理后，在发送给Reduce，有效减小了IO的压力，提高了效率

  - ##### `SPLIT`

    在Reduce过程中，加入了Partation过程，即分配多个Reduce，并按照意愿将不同的内容分配给不同的Partation，最终结果分多个文件输出
