package zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.List;

public class TestCase {

    //连接Zookeeper
    private static final String connectionString = "192.168.217.145:2181,"
            + "192.168.217.146:2181,"
            + "192.168.217.147:2181";
    public static final Integer sessionTimeout = 2000;
    public static ZooKeeper zkClient = null;

    @Before
    public void init() throws IOException {
        /**
         * 三个参数分别为连接的zookeeper集群服务器的ip，超时时间，监听器
         */
        zkClient = new ZooKeeper(connectionString, sessionTimeout, new Watcher() {
            @Override
            //收到事件通知后的回调函数（应该是我们自己的事件处理逻辑）
            public void process(WatchedEvent event) {
                System.out.println(event.getType() + "," + event.getPath());
            }
        });
    }

    /**
     * 创建数据节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        /**
         * 传入的四个参数值
         * 1、创建的节点
         * 2、节点数据
         * 3、节点的权限，OPEN_ACL_UNSAFE表示内部的应用权限
         * 4、节点的类型：分四种
         */
        String path = zkClient.create("/idea",
                "helloworld".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT
        );
        System.out.println(path);
    }

    /**
     * 获取子节点
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void getChildren() throws KeeperException, InterruptedException, IOException {
        /**
         * 传入两个参数
         * 1、指定获取哪个节点的孩子
         * 2、是否使用监听器，true表示使用以上的监听功能
         */
        List<String> children = zkClient.getChildren("/",true);
        for (String child:children){
            System.out.println(child);
        }
        System.in.read();
    }

    /**
     * 判断节点是否存在
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void testExist() throws KeeperException, InterruptedException {
        //一个参数是节点，一个是是否用监听功能,Stat封装了该节点的相关信息比如：czxid，mzxid，ctime，mtime等
        Stat stat = zkClient.exists("/idea",false);
        System.out.println(stat==null?"不存在":"存在");
    }

    /**
     * 获取节点数据
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = zkClient.getData("/idea", false, null);
        System.out.println(new String(data));
    }

    /**
     * 删除节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void delete() throws KeeperException, InterruptedException {
        //第一个参数表示地址，第二个参数表示版本，-1代表所有版本
        zkClient.delete("/idea",-1);
    }

    /**
     * 修改节点的数据
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void update() throws KeeperException, InterruptedException {
        //原 /idea节点的数据为helloworld
        zkClient.setData("/idea", "zookeeper".getBytes(), -1);
        //查看修改数据是否成功
        byte[] data = zkClient.getData("/idea", false, null);
        System.out.println(new String(data));
    }
}
