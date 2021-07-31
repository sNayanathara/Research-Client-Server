package MyClientServer;

import java.util.ArrayList;

public class NodeDetails {

    //private int nodeIndex;
    private String username;
    private String ip;
    private int port;
    private int listeningPort;
    //private ArrayList<NodeDetails> otherNodeDetails = new ArrayList();

    public NodeDetails(String username, String ip, int port, int listeningPort) {
        //this.nodeIndex = nodeIndex;
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.listeningPort = listeningPort;
    }

//    public int getNodeIndex() {
//        return nodeIndex;
//    }
//
//    public void setNodeIndex(int nodeIndex) {
//        this.nodeIndex = nodeIndex;
//    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
