package MyClientServer;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Test {

    public static void main(String[] args) {
        NodeDetails nodeDetails1 = new NodeDetails("localhost",9990);
        NodeDetails nodeDetails2 = new NodeDetails("localhost",9991);
        NodeDetails nodeDetails3 = new NodeDetails("localhost",9992);
        NodeDetails nodeDetails4 = new NodeDetails("localhost",9993);
        NodeDetails nodeDetails5 = new NodeDetails("localhost",9994);
        NodeDetails nodeDetails6 = new NodeDetails("localhost",9995);
        NodeDetails nodeDetails7 = new NodeDetails("localhost",9996);
        NodeDetails nodeDetails8 = new NodeDetails("localhost",9997);
        NodeDetails nodeDetails9 = new NodeDetails("localhost",9998);
        NodeDetails nodeDetails10 = new NodeDetails("localhost",9999);
        Node node1 = new Node(nodeDetails1);
        Node node2 = new Node(nodeDetails2);
        Thread thread1 = new Thread(node1);
        Thread thread2 = new Thread(node2);

        thread1.start();
        thread2.start();


        node1.fetchFile(node2, "Hi.pdf");
        //node1.sendFile(node2, "F://image.pdf");

    }
}
