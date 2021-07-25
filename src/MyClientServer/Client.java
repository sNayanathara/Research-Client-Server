package MyClientServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client extends Thread {

    private String ip;
    private int port;
   // private String filePath_ofFile_toSend;
    private int chunkSizeInMB;

    public Client(String ip, int port, int chunkSizeInMB) {
        this.ip = ip;
        this.port = port;
        //this.filePath_ofFile_toSend = filePath_ofFile_toSend;
        this.chunkSizeInMB = chunkSizeInMB;
    }

    public String getFileNameDetails(String filePath_ofFile_toSend) {
        String fileNameWithExtension;

        File inputFile = new File(filePath_ofFile_toSend);
        fileNameWithExtension = inputFile.getName();

        return fileNameWithExtension;

    }

    public String getFileChunkName(String filePath_ofFile_toSend, int chunkCount) {
        String chunkFileName;

        String fileName = getFileNameDetails(filePath_ofFile_toSend).substring(0, getFileNameDetails(filePath_ofFile_toSend).lastIndexOf("."));
        String chunkExtension = getFileNameDetails(filePath_ofFile_toSend).substring(getFileNameDetails(filePath_ofFile_toSend).lastIndexOf("."));
        chunkFileName = fileName + "_part_" + chunkCount + chunkExtension;

        return chunkFileName;
    }

    public int getFileSize(String filePath_ofFile_toSend){
        int fileSize;

        File inputFile = new File(filePath_ofFile_toSend);
        fileSize = (int) inputFile.length();

        return fileSize;
    }

//    public void send_splitFiles_toStore(byte[] byteChunkPart, Socket socket, String chunkFileName) throws IOException {
//        PrintWriter outputFile = new PrintWriter(chunkFileName);
////        FileOutputStream filePart = new FileOutputStream(new File("F:\\fileFolder\\" +chunkFileName));
//        OutputStream filePart = socket.getOutputStream();
//        filePart.write(byteChunkPart);
//        filePart.flush();
//        filePart.close();
//        byteChunkPart = null;
//        filePart = null;
//    }

    public List<File> splitFile(String filePath_ofFile_toSend) {

        int chunkCount = 0, read = 0, readLength = 1024 * 1024 * chunkSizeInMB;
        int fileSize = getFileSize(filePath_ofFile_toSend);
        List<File> files = new ArrayList<File>();
        byte[] byteChunkPart;
        String chunkFileName;
        FileInputStream inputStream;

        File inputFile = new File(filePath_ofFile_toSend);

        try {
            inputStream = new FileInputStream(inputFile);
            while (fileSize > 0) {
                chunkFileName = getFileChunkName(filePath_ofFile_toSend, chunkCount);
                File fileChunk = new File(chunkFileName);

                if (fileSize <= (1024 * 1024 * chunkSizeInMB)) {
                    readLength = fileSize;
                }

                byteChunkPart = new byte[readLength];
                read = inputStream.read(byteChunkPart, 0, readLength);
                fileSize -= read;
                assert (read == byteChunkPart.length);
                chunkCount++;

                FileOutputStream filePart = new FileOutputStream(fileChunk);
                OutputStream fileOut = new BufferedOutputStream(filePart);
                fileOut.write(byteChunkPart);
                filePart.flush();
                filePart.close();

                files.add(fileChunk);
                System.out.println(fileChunk);


                //System.out.println("Sending " + fileToSend + " -> " +mybytearray.length + "bytes");

                //send_splitFiles_toStore(byteChunkPart, socket, chunkFileName);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;

    }

    public void sendFiletoServer(Socket socket) throws IOException {

        Scanner getfilePath = new Scanner(System.in);

        System.out.println("Enter filepath of file to send");
        String filePath_ofFile_toSend = getfilePath.nextLine();

        List<File> fileList = splitFile(filePath_ofFile_toSend);
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        OutputStream outputStream = null;
        dataOutputStream.writeInt(fileList.size());

        for (File file : fileList) {
            System.out.println("Sending");
            long length = file.length();
            dataOutputStream.writeLong(length);

            String fileName = file.getName();
            dataOutputStream.writeUTF(fileName);

            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            int theByte = 0;
            while ((theByte = bufferedInputStream.read()) != -1) bufferedOutputStream.write(theByte);

            bufferedInputStream.close();
        }
        dataOutputStream.close();


        if (socket!=null) socket.close();
    }

        public void getFileFromServer(Socket socket) throws IOException {

        Scanner getFileName = new Scanner(System.in);

        System.out.println("Enter filename of the file you want");
        String interestFile = getFileName.nextLine();

        int bytesRead;

        OutputStream outputStream=socket.getOutputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(interestFile);
       // sdata.writeChars("Send");

//        sdata.close();
//        sout.close();

        InputStream in=socket.getInputStream();     //socket closed..
        OutputStream out=new FileOutputStream("F:\\CopyFiles\\RecievedFiles\\"+interestFile);
        byte[] buffer=new byte[1024];
        while((bytesRead=in.read(buffer))!=-1)
        {
            out.write(buffer,0,bytesRead);
        }
        out.close();
        socket.close();
    }

    public int setTask(Socket socket) throws IOException {

        OutputStream outputStream = socket.getOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object

        System.out.println("Enter what you want to do :");
        System.out.println("1) Upload file\n2) Retrieve file");

        int task = myObj.nextInt();  // Read user input
        System.out.println("Task is: " + task);  // Output user input

        if (task ==1) {
            dataOutputStream.writeChar('A');
            return task;
        } else {
            dataOutputStream.writeChar('B');
            return task;
        }

    }

    public void startClient() throws IOException {

        Socket socket =new Socket(ip, port);

//        OutputStream outputStream = socket.getOutputStream();
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
//        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

//        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
//
//        System.out.println("Enter what you want to do :");
//        System.out.println("1) Upload file\n2) Retrieve file");
//
//        int task = myObj.nextInt();  // Read user input
//        System.out.println("Task is: " + task);  // Output user input
        int task = setTask(socket);

        if (task == 1) {
            sendFiletoServer(socket);
        } else {
           // dataOutputStream.writeInt(2);
            getFileFromServer(socket);
        }

//*********   List<File> fileList = splitFile();
 //*******       System.out.println(fileList);

//        String fileToSend = "F:/image.pdf/";
//        Boolean whileCondition = true;
//********************************************************
//        FileInputStream fileInputStream = null;
//        BufferedInputStream bufferedInputStream = null;
//
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
//        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
////        OutputStream outputStream = null;
//        dataOutputStream.writeInt(fileList.size());
//
//        for (File file : fileList) {
//            System.out.println("Sending");
//            long length = file.length();
//            dataOutputStream.writeLong(length);
//
//            String fileName = file.getName();
//            dataOutputStream.writeUTF(fileName);
//
//            fileInputStream = new FileInputStream(file);
//            bufferedInputStream = new BufferedInputStream(fileInputStream);
//
//            int theByte = 0;
//            while ((theByte = bufferedInputStream.read()) != -1) bufferedOutputStream.write(theByte);
//
//            bufferedInputStream.close();
//        }
//        dataOutputStream.close();
//
//
//        if (socket!=null) socket.close();
//******************************************************************

    }

    @Override
    public void run() {

        try {
            startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.run();
    }

    public static void main(String[] args) throws IOException {

        Client client1 = new Client("127.0.0.1", 3248, 5);
//        Client client2 = new Client("127.0.0.1", 3244);
//        Client client3 = new Client("127.0.0.1", 3246);

        client1.start();
//        client2.start();
//        client3.start();

   }
}
