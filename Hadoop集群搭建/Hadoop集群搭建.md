### Hadoop集群搭建指南

最近开了Hadoop课程，所以我开始一穷二白从零开始满怀期待地搭建Hadoop的集群，结果被一大群莫名其妙的问题困扰，最终经过两天奋战终于搭好了，特意写一份记录，以便抒发自己的感悟，同时也是给其他还未涉及这一领域的同学提供一些参考吧。

- ##### 安装Linux系统

  这里其实选择什么版本Linux都无所谓，但是老师给的是`Ubuntu16.04`，而我自身则喜欢`Centos7`，所以以下内容均为基于`Centos7`进行。

- ##### 配置ip地址、机器名、hosts文件、ssh远程连接

  - ip地址

    对于ip地址，存在两种方案，一种是使用ip即可，另一种是配置静态ip。这里我没有配置静态ip，所以没有什么可说的。配置静态ip也很简单，只需在`/etc/network/interfaces`的文件中，添加一些配置项：

    > static 指定为静态 
    >
    > ip address 指定静态 
    >
    > ip 地址 
    >
    > netmask 指定子网掩码 
    >
    > gateway 指定网关 
    >
    > dns-nameserver 指定域名解析服务器  

    然后再重启网络服务`/etc/init.d/networking restart`即可。

  - 机器名

    机器名修改也很简单，只需在`/etc/hostname`中写入你想要的机器名。

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/hostname.PNG)

  - hosts文件

    hosts文件是将ip和我们的机器名相对应，这里要写入namenode主节点以及所有从节点datanode，也是在`/etc/hosts`中进行修改。

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/hosts.PNG)

  - ssh远程连接

    ssh远程连接想必大家都会经常使用，所以就不在多说什么了，由于我装的是最小安装版的系统，所以并不会带网络工具，这里需要使用`yum install net-tools`安装，安装好后`ifconfig`命令就可以愉快使用了，直接ssh连接就好。

- ##### 配置各个虚拟机之间ssh免秘钥登录

  此步骤就是为了方便在启动集群时不必逐一确认密码，在实际应用中需要，但是在我三台机器的实验来说，就没有必要了，这步骤类似于Github的免密，想必使用GitHub的人也都配置过。

- ##### 配置Java环境变量和Hadoop环境变量

  - > 首先我们将Jdk和Hadoop都下载解压了，这里我的安装目录是`/usr/myData/jdk1.8.0_171`和`Hadoop-2.7.6`。并且创建Hadoop一些目录，这里我在Hadoop的目录下新建mydir目录，然后再里面依次创建tmp、name、data、yarnnm四个子目录。

  - 其次进行环境变量配置，首先进入`/etc/profile`目录下，然后配置即可。

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/env.PNG)

- ##### 配置Hadoop

  首先，我们进入Hadoop安装目录下的`/etc/hadoop`目录下：

  - 配置core-site.xml

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/core-site.PNG)

  - 配置hdfs-site.xml

    > 再这里面的dfs.raplication中的value=2，意思是有两个datanode

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/hdfs-site.PNG)

  - 配置mapred-site.xml

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/mapred-site.PNG)

  - 配置yarn-site.xml

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/yarn-site.PNG)

  - 配置slaves文件

    > slave文件中是标明datanode，所以只需列出所有datanode的名字即可

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/slave.PNG)

  - 配置hadoop-env.sh

    > 可能hadoop自身的缺陷吧，它不太会识别这里的`JAVA_HOME=$JAVA_HOME`所以我们选择改为绝对路径

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/hadoop-env.PNG)

  - 格式化namenode

    格式化方式：在`Hadoop-2.7.6`目录下，使用指令`bin/hdfs namenode -format`

    > 格式化有一次就行了，别呆着没事就格式化，不然可能造成以下的问题，集群id不一致，直接表现为namenode开启后，datanode连不上。所以查看日志，发现以下报错。
    >
    > 解决方案：手动把我们创建的mydir/data/current/VERSION进行改正即可

    ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/erro.PNG)

- ##### 启动hdfs和yarn

  经过了一系列挣扎，如果按照上述方式配置后，在这里我们就可以启动了。在`Hadoop-2.7.6`目录下使用指令`sbin/start-dfs.sh`和`sbin/start-yarn.sh`即可。

  > 启动成功后使用`jps`指令分别可以看到以下结果（hdfs、yarn、datanode）

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/start1.PNG)

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/start2.PNG)

  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/datanode_start.PNG)
- ##### 愉快的wordCount

  第一个例子不是Helloworld了，而是wordCount。如图：
  ![](https://github.com/miaosann/Hadoop_Project/blob/master/Hadoop集群搭建/img/run.PNG)
