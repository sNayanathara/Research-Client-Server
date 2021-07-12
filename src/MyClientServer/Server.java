package MyClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread {

    private int port;
//    String receivedFileName = "";

    public Server(int port) {
        this.port = port;
//        this.receivedFileName = receivedFileName;
    }

    public void startServer() throws IOException {
        String dirPath = "F:\\CopyFiles";
        ServerSocket serverSocket = new ServerSocket(port);
        Socket connectionSocket = serverSocket.accept();

        System.out.println("Connected to client");
        System.out.println("Accepted connection -> "+connectionSocket);

        BufferedInputStream bis = new BufferedInputStream(connectionSocket.getInputStream());
        DataInputStream dis = new DataInputStream(bis);

        int filesCount = dis.readInt();
        File[] files = new File[filesCount];

        for(int i = 0; i < filesCount; i++)
        {
            long fileLength = dis.readLong();
            String fileName = dis.readUTF();

            files[i] = new File(dirPath + "/" + fileName);

            FileOutputStream fos = new FileOutputStream(files[i]);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            for(int j = 0; j < fileLength; j++) bos.write(bis.read());

            bos.close();
        }

        dis.close();
        if (connectionSocket != null) connectionSocket.close();
        if (serverSocket != null) serverSocket.close();

        //String receivedFileName = "";
//        int fileSize = 6022386;
//        int bytesRead;
//        int current = 0;
//        FileOutputStream fileOutputStream = null;
//        BufferedOutputStream bufferedOutputStream = null;
//
//        //receive file
//        byte[] mybytearray = new byte[fileSize];
//        InputStream inputStream = connectionSocket.getInputStream();
//        fileOutputStream = new FileOutputStream(receivedFileName);
//        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
//        bytesRead = inputStream.read(mybytearray, 0, mybytearray.length);
//        current = bytesRead;
//
//        do {
//            bytesRead = inputStream.read(mybytearray, current, (mybytearray.length-current));
//            if (bytesRead >= 0) {
//                current += bytesRead;
//            }
//        } while (bytesRead > -1);
//
//        bufferedOutputStream.write(mybytearray, 0, current);
//        bufferedOutputStream.flush();
//        System.out.println("File " + receivedFileName + "downloaded ->" + current + "bytes read");
//
//        if (fileOutputStream != null) fileOutputStream.close();
//        if (bufferedOutputStream != null) bufferedOutputStream.close();
//        if (connectionSocket != null) connectionSocket.close();
//        if (serverSocket != null) serverSocket.close();
    }

    @Override
    public void run() {

        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.run();
    }


    public static void main(String[] args) throws IOException {

        Server server1 = new Server(3248);
//        Server server2 = new Server(3244, "image2.pdf");
//        Server server3 = new Server(3246, "image3.pdf");

        server1.start();
//        server2.start();
//        server3.start();


    }
}
