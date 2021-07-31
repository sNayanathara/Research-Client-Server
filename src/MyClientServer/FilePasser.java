package MyClientServer;

import java.io.*;
import java.net.Socket;

public class FilePasser implements Runnable {

    String task;
    int port;
    String fileChunkPath;
    File file;
//    String seekingFileName;
//    String nodeUsername;


    public FilePasser(String task, int fetcherListeningPort, String fileChunkPath) {
        this.task = task;
        this.port = fetcherListeningPort;
        this.fileChunkPath = fileChunkPath;
//        this.seekingFileName = seekingFileName;
//        this.nodeUsername = nodeUsername;
    }

    public FilePasser(String task, int listeningPort, File file) {
        this.task = task;
        this.port = listeningPort;
        this.file = file;
    }


    public void setTask(Socket socket) throws IOException {
        if (task.equals("FETCH")) {
            sendFilesForFetchRequest(socket);
        } else {
            passFilesForSendRequest(socket);
        }
    }

    public void sendFilesForFetchRequest(Socket socket) throws IOException {

        FileSender fileSender = new FileSender();
        fileSender.getFileFromPath(fileChunkPath);
        fileSender.fetch_and_SendFile(socket);
    }

    public void passFilesForSendRequest(Socket socket) throws IOException {
        FileSender fileSender = new FileSender();
        fileSender.getFileToSend(file);
        fileSender.fetch_and_SendFile(socket);
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", port)) {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            // FileInputStream fileToSend = getFileToSend(dataInputStream);
            setTask(socket);

            dataInputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
