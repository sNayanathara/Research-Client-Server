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

    public void sendFile(Socket connectionSocket, InputStream inputStream) throws IOException {
        FileInputStream is = null;
        OutputStream out = null;
        String dir = "F:\\";
        try {
            //InputStream sin = connectionSocket.getInputStream();
            DataInputStream dis = new DataInputStream(inputStream);
            String location = dis.readUTF();
            System.out.println("location=" + location);
            File toSend = new File(dir + location);
            // TODO: validate file is safe to access here
            if (!toSend.exists()) {
                System.out.println("File does not exist");
                return;
            }
            is = new FileInputStream(toSend);
            out = connectionSocket.getOutputStream();
            int bytesRead;
            byte[] buffer = new byte[4096];
            while ((bytesRead = is.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch(IOException e) {
                }
            if (is != null)
                try {
                    is.close();
                } catch(IOException e) {
                }
            //connectionSocket.close();
        }
    }

    public void receiveFiles(InputStream inputStream) throws IOException {
        String dirPath = "F:\\CopyFiles";

//        InputStream inputStream = connectionSocket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        DataInputStream dis = new DataInputStream(bis);

        int filesCount = dis.readInt();
        File[] files = new File[filesCount];

        for(int i = 0; i < filesCount; i++)
        {
            long fileLength = dis.readLong();
            String fileName = dis.readUTF();
            System.out.println(fileName);

            files[i] = new File(dirPath + "/" + fileName);

            FileOutputStream fos = new FileOutputStream(files[i]);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            for(int j = 0; j < fileLength; j++) bos.write(bis.read());

            bos.close();
        }


        dis.close();
    }

    public char findTask(InputStream inputStream) throws IOException {

      //  InputStream inputStream = connectionSocket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        DataInputStream dis = new DataInputStream(bis);

        char task = dis.readChar();
        System.out.println(task);

        return task;

    }

    public void startServer() throws IOException {
        //String dirPath = "F:\\CopyFiles";
        ServerSocket serverSocket = new ServerSocket(port);
        Socket connectionSocket = serverSocket.accept();

        InputStream inputStream = connectionSocket.getInputStream();
//        BufferedInputStream bis = new BufferedInputStream(inputStream);
//        DataInputStream dis = new DataInputStream(bis);
//
       // char getTask = findTask(inputStream);
        //System.out.println(getTask);

        System.out.println("Connected to client");
        System.out.println("Accepted connection -> "+connectionSocket);

        receiveFiles(inputStream);
        //sendFile(connectionSocket/**, dis**/);

//        if (getTask == 'A') {
//            receiveFiles(inputStream);
//            System.out.println(getTask);
//        } else if (getTask == 'B'){
//            sendFile(connectionSocket, inputStream);
//            System.out.println(getTask);
//        } else {
//            System.out.println("No matching task found");
//        }


//
//        BufferedInputStream bis = new BufferedInputStream(connectionSocket.getInputStream());
//        DataInputStream dis = new DataInputStream(bis);
//
//        int filesCount = dis.readInt();
//        File[] files = new File[filesCount];
//
//        for(int i = 0; i < filesCount; i++)
//        {
//            long fileLength = dis.readLong();
//            String fileName = dis.readUTF();
//            System.out.println(fileName);
//
//            files[i] = new File(dirPath + "/" + fileName);
//
//            FileOutputStream fos = new FileOutputStream(files[i]);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//            for(int j = 0; j < fileLength; j++) bos.write(bis.read());
//
//            bos.close();
//        }
//
//
//        dis.close();
        if (connectionSocket != null) connectionSocket.close();
        if (serverSocket != null) serverSocket.close();

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
