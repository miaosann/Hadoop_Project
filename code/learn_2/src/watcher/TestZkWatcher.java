package watcher;

import org.apache.zookeeper.ZooKeeper;

public class TestZkWatcher {
    public static void main(String[] args) {
        try {
            //手写监听器
            ZkWatcher zkw = new ZkWatcher();
            //建立Zookeeper连接
            ZooKeeper zk = new ZooKeeper(ConfigString.serverConnectString, ConfigString.sessionTimeout,zkw);
            //把建立的Zookeeper连接加入监听器
            zkw.setZoo(zk);

            //zk.setData("/bbb", "w".getBytes(), -1);

            ContinueMonitor.continueMonitor(zk, zkw, ConfigString.workPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
