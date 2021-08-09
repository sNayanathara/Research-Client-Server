package MyClientServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplitFiles {

    private int chunkSizeInMB;
    private String filePath_ofFile_toSend;
    public static HashMap<String, SecretKey> keys = new HashMap<>();
    public static HashMap<String, IvParameterSpec> IVs = new HashMap<>();

    public SplitFiles(int chunkSizeInMB, String filePath_ofFile_toSend) {
        this.chunkSizeInMB = chunkSizeInMB;
        this.filePath_ofFile_toSend = filePath_ofFile_toSend;
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
                File fileChunk = new File("temp/" + chunkFileName);

                String fileChunkNameWithoutExten = chunkFileName.substring(0,chunkFileName.lastIndexOf("."));
                String encFileName = fileChunkNameWithoutExten + ".enc";
                File fileChunkEnc = new File("temp/"+encFileName);

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

                String algorithm = "AES/CBC/PKCS5Padding";

                SecretKey key = AESUtil.generateKey();
                keys.put(encFileName, key);

                IvParameterSpec ivParameterSpec = AESUtil.generateIv();
                IVs.put(encFileName, ivParameterSpec);

//                System.out.println("Check Enc Keys : " +encFileName + " " + key + " " + ivParameterSpec);

                AESUtil.encryptFile(algorithm, key, ivParameterSpec, fileChunk, fileChunkEnc);

                files.add(fileChunkEnc);
            }
            inputStream.close();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return files;

    }
}
