package MyClientServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileFetcher implements Runnable {

    String task;
    String ip;
    int port;
    ArrayList<NodeDetails> nodesList;
    HashMap<String, String> filedata;
    HashMap<String, SecretKey> keys;
    HashMap<String, IvParameterSpec> ivs;
    String seekingFile;
    DatagramSocket sock;
    String sendingFileName;
    String nodeUsername;

    public FileFetcher(String task, String ip, int port, ArrayList<NodeDetails> nodesList, HashMap<String, String> filedata, HashMap<String, SecretKey> keys, HashMap<String, IvParameterSpec> ivs, String seekingFile, DatagramSocket sock) {
        this.task = task;
        this.ip = ip;
        this.port = port;
        this.nodesList = nodesList;
        this.filedata = filedata;
        this.keys = keys;
        this.ivs = ivs;
        this.seekingFile = seekingFile;
        this.sock = sock;
    }

    public FileFetcher(String task, int senderPort, String sendingFileName, String nodeUsername) {
        this.task = task;
        this.port = senderPort;
        this.sendingFileName = sendingFileName;
        this.nodeUsername = nodeUsername;
    }

    public void selectTask(ServerSocket serverSocket) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        if (task.equals("FETCH")) {
            sendFetchRequestToNodes(serverSocket);
        } else {
            sendFileChunksToNodes(serverSocket);
        }

    }

    private void sendFileChunksToNodes(ServerSocket serverSocket) throws IOException {
        FileReceiver fileReceiver = new FileReceiver();
        fileReceiver.setFilepathFromName(sendingFileName, nodeUsername );
        fileReceiver.getFile(serverSocket);
    }

    public String setFilepathFromSeekingFile(String fileName) {

//        String dir = "F:\\CopyFiles\\";
        String dir = FilePathsUtil.FETCHED_FILE;
        String filepath;

        filepath = dir + fileName;
        System.out.println("Saving file to -> " + filepath);

        return  filepath;

    }


    public void sendFetchRequestToNodes(ServerSocket serverSocket) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String fileOutPath = setFilepathFromSeekingFile(seekingFile);
        OutputStream outputFile = new FileOutputStream(fileOutPath);

        for (int i = 0; i< nodesList.size(); i++) {
            String nodeUsername = nodesList.get(i).getUsername();
            int nodePort = nodesList.get(i).getPort();
            String nodeIP = nodesList.get(i).getIp();

            String fileChunk = filedata.get(nodeUsername);
            String msg = "FETCH " + ip + " " + port + " " + fileChunk + " " + seekingFile + " " + nodeUsername;

            InetAddress bs_address = InetAddress.getByName(nodeIP);
            Node.sendMsgViaSocket(sock, bs_address, nodePort, msg);

            FileReceiver fileReceiver = new FileReceiver();
            //fileReceiver.setFilepathFromSeekingFile(seekingFile);
            fileReceiver.getFileToMerge(serverSocket, fileChunk, keys, ivs, outputFile);

            // socket.close();
        }
        outputFile.close();
    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);
//            SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
//            SSLServerSocket sslserversocket = (SSLServerSocket)sslserversocketfactory.createServerSocket(port);
            selectTask(socket);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }
}
