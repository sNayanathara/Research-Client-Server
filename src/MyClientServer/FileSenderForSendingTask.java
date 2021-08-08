package MyClientServer;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.logging.Logger;


public class FileSenderForSendingTask implements Runnable {

    DatagramPacket incoming;
    String fileName;
    FileInputStream fileToSend;
    int port;
    String path;
    DatagramSocket sock;
    String nodeUsername;

    public FileSenderForSendingTask(String path, DatagramSocket sock, int listeningPort, String fileName, DatagramPacket incoming, String nodeUsername) {
        this.path = path;
        this.sock = sock;
        this.port = listeningPort;
        this.fileName = fileName;
        this.incoming = incoming;
        this.nodeUsername = nodeUsername;
    }
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

    public void sendFileToNode(Socket socket) throws IOException {
        SplitFiles fileSplitter = new SplitFiles(10, path);
        List<File> fileList = fileSplitter.splitFile(path);

        String msg = "SEND " + fileName + " " + port + " " + nodeUsername;

        for (File file: fileList) {
            int count = 0;
            String currentFileName = file.getName();
            if (currentFileName.equals(fileName)) {

                InetAddress bs_address = InetAddress.getByName(incoming.getAddress().getHostAddress());
                Node.sendMsgViaSocket(sock, bs_address, incoming.getPort(), msg);

                getFileToSend(file);
                fetch_and_SendFile(socket);
            }
        }

    }

    public FileInputStream getFileToSend(File file) throws FileNotFoundException {

        fileToSend = new FileInputStream(file);

        return fileToSend;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", port)) {
            sendFileToNode(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
