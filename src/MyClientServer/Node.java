package MyClientServer;

import java.io.File;
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
    //private int fileCount;

    public Node(NodeDetails nodeDetails) {
        this.nodeDetails = nodeDetails;
    }

    public void addNodesToNodeList() {  //a temp function to add the nodes to the ArrayList -> nodeList
        //nodesList.add(new NodeDetails("localhost", 9990));
        nodesList.add(new NodeDetails("localhost", 9991));
        nodesList.add(new NodeDetails("localhost", 9992));
        nodesList.add(new NodeDetails("localhost", 9993));
        nodesList.add(new NodeDetails("localhost", 9994));
        nodesList.add(new NodeDetails("localhost", 9995));
//        nodesList.add(new NodeDetails("localhost", 9996));
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
//                s = formatInputString(new String(data, 0, incoming.getLength()));
                StringTokenizer st = new StringTokenizer(new String(data, 0, incoming.getLength()), " ");
                String command = st.nextToken();

                System.out.println(command);

                if (command.equals("FETCH")) {
//                  logger.log(Level.INFO, "Msg received");
                    String reqFilename = st.nextToken();
                    int receiverPort = Integer.parseInt(st.nextToken());
//                  logger.log(Level.INFO, reqFilename + " " + receiverPort);
                    System.out.println(reqFilename + " " + receiverPort);
                    FileSender fileSender = new FileSender(receiverPort);
                    fileSender.getFileFromName(reqFilename);
                    Thread fileSenderThread = new Thread(fileSender);
                    fileSenderThread.start();

                } else if (command.equals("SEND_REQUEST")) {
                    String fileToBeAccepted = st.nextToken();

                } else if (command.equals("SEND")) {
                    String sendingFilepath = st.nextToken();
                    int senderPort = Integer.parseInt(st.nextToken());
//                    logger.log(Level.INFO, reqFilename + " " + receiverPort);
                    System.out.println(sendingFilepath + " " + senderPort);
                    FileReceiver fileReceiver = new FileReceiver(senderPort);
                    fileReceiver.setFilepathFromFilepath(sendingFilepath);
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

//    public void fetchFiles(HashMap<Node, String> filedata) {
//        String msg;
//        Iterator it = filedata.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            msg = "FETCH " + pair.getValue() + " " + ;
//
//        }
//    }

    public void fetchFile(Node node, String filename) {
        String msg = "FETCH " + filename + " " + 9997;
        try {
            FileReceiver fileReceiver = new FileReceiver(9997);
            fileReceiver.setFilepathFromName(filename);
            Thread fileReceiverThread = new Thread(fileReceiver);
            fileReceiverThread.start();
            InetAddress bs_address = InetAddress.getByName(node.nodeDetails.getIp());
            sendMsgViaSocket(sock, bs_address, node.nodeDetails.getPort(), msg);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void fileSendRequestMsg(String filePathOfSendingFile) throws UnknownHostException {
        SplitFiles fileSpliter = new SplitFiles(10,filePathOfSendingFile);
        List<File> fileList = fileSpliter.splitFile(filePathOfSendingFile);
        //int fileCount = fileList.size();

        String msg;
        int count = 0;
        for(File file: fileList) {
            String fileNameofChunk = file.getName();
            msg = "SEND_REQUEST " + fileNameofChunk;
            InetAddress bs_address = InetAddress.getByName(nodesList.get(count).getIp());
            sendMsgViaSocket(sock, bs_address, nodesList.get(count).getPort(), msg);
            count++;
        }
    }

//    public void acceptRequestMsg(String fileToBeAccepted) {
//        String msg =
//    }

    public void sendFile(Node node, String filepath) {
        String msg = "SEND " + filepath + " " + 9997;
        try {
            InetAddress bs_address = InetAddress.getByName(node.nodeDetails.getIp());
            sendMsgViaSocket(sock, bs_address, node.nodeDetails.getPort(), msg);

            FileSender fileSender = new FileSender(9997);
            fileSender.getFileFromPath(filepath);
            Thread fileSenderThread = new Thread(fileSender);
            fileSenderThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMsgViaSocket(DatagramSocket socket, InetAddress bs_address, int port, String msg) {
        try {
            DatagramPacket out_packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, bs_address, port);
            socket.send(out_packet);
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
        }
    }

}
