package MyClientServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public  void getFileToMerge(ServerSocket socket, String fileChunk, HashMap<String, SecretKey> keys, HashMap<String, IvParameterSpec> ivs, OutputStream out) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Socket sss = socket.accept();
        String algorithm = "AES/CBC/PKCS5Padding";
        SecretKey key = keys.get(fileChunk);
        IvParameterSpec ivParameterSpec = ivs.get(fileChunk);


        //int bytesRead;

        InputStream in=sss.getInputStream();     //socket closed..
        //out=new FileOutputStream(filepath);
 //       byte[] buffer=new byte[1024];
//        while((bytesRead=in.read(buffer))!=-1)
//        {
//            out.write(buffer,0,bytesRead);
//        }
        AESUtil.decryptFile(algorithm, key, ivParameterSpec, in, out);
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
        String filenameWithoutExtension = filename.substring(0,filename.lastIndexOf("."));
        filepath = directory + "\\" + filenameWithoutExtension + ".enc";
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
