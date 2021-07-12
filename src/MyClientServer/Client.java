package MyClientServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread {

    private String ip;
    private int port;
    private String filePath_ofFile_toSend;
    private int chunkSizeInMB;

    public Client(String ip, int port, String filePath_ofFile_toSend, int chunkSizeInMB) {
        this.ip = ip;
        this.port = port;
        this.filePath_ofFile_toSend = filePath_ofFile_toSend;
        this.chunkSizeInMB = chunkSizeInMB;
    }

    public String getFileNameDetails() {
        String fileNameWithExtension;

        File inputFile = new File(filePath_ofFile_toSend);
        fileNameWithExtension = inputFile.getName();

        return fileNameWithExtension;

    }

    public String getFileChunkName(int chunkCount) {
        String chunkFileName;

        String fileName = getFileNameDetails().substring(0, getFileNameDetails().lastIndexOf("."));
        String chunkExtension = getFileNameDetails().substring(getFileNameDetails().lastIndexOf("."));
        chunkFileName = fileName + "_part_" + chunkCount + chunkExtension;

        return chunkFileName;
    }

    public int getFileSize(){
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

    public List<File> splitFile() {

        int chunkCount = 0, read = 0, readLength = 1024 * 1024 * chunkSizeInMB;
        int fileSize = getFileSize();
        List<File> files = new ArrayList<File>();
        byte[] byteChunkPart;
        String chunkFileName;
        FileInputStream inputStream;

        File inputFile = new File(filePath_ofFile_toSend);

        try {
            inputStream = new FileInputStream(inputFile);
            while (fileSize > 0) {
                chunkFileName = getFileChunkName(chunkCount);
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

    public void startClient() throws IOException {

        Socket socket =new Socket(ip, port);
        List<File> fileList = splitFile();
        System.out.println(fileList);

//        String fileToSend = "F:/image.pdf/";
//        Boolean whileCondition = true;
//
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
//        OutputStream outputStream = null;
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

//        while (whileCondition) {
//            try {
//                File myFile = new File(fileToSend);
//                byte[] mybytearray = new byte[(int)myFile.length()];
//                fileInputStream = new FileInputStream(myFile);
//                bufferedInputStream = new BufferedInputStream(fileInputStream);
//                bufferedInputStream.read(mybytearray, 0, mybytearray.length);
//                outputStream = socket.getOutputStream();
//
//                System.out.println("Sending " + fileToSend + " -> " +mybytearray.length + "bytes");
//                outputStream.write(mybytearray, 0, mybytearray.length);
//                outputStream.flush();
//                System.out.println("Done");
//                whileCondition = false;
//            }
//            finally {
//                if (bufferedInputStream != null) bufferedInputStream.close();
//                if (outputStream != null) outputStream.close();
//                if (socket!=null) socket.close();
//            }
//
//        }

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

        Client client1 = new Client("127.0.0.1", 3248, "F:\\Project Proposal_AS2017453.pdf", 5);
//        Client client2 = new Client("127.0.0.1", 3244);
//        Client client3 = new Client("127.0.0.1", 3246);

        client1.start();
//        client2.start();
//        client3.start();

   }
}
