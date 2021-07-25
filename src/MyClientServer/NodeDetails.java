package MyClientServer;

import java.util.ArrayList;

public class NodeDetails {

    private String ip;
    private int port;
    //private ArrayList<NodeDetails> otherNodeDetails = new ArrayList();

    public NodeDetails(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
