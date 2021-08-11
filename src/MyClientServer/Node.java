package MyClientServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Node implements Runnable {

    private Logger logger = LogManager.getLogManager().getLogger("a");
    DatagramSocket sock = null;

    private NodeDetails nodeDetails;
    private ArrayList<NodeDetails> nodesList = new ArrayList<>();

    public Node(NodeDetails nodeDetails) throws SocketException {
        this.nodeDetails = nodeDetails;
        sock = new DatagramSocket(nodeDetails.getPort());
    }

    public void addNodesToNodeList() {  //a temp function to add the nodes to the ArrayList -> nodeList
        nodesList.add(new NodeDetails("Node0","localhost",9990, 7981));
        nodesList.add(new NodeDetails("Node1","localhost",9991, 9981));
        nodesList.add(new NodeDetails("Node2","localhost",9992, 9982));
        nodesList.add(new NodeDetails("Node3","localhost",9993, 9983));
        nodesList.add(new NodeDetails("Node4","localhost",9994, 9984));
        nodesList.add(new NodeDetails("Node5","localhost",9995, 9985));
//        nodesList.add(new NodeDetails("Node6","localhost",9996, 9986));
//        nodesList.add(new NodeDetails("localhost", 9997));
//        nodesList.add(new NodeDetails("localhost", 9998));
//        nodesList.add(new NodeDetails("localhost", 9999));
        //System.out.println(nodesList);

    }
    public void run() {
        try {

            while(true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                StringTokenizer st = new StringTokenizer(new String(data, 0, incoming.getLength()), " ");
                String command = st.nextToken();

               if (command.equals("FETCH")) {

                   System.out.println("FETCH");

                   int receiverListeningPort = Integer.parseInt(st.nextToken());
                   String fileChunkName = st.nextToken();
                   String seekingFileName = st.nextToken();
                   String nodeUsername = st.nextToken();
                   String task = "FETCH";

//                   String fileChunkPath = "F:\\CopyFiles\\RecievedFiles\\" + nodeUsername + "\\" + fileChunkName;
                   String fileChunkPath = FilePathsUtil.getSystemReceivedFiles() + nodeUsername + "\\" + fileChunkName;

                   FilePasser filePasser = new FilePasser(task, receiverListeningPort, fileChunkPath);
                   Thread filePasserThread = new Thread(filePasser);
                   filePasserThread.start();

                } else if (command.equals("SEND_REQUEST")) {
                    String fileToBeAccepted = st.nextToken();
                    int nodeNumber = Integer.parseInt(st.nextToken());

                    System.out.println(command);
                    System.out.println("Accept" + fileToBeAccepted + " " + nodeNumber);

                    acceptRequestMsg(fileToBeAccepted, nodeNumber, incoming);

                } else if (command.equals("OK_SEND")) {
                    String fileToBeSend = st.nextToken();
                    int listeningPort = Integer.parseInt(st.nextToken());
                    String nodeUsername = st.nextToken();

                    System.out.println(command);
                    System.out.println("Sending" + fileToBeSend + " " + listeningPort);

                   sendFile(fileToBeSend, listeningPort, incoming, nodeUsername);

                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void fileSendRequestMsg(String filePathOfSendingFile) throws UnknownHostException {
        String task = "FILE_SEND_REQUEST";
        FilePasser filePasser = new FilePasser(task, filePathOfSendingFile, nodesList, sock);
        filePasser.passFileSendRequestMsg();

    }

    public void acceptRequestMsg(String fileToBeAccepted, int nodeNumber, DatagramPacket incoming) throws UnknownHostException {

        String nodeUsername = nodesList.get(nodeNumber).getUsername();
        int listeningPort = nodesList.get(nodeNumber).getListeningPort();
        String task = "RECEIVE_SEND";

        String msg = "OK_SEND " + fileToBeAccepted + " " + listeningPort + " " + nodeUsername;

        FileFetcher fileFetcher = new FileFetcher(task, listeningPort, fileToBeAccepted, nodeUsername);
        Thread fileFetcherThread = new Thread(fileFetcher);
        fileFetcherThread.start();

        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
    }

    public void sendFile(String fileName, int listeningPort, DatagramPacket incoming , String nodeUsername) {

        String task = "SEND";

        FilePasser filePasser = new FilePasser(task, fileName, listeningPort, incoming, nodeUsername);
        Thread filePasserThread = new Thread(filePasser);
        filePasserThread.start();

    }

    public static void sendMsgViaSocket(DatagramSocket socket, InetAddress bs_address, int port, String msg) {
        try {
            System.out.println(msg);
            DatagramPacket out_packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, bs_address, port);
            socket.send(out_packet);
        } catch (Exception e) {
            //logger.log(Level.INFO, e.toString());
        }
    }
    public HashMap<String, String> getFileData(String seekingFile){
        HashMap<String, String> filedata = new HashMap<>();
        System.out.println("inside");

        filedata.put(nodesList.get(1).getUsername(), "marsland.ml-alg-perspect.09_part_0.enc");
        filedata.put(nodesList.get(2).getUsername(), "marsland.ml-alg-perspect.09_part_1.enc");
        filedata.put(nodesList.get(3).getUsername(), "marsland.ml-alg-perspect.09_part_2.enc");
        filedata.put(nodesList.get(4).getUsername(), "marsland.ml-alg-perspect.09_part_3.enc");
        filedata.put(nodesList.get(5).getUsername(), "marsland.ml-alg-perspect.09_part_4.enc");

        System.out.println("HashMap");
        return filedata;

    }

    public void fetchFileMsg(String seekingFile) {
        System.out.println("sock1 " +sock);
        HashMap<String, String> filedata = getFileData(seekingFile);
        HashMap<String, SecretKey> keys = SplitFiles.keys;
        HashMap<String, IvParameterSpec> ivs = SplitFiles.IVs;
        System.out.println(keys);
        System.out.println(ivs);
        System.out.println(nodesList.size());

        int listeningPort = nodesList.get(0).getListeningPort();
        String task = "FETCH";

        FileFetcher fileFetcher = new FileFetcher(task, listeningPort, nodesList, filedata, keys, ivs, seekingFile, sock);
        Thread fileFetcherThread = new Thread(fileFetcher);
        fileFetcherThread.start();

    }
}
