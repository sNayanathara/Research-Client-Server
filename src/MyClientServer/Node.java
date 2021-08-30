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
    static String minerIP = "localhost";

    public Node(NodeDetails nodeDetails) throws SocketException, UnknownHostException {
        this.nodeDetails = nodeDetails;
        sock = new DatagramSocket(nodeDetails.getPort());

        sendRegistryRequestToMiner();
    }

//    public void addNodesToNodeList() {  //a temp function to add the nodes to the ArrayList -> nodeList
//        nodesList.add(new NodeDetails("Node0", "18.191.191.108",9801, 9901));
//        nodesList.add(new NodeDetails("Node1", "3.144.71.58",9801, 9901));
//
//    }
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

                   String ip = st.nextToken();
                   int receiverListeningPort = Integer.parseInt(st.nextToken());
                   String fileChunkName = st.nextToken();
                   String seekingFileName = st.nextToken();
                   String nodeUsername = st.nextToken();
                   String task = "FETCH";


//                   String fileChunkPath = "F:\\CopyFiles\\RecievedFiles\\" + nodeUsername + "\\" + fileChunkName;
                   //String fileChunkPath = FilePathsUtil.SYSTEM_RECEIVED_FILES + nodeUsername + "/" + fileChunkName;
                   String fileChunkPath = FilePathsUtil.SYSTEM_RECEIVED_FILES + nodeUsername + "\\" + fileChunkName;

                   FilePasser filePasser = new FilePasser(task, ip, receiverListeningPort, fileChunkPath);
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
                    String ip = st.nextToken();
                    int listeningPort = Integer.parseInt(st.nextToken());
                    String nodeUsername = st.nextToken();

                    System.out.println(command);
                    System.out.println("Sending" + fileToBeSend + " " + listeningPort);

                   sendFile(fileToBeSend, ip, listeningPort, incoming, nodeUsername);

//                } else  if (command.equals("REGISTERED")) {
//                   System.out.println(command);
////                   String username = st.nextToken();
////                   String ip = st.nextToken();
//
//                  // nodesList.add(new NodeDetails(username, ip, 9802, 9902));
//                   int nodeListSize = Integer.parseInt(st.nextToken());
//
//                   for (int i=0; i<nodeListSize; i++) {
//                       String nodeData = st.nextToken();
//                       String username = nodeData.substring(0, nodeData.indexOf("_"));
//                       String ip = nodeData.substring(nodeData.indexOf("_"));
//                       System.out.println(username + ip);
//                       nodesList.add(new NodeDetails(username, ip, 9802, 9902));
//                   }

               } else if (command.equals("ADD_NODE")){

                   System.out.println(command);

                   String username = st.nextToken();
                   String ip = st.nextToken();
                   int port = Integer.parseInt(st.nextToken());
                   int listeningPort = Integer.parseInt(st.nextToken());

                   nodesList.add(new NodeDetails(username, ip, port, listeningPort));
                   System.out.println(nodesList);
                   System.out.println("Added to nodelist: " + username);

               } else if (command.equals("REMOVED")) {

                   System.out.println(command);

                   System.out.println("REMOVED>>>>>");
                   nodesList.clear();

               } else if (command.equals("DELETE_NODE")) {

                   System.out.println(command);

                   String username = st.nextToken();
                   String ip = st.nextToken();
                   int port = Integer.parseInt(st.nextToken());
                   int listeningPort = Integer.parseInt(st.nextToken());

                   System.out.println(nodesList.size());
                   System.out.println(nodesList);


                   int count = 0;
                   for (NodeDetails nodes: nodesList) {
                       if (nodes.getUsername().equals(username)) {
                           break;
                       }
                       count++;
                   }
                   nodesList.remove(count);

                   System.out.println("Deleted :" + username);
                   System.out.println(nodesList.size());
                   System.out.println(nodesList);
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

        //String nodeUsername = nodesList.get(nodeNumber).getUsername();
        String nodeUsername = nodeDetails.getUsername();
        String ip = nodeDetails.getIp();
        int listeningPort = nodeDetails.getListeningPort();
        String task = "RECEIVE_SEND";

        String msg = "OK_SEND " + fileToBeAccepted + " " + ip + " " + listeningPort + " " + nodeUsername;

        FileFetcher fileFetcher = new FileFetcher(task, listeningPort, fileToBeAccepted, nodeUsername);
        Thread fileFetcherThread = new Thread(fileFetcher);
        fileFetcherThread.start();

        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);

    }

    public void sendFile(String fileName, String ip, int listeningPort, DatagramPacket incoming , String nodeUsername) {

        String task = "SEND";

        FilePasser filePasser = new FilePasser(task, fileName, ip, listeningPort, incoming, nodeUsername);
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

        filedata.put(nodesList.get(0).getUsername(), "marsland.ml-alg-perspect.09_part_0.enc"); //////initially get(1) walin start kre
        //filedata.put(nodesList.get(1).getUsername(), "marsland.ml-alg-perspect.09_part_1.enc");
        //////initially get(1) walin start kre

        return filedata;

    }

    public void fetchFileMsg(String seekingFile) {

        HashMap<String, String> filedata = getFileData(seekingFile);
        HashMap<String, SecretKey> keys = SplitFiles.keys;
        HashMap<String, IvParameterSpec> ivs = SplitFiles.IVs;

        System.out.println(keys);
        System.out.println(ivs);
        System.out.println(nodesList.size());

        int listeningPort = nodeDetails.getListeningPort();
        String ip = nodeDetails.getIp();

        String task = "FETCH";

        FileFetcher fileFetcher = new FileFetcher(task, ip, listeningPort, nodesList, filedata, keys, ivs, seekingFile, sock);
        Thread fileFetcherThread = new Thread(fileFetcher);
        fileFetcherThread.start();

    }

    public void sendRegistryRequestToMiner() throws UnknownHostException {

        String username = nodeDetails.getUsername();
        String ip = nodeDetails.getIp();
        int port = nodeDetails.getPort();
        int listeningPort = nodeDetails.getListeningPort();

        String msg = "REGISTER_REQUEST " + username + " " + ip + " " + port + " " + listeningPort;

        InetAddress bs_address = InetAddress.getByName(minerIP);
        Node.sendMsgViaSocket(sock, bs_address, 9801, msg);
    }

    public void sendLeaveSystemRequestToMiner() throws UnknownHostException {

        String username = nodeDetails.getUsername();
        String ip = nodeDetails.getIp();
        int port = nodeDetails.getPort();
        int listeningPort = nodeDetails.getListeningPort();

        String msg = "LEAVE_REQUEST " + username + " " + ip + " " + port + " " + listeningPort;
        InetAddress bs_address = InetAddress.getByName(minerIP);
        Node.sendMsgViaSocket(sock, bs_address, 9801, msg);
    }
}
