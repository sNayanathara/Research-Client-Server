package MyClientServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//Why utility class
public class Node implements Runnable {
    
//    private int port;
//    private String address;
    private Logger logger = LogManager.getLogManager().getLogger("a");
    DatagramSocket sock = null;

    private NodeDetails nodeDetails;
    private ArrayList<NodeDetails> nodesList = new ArrayList<>();
    private List<File> fileList;
    //private int fileCount;

    public Node(NodeDetails nodeDetails) {
        this.nodeDetails = nodeDetails;
    }

    public void addNodesToNodeList() {  //a temp function to add the nodes to the ArrayList -> nodeList
        nodesList.add(new NodeDetails("localhost",9990, 9980));
        nodesList.add(new NodeDetails("localhost",9991, 9981));
        nodesList.add(new NodeDetails("localhost",9992, 9982));
        nodesList.add(new NodeDetails("localhost",9993, 9983));
        nodesList.add(new NodeDetails("localhost",9994, 9984));
        nodesList.add(new NodeDetails("localhost",9995, 9985));
        nodesList.add(new NodeDetails("localhost",9996, 9986));
//        nodesList.add(new NodeDetails("localhost", 9997));
//        nodesList.add(new NodeDetails("localhost", 9998));
//        nodesList.add(new NodeDetails("localhost", 9999));
        //System.out.println(nodesList);
    }
    public void run() {
        String s;
        try {
            sock = new DatagramSocket(nodeDetails.getPort());
            addNodesToNodeList();

            while(true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
//                s = formatInputString(new String(data, 0, incoming.getLength()));
                StringTokenizer st = new StringTokenizer(new String(data, 0, incoming.getLength()), " ");
                String command = st.nextToken();

                //System.out.println(command);

//                if (command.equals("FETCH")) {
////                  logger.log(Level.INFO, "Msg received");
//                    String reqFilename = st.nextToken();
//                    int receiverPort = Integer.parseInt(st.nextToken());
////                  logger.log(Level.INFO, reqFilename + " " + receiverPort);
//                    System.out.println(reqFilename + " " + receiverPort);
//                    FileSender fileSender = new FileSender(receiverPort);
//                    fileSender.getFileFromName(reqFilename);
//                    Thread fileSenderThread = new Thread(fileSender);
//                    fileSenderThread.start();
//
//                } else
               if (command.equals("FETCH_REQUEST")) {

                   System.out.println("FETCH_REQUEST");

                    String fileChunkName = st.nextToken();
                    String seekingFileName = st.nextToken();
                    int nodeNumber = Integer.parseInt(st.nextToken());

                    fetchFileAvailabilityMsg(seekingFileName, fileChunkName, nodeNumber, incoming);

                }else if (command.equals("FETCH_AVAILABLE")) {

                   System.out.println("FETCH_AVAILABLE");

                   String seekingFileName = st.nextToken();
                   String fileChunkName = st.nextToken();
                   //int fileChunkNumber = Integer.parseInt(st.nextToken());
                   //int listeningPort = Integer.parseInt(st.nextToken());
                   int nodeNumber = Integer.parseInt(st.nextToken());

                   fetchFile(seekingFileName, fileChunkName, nodeNumber, incoming);

                } else if (command.equals("FETCH_UNAVAILABLE")) {

                   System.out.println("FETCH_UNAVAILABLE");

                   String seekingFileName = st.nextToken();
                   String fileChunkName = st.nextToken();
                   System.out.println("Fetch for " + seekingFileName + "not found -> " + fileChunkName);

                }else if (command.equals("FETCH")) {

                   System.out.println("FETCH");

                    int listeningPort = Integer.parseInt(st.nextToken());
                    String seekingFileName = st.nextToken();
                    String fileChunkName = st.nextToken();
                    int nodeNumber = Integer.parseInt(st.nextToken());

                    String fileChunkPath = "F:\\CopyFiles\\RecievedFiles\\node_" + nodeNumber + "/" + fileChunkName;

                    FileSender fileSender = new FileSender(listeningPort);
                    fileSender.getFileFromPath(fileChunkPath);
                    Thread fileSenderThread = new Thread(fileSender);
                    fileSenderThread.start();

                } else if (command.equals("SEND_REQUEST")) {
                    String fileToBeAccepted = st.nextToken();
                    int nodeNumber = Integer.parseInt(st.nextToken());

                    System.out.println(command);
                    System.out.println(fileToBeAccepted + " " + nodeNumber);

                    acceptRequestMsg(fileToBeAccepted, nodeNumber, incoming);

                } else if (command.equals("OK_SEND")) {
                    String fileToBeSend = st.nextToken();
                    int listeningPort = Integer.parseInt(st.nextToken());
                    int nodeNumber = Integer.parseInt(st.nextToken());

                    System.out.println(command);
                    System.out.println(fileToBeSend + " " + listeningPort);

                    sendFile(fileToBeSend, listeningPort, incoming, nodeNumber);

                } else if (command.equals("SEND")) {
                    String sendingFileName = st.nextToken();
                    int senderPort = Integer.parseInt(st.nextToken());
                    int nodeNumber = Integer.parseInt(st.nextToken());
//                    logger.log(Level.INFO, reqFilename + " " + receiverPort);
                    System.out.println(command);
                    System.out.println(sendingFileName + " " + senderPort);

                    FileReceiver fileReceiver = new FileReceiver(senderPort);
                    fileReceiver.setFilepathFromName(sendingFileName, nodeNumber );
                    Thread fileReceiverThread = new Thread(fileReceiver);
                    fileReceiverThread.start();

                } else if (command.equals("UNROK")) {
                    int status = Integer.parseInt(st.nextToken());
                    if (status == 0) {
                        logger.log(Level.INFO, "Successfully unregistered with the BS...");
                    } else if (status == 9999) {
                        logger.log(Level.INFO, "Error while unregistering. IP and port may not be in the registry or command is incorrect...");
                    }
                } else if (command.equals("JOIN")) {
                }
            }
        } catch(IOException e) {
            System.err.println("IOException " + e);
        }
    }


//    public void fetchFile(Node node, String filename) {
//        String msg = "FETCH " + filename + " " + 9997;
//        try {
//            FileReceiver fileReceiver = new FileReceiver(9997);
//            fileReceiver.setFilepathFromName(filename);
//            Thread fileReceiverThread = new Thread(fileReceiver);
//            fileReceiverThread.start();
//            InetAddress bs_address = InetAddress.getByName(node.nodeDetails.getIp());
//            sendMsgViaSocket(sock, bs_address, node.nodeDetails.getPort(), msg);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//    }

    public void fileSendRequestMsg(String filePathOfSendingFile) throws UnknownHostException {
        SplitFiles fileSpliter = new SplitFiles(10,filePathOfSendingFile);
        fileList = fileSpliter.splitFile(filePathOfSendingFile);
        //int fileCount = fileList.size();

        String msg;
        int count = 1;
        for(File file: fileList) {
            String fileNameofChunk = file.getName();
            msg = "SEND_REQUEST " + fileNameofChunk + " " + count;    //thamangema listening pot eka hoyagnna
            InetAddress bs_address = InetAddress.getByName(nodesList.get(count).getIp());
            sendMsgViaSocket(sock, bs_address, nodesList.get(count).getPort(), msg);
            count++;
        }
    }

    public void acceptRequestMsg(String fileToBeAccepted, int nodeNumber, DatagramPacket incoming) throws UnknownHostException {

        String msg = "OK_SEND " + fileToBeAccepted + " " + nodesList.get(nodeNumber).getListeningPort() + " " + nodeNumber;
        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
    }

    public void sendFile(String fileName, int listeningPort, DatagramPacket incoming , int nodeNumber) throws FileNotFoundException, UnknownHostException {

        String msg = "SEND " + fileName + " " + listeningPort + " " + nodeNumber;

        for (File file: fileList) {
            String currentFileName = file.getName();
            if (currentFileName.equals(fileName)) {
                InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
                sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);

                FileSender fileSender = new FileSender(listeningPort);
                fileSender.getFileToSend(file);
                Thread fileSenderThread = new Thread(fileSender);
                fileSenderThread.start();
            }
        }
    }

//    public void sendFile(Node node, String filepath) {
//        String msg = "SEND " + filepath + " " + 9997;
//        try {
//            InetAddress bs_address = InetAddress.getByName(node.nodeDetails.getIp());
//            sendMsgViaSocket(sock, bs_address, node.nodeDetails.getPort(), msg);
//
//            FileSender fileSender = new FileSender(9997);
//            fileSender.getFileFromPath(filepath);
//            Thread fileSenderThread = new Thread(fileSender);
//            fileSenderThread.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void sendMsgViaSocket(DatagramSocket socket, InetAddress bs_address, int port, String msg) {
        try {
            DatagramPacket out_packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, bs_address, port);
            socket.send(out_packet);
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
        }
    }
    public HashMap<NodeDetails, String> getFileData(String seekingFile) {
        HashMap<NodeDetails, String> filedata = new HashMap<>();
        System.out.println("inside");
        //System.out.println(nodesList.get(1));

        filedata.put(nodesList.get(1), "marsland.ml-alg-perspect.09.part_0.pdf");
        filedata.put(nodesList.get(2), "marsland.ml-alg-perspect.09.part_1.pdf");
        filedata.put(nodesList.get(3), "marsland.ml-alg-perspect.09.part_2.pdf");
        filedata.put(nodesList.get(4), "marsland.ml-alg-perspect.09.part_3.pdf");
        filedata.put(nodesList.get(5), "marsland.ml-alg-perspect.09.part_4.pdf");

        //System.out.println(filedata);

        return filedata;

    }

    public void fetchFileMsg(String seekingFile) throws UnknownHostException {
        HashMap<NodeDetails, String> filedata = getFileData(seekingFile);

        String msg;
        int count = 1;
        Iterator it = filedata.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            msg = "FETCH_REQUEST " + pair.getValue() + " " + seekingFile + " " + count;
            NodeDetails node = (NodeDetails) pair.getKey();
            InetAddress bs_address = InetAddress.getByName(node.getIp());
            sendMsgViaSocket(sock, bs_address, node.getPort(), msg);
            count++;
        }
    }

    public void fetchFileAvailabilityMsg(String seekingFileName, String fileChunkName, int nodeNumber, DatagramPacket incoming) throws UnknownHostException {
        String msg = "";
        //String fileChunkPath = "F:\\CopyFiles\\RecievedFiles\\node_" + nodeNumber + "/" + fileChunkName;
        String dirPath = "F:\\CopyFiles\\RecievedFiles\\node_" + nodeNumber;
        File dir = new File(dirPath);
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            for (File file : directoryListing) {
                String fileName = file.getName();
                System.out.println("seekin: " +fileName);
                System.out.println("chunk: " + fileChunkName);
                if (fileName.equals(fileChunkName)) {
                    msg = "FETCH_AVAILABLE " + seekingFileName + " " + fileChunkName + " " + nodeNumber;
                    break;
                }
            }
        }
//        File file = new File(fileChunkPath);
//        if(file.exists()) {
//            msg = "FETCH_AVAILABLE " + seekingFileName + " " + fileChunkName + " " + nodeNumber;
//        } else {
//            msg = "FETCH_UNAVAILABLE " + seekingFileName + " " + fileChunkName;
//        }
        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
    }


//    public void fileFetchRequestMsg(String fetchFileName) throws UnknownHostException {
//        int count = 1;
//
//        for (NodeDetails nodes: nodesList) {
//            String msg = "FETCH_REQUEST " + fetchFileName + " " + count;
//            InetAddress bs_address = InetAddress.getByName(nodes.getIp());
//            sendMsgViaSocket(sock, bs_address, nodes.getPort(), msg);
//            count ++;
//        }
//    }

//    public void fetchFileAvailabilityMsg(String seekingFileName, String fileChunkName, int nodeNumber, DatagramPacket incoming) throws UnknownHostException {
//
//        String msg;
//        String folderPath = "F:\\CopyFiles\\RecievedFiles\\node_" + nodeNumber;
//        String seekingFileWithoutExtension = seekingFileName.substring(0, seekingFileName.lastIndexOf("."));
//
//        File dir = new File(folderPath);
//        File[] directoryListing = dir.listFiles();
//        if (directoryListing != null) {
//            for (File child : directoryListing) {
//                String childName = child.getName();
//                int fileNumber = Integer.parseInt(childName.substring(childName.lastIndexOf("_"), childName.lastIndexOf(".")));
//                if (childName.contains(seekingFileWithoutExtension)) {
//                    msg = "FETCH_AVAILABLE " + seekingFileName + " " + childName + " " + fileNumber + " " + nodeNumber;
//                } else {
//                    msg = "FETCH_UNAVAILABLE " + nodeNumber;
//                }
//                InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
//                sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);
//            }
//        } else {
//            System.out.println("Empty Directory -> " + nodeNumber);
//        }
//    }

    public void fetchFile(String seekingFileName, String fileChunkName, int nodeNumber, DatagramPacket incoming) throws UnknownHostException {
        //List<File> fetchedFiles = new ArrayList<>();
       // String filePath = "F:\\CopyFiles\\RecievedFiles\\node_" + nodeNumber + fileChunkName;  //////////////////////////////////////////////////
        int listeningPort = nodesList.get(0).getListeningPort();
        HashMap<NodeDetails, String> filedata = getFileData(seekingFileName);
        int numberOfFileChunks = filedata.size();
        int fileIndex = nodeNumber; //file order eka hoyagnna use kre.methana node nunmber eka gattata wenama file index ekak wge dila kranna ona

        String msg = "FETCH " + listeningPort + " " + seekingFileName + " " + fileChunkName + " " + nodeNumber;
        InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
        sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);

       // File file = new File(folderPath + "/" + fileChunkName);
       // fetchedFiles.add(fileChunkNumber, file);

        FileReceiver fileReceiver = new FileReceiver(listeningPort);
        fileReceiver.setFilepathFromSeekingFile(seekingFileName, fileChunkName, fileIndex, numberOfFileChunks);
        Thread fileReceiverThread = new Thread(fileReceiver);
        fileReceiverThread.start();


    }
}
