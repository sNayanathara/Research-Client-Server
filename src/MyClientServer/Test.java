package MyClientServer;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Test {

    public static void main(String[] args) throws UnknownHostException, SocketException {

        NodeDetails nodeDetails3 = new NodeDetails("Node2", "localhost",9802, 9902);

        Node node3 = new Node(nodeDetails3);

        //node3.addNodesToNodeList();

        Thread thread3 = new Thread(node3);

        thread3.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       // node3.fileSendRequestMsg("/home/ubuntu/marsland.ml-alg-perspect.09.pdf");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //node3.fetchFileMsg("marsland.ml-alg-perspect.09.pdf");

        //node3.sendRegistryRequestToMiner();
        //node3.sendLeaveSystemRequestToMiner();

    }
}
