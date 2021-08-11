package MyClientServer;

public class FilePathsUtil {

    private  static String tempFolder = "temp/";
    private  static String systemReceivedFiles = "F:\\CopyFiles\\RecievedFiles\\";
    private  static String fetchedFile = "F:\\CopyFiles\\";

    public static  String getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    public static String getSystemReceivedFiles() {
        return systemReceivedFiles;
    }

    public void setSystemReceivedFiles(String systemReceivedFiles) {
        this.systemReceivedFiles = systemReceivedFiles;
    }

    public static String getFetchedFile() {
        return fetchedFile;
    }

    public void setFetchedFile(String fetchedFile) {
        this.fetchedFile = fetchedFile;
    }
}
