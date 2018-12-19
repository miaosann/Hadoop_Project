package watcher;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

public class ContinueMonitor {
    public static void continueMonitor(ZooKeeper zooKeeper, Watcher watcher, String path) {
        /**
         * 持续监控path下的结点
         */
        for (;;) {
            try {
                // 判断所要监控的主结点是否存在
                zooKeeper.exists(path, watcher);
                // 获取所监听主节点下所有的子节点
                List<String> nodeList = zooKeeper.getChildren(path, watcher);
                for (String child : nodeList) {
                    System.out.println("child: " + child);
                }
                // 对path下的每个结点都设置一个watcher
                for (String nodeName : nodeList) {
                    // 判断所要监控的各子节点是否存在
                    zooKeeper.exists(path + "/" + nodeName, watcher);
                }
                Thread.sleep(3000);// sleep3秒，减少CUP占用率
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
