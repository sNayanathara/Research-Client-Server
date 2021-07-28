package MyClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReceiver implements Runnable {

    int port;
    String filepath;

    public FileReceiver(int port) {
        //this.fileName = fileName;
        this.port = port;
    }

    public  void getFile(ServerSocket socket) throws IOException {

        Socket sss = socket.accept();

        int bytesRead;

        InputStream in=sss.getInputStream();     //socket closed..
        OutputStream out=new FileOutputStream(filepath);
        byte[] buffer=new byte[1024];
        while((bytesRead=in.read(buffer))!=-1)
        {
            out.write(buffer,0,bytesRead);
        }
        out.close();
   }

   public String setFilepathFromName(String filename, int folderNumber) {
        String dir = "F:\\CopyFiles\\RecievedFiles\\";
        String folderName = "node_" + folderNumber;
        String folder = dir + folderName;
        File directory = new File(folder);
        if(! directory.exists()) {
            directory.mkdir();
        }
        filepath = directory + "/" + filename;

       return filepath;
   }

    public String setFilepathFromSeekingFile(String fileName, String fileChunk, int fileIndex, int totalNumberOfFiles) {

        List<File> fetchedFiles = new ArrayList<>();
        File file = new File(fileChunk);
        fetchedFiles.add(fileIndex, file);
        int numberOfFilesReceived = fetchedFiles.size();
        String dir = "F:\\CopyFiles\\";

        if (numberOfFilesReceived == totalNumberOfFiles) {
            filepath = dir + fileName;
        }

        return  filepath;


    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);
            getFile(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
