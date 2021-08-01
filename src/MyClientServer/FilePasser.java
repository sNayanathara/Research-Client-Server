package MyClientServer;

import java.io.*;
import java.net.Socket;

public class FilePasser implements Runnable {

    String task;
    int port;
    String fileChunkPath;
    File file;

    public FilePasser(String task, int fetcherListeningPort, String fileChunkPath) {
        this.task = task;
        this.port = fetcherListeningPort;
        this.fileChunkPath = fileChunkPath;
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
            setTask(socket);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
