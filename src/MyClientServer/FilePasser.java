package MyClientServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FilePasser implements Runnable {

    String nodeUsername;
    String fileName;
    String task;
    int port;
    String fileChunkPath;
    ArrayList<NodeDetails> nodesList;
    String filePathOfSendingFile;
    DatagramSocket sock;
    static List<File> fileList;
    DatagramPacket incoming;


    public FilePasser(String task, int fetcherListeningPort, String fileChunkPath) {
        this.task = task;
        this.port = fetcherListeningPort;
        this.fileChunkPath = fileChunkPath;
    }

    public FilePasser(String task, String fileName, int listeningPort, DatagramPacket incoming, String nodeUsername) {
        this.task = task;
        this.fileName = fileName;
        this.port = listeningPort;
        this.incoming = incoming;
        this.nodeUsername = nodeUsername;
    }

    public FilePasser(String task, String filePathOfSendingFile, ArrayList<NodeDetails> nodesList, DatagramSocket sock) {
        this.task = task;
        this.filePathOfSendingFile = filePathOfSendingFile;
        this.nodesList = nodesList;
        this.sock = sock;

        SplitFiles fileSpliter = new SplitFiles(10, filePathOfSendingFile);
        fileList = fileSpliter.splitFile(filePathOfSendingFile);
    }


    public void setTask(Socket socket) throws IOException {
        if (task.equals("FETCH")) {
            sendFilesForFetchRequest(socket);
        } else {
            sendFileToNodes(socket);
        }
    }

    public void sendFilesForFetchRequest(Socket socket) throws IOException {

        FileSender fileSender = new FileSender();
        fileSender.getFileFromPath(fileChunkPath);
        fileSender.fetch_and_SendFile(socket);
    }

    public void passFileSendRequestMsg() throws UnknownHostException {

        String msg;
        int count = 1;
        for (File file : fileList) {
            String fileNameofChunk = file.getName();
            String nodeUsername = nodesList.get(count).getUsername();
            msg = "SEND_REQUEST " + fileNameofChunk + " " + count;
            InetAddress bs_address = InetAddress.getByName(nodesList.get(count).getIp());
            Node.sendMsgViaSocket(sock, bs_address, nodesList.get(count).getPort(), msg);
            count++;
        }
    }

    public void sendFileToNodes(Socket socket) throws IOException {

        for (File file : fileList) {
            int count = 0;
            String currentFileName = file.getName();
            if (currentFileName.equals(fileName)) {
                passFilesForSendRequest(socket, file);
            }
        }

        emptyDirectory("temp/");

    }

    public void emptyDirectory(String filePath)  //to empty the temp folder
    {
        File directory = new File(filePath);
        for (File files : directory.listFiles()) {
            if (!files.isDirectory())
                files.delete();
        }
    }
    public void passFilesForSendRequest(Socket socket, File file) throws IOException {
        FileSender fileSender = new FileSender();
        fileSender.getFileToSend(file);
        fileSender.fetch_and_SendFile(socket);
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", port)) {
            setTask(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

