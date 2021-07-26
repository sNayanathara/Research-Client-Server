package MyClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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

   public String setFilepathFromName(String filename) {
        String dir = "F:\\CopyFiles\\RecievedFiles\\";
        filepath = dir + filename;

       return filepath;
   }

    public String setFilepathFromFilepath(String filePath) {

        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        //System.out.println(fileName);
        String dir = "F:\\CopyFiles\\";

        filepath = dir + fileName;

        return filepath;
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
