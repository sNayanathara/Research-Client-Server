package MyClientServer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SplitFiles {

    private int chunkSizeInMB;
    private String filePath_ofFile_toSend;

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
}
