package MyClientServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//Why utility class
public class Node implements Runnable {

    private Logger logger = LogManager.getLogManager().getLogger("a");
    DatagramSocket sock = null;

    private NodeDetails nodeDetails;
    private ArrayList<NodeDetails> nodesList = new ArrayList<>();
    private List<File> fileList;

    public Node(NodeDetails nodeDetails) {
        this.nodeDetails = nodeDetails;
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
        String s;
        try {
            sock = new DatagramSocket(nodeDetails.getPort());

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
                    //System.out.println("Node " + nodeNumber+ "->" +nodesList.get(nodeNumber).getPort());

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

//                    FileReceiver fileReceiver = new FileReceiver(senderPort);
//                    fileReceiver.setFilepathFromName(sendingFileName, nodeNumber );
//                    Thread fileReceiverThread = new Thread(fileReceiver);
//                    fileReceiverThread.start();

                }
            }
        } catch(IOException e) {
            System.err.println("IOException " + e);
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
            msg = "SEND_REQUEST " + fileNameofChunk + " " + count;    //thamangema listening pot eka hoyagnna
            InetAddress bs_address = InetAddress.getByName(nodesList.get(count).getIp());
            sendMsgViaSocket(sock, bs_address, nodesList.get(count).getPort(), msg);
            count++;
        }
    }

    public void acceptRequestMsg(String fileToBeAccepted, int nodeNumber, DatagramPacket incoming) throws UnknownHostException {

        String nodeUsername = nodesList.get(nodeNumber).getUsername();

        String msg = "OK_SEND " + fileToBeAccepted + " " + nodesList.get(nodeNumber).getListeningPort() + " " + nodeUsername;
        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
    }

    public void sendFile(String fileName, int listeningPort, DatagramPacket incoming , String nodeUsername) throws FileNotFoundException, UnknownHostException {

        String msg = "SEND " + fileName + " " + listeningPort + " " + nodeUsername;
        String task = "SEND";

        for (File file: fileList) {
            String currentFileName = file.getName();
            if (currentFileName.equals(fileName)) {
                InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
                sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);

//                FileSender fileSender = new FileSender(listeningPort);
//                fileSender.getFileToSend(file);
//                Thread fileSenderThread = new Thread(fileSender);
//                fileSenderThread.start();
                FilePasser filePasser = new FilePasser(task, listeningPort, file);
                Thread filePasserThread = new Thread(filePasser);
                filePasserThread.start();

            }
        }
    }

    public static void sendMsgViaSocket(DatagramSocket socket, InetAddress bs_address, int port, String msg) {
        try {
            DatagramPacket out_packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, bs_address, port);
            socket.send(out_packet);
        } catch (Exception e) {
            //logger.log(Level.INFO, e.toString());
        }
    }
    public HashMap<String, String> getFileData(String seekingFile) {
        HashMap<String, String> filedata = new HashMap<>();
        System.out.println("inside");
        //System.out.println(nodesList.get(1));
        Node node1 = new Node(nodesList.get(1));
        Node node2 = new Node(nodesList.get(2));
        Node node3 = new Node(nodesList.get(3));
        Node node4 = new Node(nodesList.get(4));
        Node node5 = new Node(nodesList.get(5));

        filedata.put(node1.nodeDetails.getUsername(), "marsland.ml-alg-perspect.09_part_0.pdf");
        filedata.put(node2.nodeDetails.getUsername(), "marsland.ml-alg-perspect.09_part_1.pdf");
        filedata.put(node3.nodeDetails.getUsername(), "marsland.ml-alg-perspect.09_part_2.pdf");
        filedata.put(node4.nodeDetails.getUsername(), "marsland.ml-alg-perspect.09_part_3.pdf");
        filedata.put(node5.nodeDetails.getUsername(), "marsland.ml-alg-perspect.09_part_4.pdf");

        System.out.println("HashMap");
        return filedata;

    }

    public void fetchFileMsg(String seekingFile) {
        HashMap<String, String> filedata = getFileData(seekingFile);
        System.out.println(nodesList.size());

        int listeningPort = nodesList.get(0).getListeningPort();
        String task = "FETCH";

        FileFetcher fileFetcher = new FileFetcher(task, listeningPort, nodesList, filedata, seekingFile, sock);
        Thread fileFetcherThread = new Thread(fileFetcher);
        fileFetcherThread.start();

//        String msg;
//        int count = 1;
//        for (int i = 1; i < nodesList.size();i++) {
//            String username = nodesList.get(i).getUsername();
//            String ip = nodesList.get(i).getIp();
//            int port = nodesList.get(i).getPort();
//            String fileChunkName = filedata.get(username);
//
//            System.out.println("Inside fetchFileMsg " + username + " " + port + " --> " + fileChunkName);
//            msg = "FETCH_REQUEST " + fileChunkName + " " + seekingFile + " " + username;
//
//            InetAddress bs_address = InetAddress.getByName(ip);
//            sendMsgViaSocket(sock, bs_address, port, msg);
//            count++;
//        }
    }

//    public void fetchFileAvailabilityMsg(String seekingFileName, String fileChunkName, String nodeUsername, DatagramPacket incoming) throws UnknownHostException {
//        String msg = "";
//        //String fileChunkPath = "F:\\CopyFiles\\RecievedFiles\\node_" + nodeNumber + "/" + fileChunkName;
//        String dirPath = "F:\\CopyFiles\\RecievedFiles\\"+ nodeUsername;
//        File dir = new File(dirPath);
//        File[] directoryListing = dir.listFiles();
//
//        if (directoryListing != null) {
//            for (File file : directoryListing) {
//                String fileName = file.getName();
//                System.out.println("seekin_ " +nodeUsername + "->" +fileName);
//                System.out.println("chunk_ " +nodeUsername + "->" + fileChunkName);
//                if (fileName.equals(fileChunkName)) {
////                    msg = "FETCH_AVAILABLE " + seekingFileName + " " + fileChunkName + " " + nodeUsername;
////                    InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
////                    sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
//                }
//            }
//        }
//    }
//
//    public void fetchFile(String seekingFileName, String fileChunkName, String nodeUsername , DatagramPacket incoming) throws UnknownHostException {
//
//        int listeningPort = nodesList.get(0).getListeningPort();
//        System.out.println(listeningPort);
//        HashMap<String, String> filedata = getFileData(seekingFileName);
//        int numberOfFileChunks = filedata.size();
//        int fileIndex = getFileIndex(fileChunkName); //file order eka hoyagnna use kre.methana node nunmber eka gattata wenama file index ekak wge dila kranna ona
//
//        String msg = "FETCH " + listeningPort + " " + seekingFileName + " " + fileChunkName + " " + nodeUsername;
//        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
//        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
//
//       // File file = new File(folderPath + "/" + fileChunkName);
//       // fetchedFiles.add(fileChunkNumber, file);
//
//        FileReceiver fileReceiver = new FileReceiver(listeningPort);
//        fileReceiver.setFilepathFromSeekingFile(seekingFileName, fileChunkName, fileIndex, numberOfFileChunks);
//        Thread fileReceiverThread = new Thread(fileReceiver);
//        fileReceiverThread.start();
//    }
//
//    public int getFileIndex(String fileChunkName) {
//        String index = fileChunkName.substring(fileChunkName.lastIndexOf("_") + 1, fileChunkName.lastIndexOf("."));
//        int indexNumber = Integer.parseInt(index);
//        System.out.println("getFileIndex "+indexNumber);
//
//        return indexNumber;
//    }
}
