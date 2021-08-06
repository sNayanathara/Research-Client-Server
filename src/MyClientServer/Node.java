package MyClientServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileNotFoundException;
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
    private List<File> fileList;

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
            System.out.println("sock0 " +sock);

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

                   String fileChunkPath = "F:\\CopyFiles\\RecievedFiles\\" + nodeUsername + "\\" + fileChunkName;

                   FilePasser filePasser = new FilePasser(task, receiverListeningPort, fileChunkPath);
                   Thread filePasserThread = new Thread(filePasser);
                   filePasserThread.start();

                } else if (command.equals("SEND_REQUEST")) {
                    String fileToBeAccepted = st.nextToken();
                    int nodeNumber = Integer.parseInt(st.nextToken());

                    System.out.println(command);
                    System.out.println(fileToBeAccepted + " " + nodeNumber);

                    acceptRequestMsg(fileToBeAccepted, nodeNumber, incoming);

                } else if (command.equals("OK_SEND")) {
                    String fileToBeSend = st.nextToken();
                    int listeningPort = Integer.parseInt(st.nextToken());
                    String nodeUsername = st.nextToken();

                    System.out.println(command);
                    System.out.println(fileToBeSend + " " + listeningPort);

                    sendFile(fileToBeSend, listeningPort, incoming, nodeUsername);

                } else if (command.equals("SEND")) {
                    String sendingFileName = st.nextToken();
                    int senderPort = Integer.parseInt(st.nextToken());
                    String nodeUsername = st.nextToken();

                    String task = "SEND";
//                    logger.log(Level.INFO, reqFilename + " " + receiverPort);
                    System.out.println(command);
                    System.out.println(sendingFileName + " " + senderPort);

                    FileFetcher fileFetcher = new FileFetcher(task, senderPort, sendingFileName, nodeUsername);
                    Thread fileFetcherThread = new Thread(fileFetcher);
                    fileFetcherThread.start();
                }
            }
        } catch(IOException e) {
            System.err.println("IOException " + e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }


    public void fileSendRequestMsg(String filePathOfSendingFile) throws UnknownHostException {
        SplitFiles fileSpliter = new SplitFiles(10,filePathOfSendingFile);
        fileList = fileSpliter.splitFile(filePathOfSendingFile);

        String msg;
        int count = 1;
        for(File file: fileList) {
            String fileNameofChunk = file.getName();
            String nodeUsername = nodesList.get(count).getUsername();
            msg = "SEND_REQUEST " + fileNameofChunk + " " + count;
            InetAddress bs_address = InetAddress.getByName(nodesList.get(count).getIp());
            sendMsgViaSocket(sock, bs_address, nodesList.get(count).getPort(), msg);
            count++;
        }
//        String task = "SEND REQUEST";
//        FilePasser filePasser = new FilePasser(task, filePathOfSendingFile, nodesList, sock);
//        Thread filePasserThread = new Thread(filePasser);
//        filePasserThread.start();

    }

    public void acceptRequestMsg(String fileToBeAccepted, int nodeNumber, DatagramPacket incoming) throws UnknownHostException {

        String nodeUsername = nodesList.get(nodeNumber).getUsername();

        String msg = "OK_SEND " + fileToBeAccepted + " " + nodesList.get(nodeNumber).getListeningPort() + " " + nodeUsername;
        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
    }

    public void sendFile(String fileName, int listeningPort, DatagramPacket incoming , String nodeUsername) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        String msg = "SEND " + fileName + " " + listeningPort + " " + nodeUsername;
        String task = "SEND";

        for (File file: fileList) {
            int count = 0;
            String currentFileName = file.getName();
            if (currentFileName.equals(fileName)) {

                InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
                sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);

                FilePasser filePasser = new FilePasser(task, listeningPort, file);
                Thread filePasserThread = new Thread(filePasser);
                filePasserThread.start();
            }
        }
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
//        HashMap<String, SecretKey> keys = storeKeyTemp();
//        HashMap<String, IvParameterSpec> ivs = storeIVTemp();
        System.out.println(nodesList.size());

        int listeningPort = nodesList.get(0).getListeningPort();
        String task = "FETCH";

        //FileFetcher fileFetcher = new FileFetcher(task, listeningPort, nodesList, filedata, keys, ivs, seekingFile, sock);
        //Thread fileFetcherThread = new Thread(fileFetcher);
        //fileFetcherThread.start();

    }
}
