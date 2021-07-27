package MyClientServer;

import java.util.ArrayList;

public class NodeDetails {

    private String ip;
    private int port;
    private int listeningPort;
    //private ArrayList<NodeDetails> otherNodeDetails = new ArrayList();

    public NodeDetails(String ip, int port, int listeningPort) {
        this.ip = ip;
        this.port = port;
        this.listeningPort = listeningPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }
}
