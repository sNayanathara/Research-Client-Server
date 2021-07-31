package MyClientServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class FileFetcher implements Runnable {

    String task;
    int port;
    ArrayList<NodeDetails> nodesList;
    HashMap<String, String> filedata;
    String seekingFile;
    DatagramSocket sock;
    String sendingFileName;
    String nodeUsername;


    public FileFetcher(String task, int port, ArrayList<NodeDetails> nodesList, HashMap<String, String> filedata, String seekingFile, DatagramSocket sock) {
        this.task = task;
        this.port = port;
        this.nodesList = nodesList;
        this.filedata = filedata;
        this.seekingFile = seekingFile;
        this.sock = sock;
    }

    public FileFetcher(String task, int senderPort, String sendingFileName, String nodeUsername) {
        this.task = task;
        this.port = senderPort;
        this.sendingFileName = sendingFileName;
        this.nodeUsername = nodeUsername;
    }

    public void selectTask(ServerSocket socket) throws IOException {

        if (task.equals("FETCH")) {
            sendFetchRequestToNodes(socket);
        } else {
            sendFileChunksToNodes(socket);
        }

    }

    private void sendFileChunksToNodes(ServerSocket socket) throws IOException {
        System.out.println("sendFileChunksToNodes");
        FileReceiver fileReceiver = new FileReceiver(socket);
        fileReceiver.setFilepathFromName(sendingFileName, nodeUsername );
        fileReceiver.getFile(socket);
    }

    public void sendFetchRequestToNodes(ServerSocket socket) throws IOException {
        System.out.println("Nodelist size " +nodesList.size());
        for (int i = 1; i< nodesList.size(); i++) {
            String nodeUsername = nodesList.get(i).getUsername();
            int nodePort = nodesList.get(i).getPort();
            String nodeIP = nodesList.get(i).getIp();

            System.out.println(nodeUsername);
            System.out.println(nodePort);
            System.out.println(nodeIP);

            System.out.println(port);

            String fileChunk = filedata.get(nodeUsername);
            String msg = "FETCH " + port + " " + fileChunk + " " + seekingFile + " " + nodeUsername;

            InetAddress bs_address = InetAddress.getByName(nodeIP);
            Node.sendMsgViaSocket(sock, bs_address, nodePort, msg);

            FileReceiver fileReceiver = new FileReceiver(socket);
            fileReceiver.setFilepathFromSeekingFile(seekingFile);
            fileReceiver.getFile(socket);

            socket.close();
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);
            selectTask(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
