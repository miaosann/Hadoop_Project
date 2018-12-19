package watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ZkWatcher implements Watcher {

    private ZooKeeper zoo;

    public void setZoo(ZooKeeper zoo) {
        this.zoo = zoo;
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            Event.EventType type = event.getType();
            if(type==Event.EventType.NodeCreated) {
                System.out.println("NodeCreated");
            }else if(type==Event.EventType.NodeDeleted) {//删除子节点会触发，同时也会触发NodeChildrenChanged
                System.out.println("NodeDeleted");
            }else if(type==Event.EventType.NodeDataChanged) { //修改节点内容
                System.out.println("NodeDataChanged");
            }else if(type==Event.EventType.NodeChildrenChanged) {//节点删除和添加才会触发
                System.out.println("event.getPath()"+event.getPath());
                String slaves_str="";//slave的字符串
                //取出改变之后的儿子列表
                List<String> c=zoo.getChildren(event.getPath(), this);
                for (String string : c) {
                    //取出每个儿子的text文本
                    byte[] text=zoo.getData(event.getPath()+"/"+string, this, null);
                    String text_str = new String(text);
                    slaves_str+=text_str+"  ";
                }


                //把每个儿子的text文本累加串写入文件对象

                File f1 = new File("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\learn2\\slaves\\slaves1\\slaves.txt");
                if(f1.exists()) {//如果文件存在
                    FileWriter fw = new FileWriter(f1);
                    fw.write(slaves_str);
                    fw.flush();
                    fw.close();
                }
                File f2 = new File("C:\\Users\\miaohualin\\Desktop\\Hadoop_Project\\learn2\\slaves\\slaves2\\slaves.txt");
                if(f2.exists()) {//如果文件存在
                    FileWriter fw = new FileWriter(f2);
                    fw.write(slaves_str);
                    fw.flush();
                    fw.close();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
