package MyClientServer;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Test {

    public static void main(String[] args) throws UnknownHostException {
        NodeDetails nodeDetails1 = new NodeDetails("localhost",9990, 9980);
        NodeDetails nodeDetails2 = new NodeDetails("localhost",9991, 9981);
        NodeDetails nodeDetails3 = new NodeDetails("localhost",9992, 9982);
        NodeDetails nodeDetails4 = new NodeDetails("localhost",9993, 9983);
        NodeDetails nodeDetails5 = new NodeDetails("localhost",9994,9984);
        NodeDetails nodeDetails6 = new NodeDetails("localhost",9995, 9985);
        NodeDetails nodeDetails7 = new NodeDetails("localhost",9996, 9986);
        NodeDetails nodeDetails8 = new NodeDetails("localhost",9997, 9987);
        NodeDetails nodeDetails9 = new NodeDetails("localhost",9998, 9988);
        NodeDetails nodeDetails10 = new NodeDetails("localhost",9999, 9989);

        Node node1 = new Node(nodeDetails1);
        Node node2 = new Node(nodeDetails2);
        Node node3 = new Node(nodeDetails3);
        Node node4 = new Node(nodeDetails4);
        Node node5 = new Node(nodeDetails5);
        Node node6 = new Node(nodeDetails6);
        Node node7 = new Node(nodeDetails7);

        Thread thread1 = new Thread(node1);
        Thread thread2 = new Thread(node2);
        Thread thread3 = new Thread(node3);
        Thread thread4 = new Thread(node4);
        Thread thread5 = new Thread(node5);
        Thread thread6 = new Thread(node6);
        Thread thread7 = new Thread(node7);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        //node1.fetchFile(node2, "Hi.pdf");
        //node1.fileSendRequestMsg("F:/fileFolder/marsland.ml-alg-perspect.09.pdf");
        node1.addNodesToNodeList();
        node1.fetchFileMsg("marsland.ml-alg-perspect.09.pdf");

    }
}
