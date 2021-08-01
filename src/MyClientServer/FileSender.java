package MyClientServer;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;


public class FileSender {

    FileInputStream fileToSend;
    //private Logger logger = Logger.getLogger(FileSender.class.getName());

    public void fetch_and_SendFile(Socket socket) throws IOException {
        OutputStream os = null;
        FileInputStream fileInputStream = fileToSend;
        try {
            System.out.println("Inside sender");
            os = socket.getOutputStream();
            int bytesRead;
            byte[] buffer = new byte[4096];
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
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
        }
    }

    public FileInputStream getFileFromName(String filename) throws IOException {
        String dir = "F:\\";

        String filePath = dir + filename;
        fileToSend = new FileInputStream(filePath);

        return fileToSend;
    }

    public FileInputStream getFileFromPath(String filepath) throws IOException {

        fileToSend = new FileInputStream(filepath);

        return fileToSend;
    }

    public FileInputStream getFileToSend(File file) throws FileNotFoundException {

        fileToSend = new FileInputStream(file);

        return fileToSend;
    }

}
