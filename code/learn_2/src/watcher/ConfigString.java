package watcher;

import java.util.ArrayList;
import java.util.List;

public class ConfigString {
    //连接Zookeeper
    public static final String serverConnectString = "192.168.217.145:2181,"
            + "192.168.217.146:2181,"
            + "192.168.217.147:2181";
    public static final int sessionTimeout = 2000;
    public static final String workPath = "/slaves";
    public static final List<String> ipList = new ArrayList<String>();
    static {
        ipList.add("192.168.217.145");
        ipList.add("192.168.217.146");
        ipList.add("192.168.217.147");
    }
}
