package MyClientServer;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;


public class FileSender implements Runnable {

    private int port;
    FileInputStream fileToSend;
    private Logger logger = Logger.getLogger(FileSender.class.getName());
   // private String filename;

    public FileSender(int port) {
        //this.filename = filename;
        this.port = port;
    }

    public void fetch_and_SendFile(Socket socket, FileInputStream fileInputStream) throws IOException {
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            int bytesRead;
            byte[] buffer = new byte[4096];
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                //dataOutputStream.flush();
            }
            os.flush();
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch(IOException e) {
                }
            if (fileInputStream != null)
                try {
                    fileInputStream.close();
                } catch(IOException e) {
                }
            //connectionSocket.close();
        }
    }

    public FileInputStream getFileFromName(String filename) throws IOException {
        String dir = "F:\\";

//        String fileName = is.readUTF();
        String filePath = dir + filename;
        fileToSend = new FileInputStream(filePath);

        return fileToSend;
    }

    public FileInputStream getFileFromPath(String filepath) throws IOException {

//        String fileName = is.readUTF();
        //String filePath = filename;

        fileToSend = new FileInputStream(filepath);

        return fileToSend;
    }


    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", port)) {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
           // FileInputStream fileToSend = getFileToSend(dataInputStream);


            fetch_and_SendFile(socket, fileToSend);

            dataInputStream.close();
            //dataInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
