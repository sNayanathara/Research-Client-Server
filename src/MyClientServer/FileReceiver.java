package MyClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReceiver {

    String filepath;

    public FileReceiver() {

    }

    public  void getFile(ServerSocket socket) throws IOException {

        Socket sss = socket.accept();

        int bytesRead;

        InputStream in=sss.getInputStream();
        OutputStream out=new FileOutputStream(filepath);
        byte[] buffer=new byte[1024];
        while((bytesRead=in.read(buffer))!=-1)
        {
            out.write(buffer,0,bytesRead);
        }
        out.close();
        sss.close();
        //socket.close();
   }

    public  void getFileToMerge(ServerSocket socket, OutputStream out) throws IOException {

        Socket sss = socket.accept();

        int bytesRead;

        InputStream in=sss.getInputStream();     //socket closed..
        //out=new FileOutputStream(filepath);
        byte[] buffer=new byte[1024];
        while((bytesRead=in.read(buffer))!=-1)
        {
            out.write(buffer,0,bytesRead);
        }
        sss.close();
        //socket.close();
    }


    public String setFilepathFromName(String filename, String nodeUserName) {
        String dir = "F:\\CopyFiles\\RecievedFiles\\";
        String folderName = nodeUserName;
        String folder = dir + folderName;
        File directory = new File(folder);
        if(! directory.exists()) {
            directory.mkdir();
        }
        filepath = directory + "\\" + filename;
        System.out.println(filepath);

       return filepath;
   }

//    public String setFilepathFromSeekingFile(String fileName) {
//
//        String dir = "F:\\CopyFiles\\";
//
//        filepath = dir + fileName;
//        System.out.println("Saving file to -> " + filepath);
//
//        return  filepath;
//
//
//    }

}
