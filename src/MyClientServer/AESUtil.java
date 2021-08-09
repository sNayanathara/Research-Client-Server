package MyClientServer;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {

//    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
//            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
//            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
//        Cipher cipher = Cipher.getInstance(algorithm);
//        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
//        byte[] cipherText = cipher.doFinal(input.getBytes());
//        return Base64.getEncoder()
//                .encodeToString(cipherText);
//    }
//
//    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
//            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
//            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
//        Cipher cipher = Cipher.getInstance(algorithm);
//        cipher.init(Cipher.DECRYPT_MODE, key, iv);
//        byte[] plainText = cipher.doFinal(Base64.getDecoder()
//                .decode(cipherText));
//        return new String(plainText);
//    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);   //n= 128,192,256
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    //Initialization Vector is a pseudo-random value and has the same size as the block that is encrypted
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static void encryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   File inputFile, File outputFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   InputStream inputStream, OutputStream outputStream) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        //FileInputStream inputStream = new FileInputStream(encryptedFile);
        //FileOutputStream outputStream = new FileOutputStream(decryptedFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] output = cipher.doFinal();
        if (output != null) {
            outputStream.write(output);
        }
//        inputStream.close();
//        outputStream.close();
    }


//    public static void main(String[] args)throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException,
//            InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException,
//            NoSuchPaddingException {
//
//        SecretKey key = AESUtil.generateKey(128);
//        String algorithm = "AES/CBC/PKCS5Padding";
//        IvParameterSpec ivParameterSpec = AESUtil.generateIv();
//        //Resource resource = new ClassPathResource("inputFile/baeldung.txt");
//        File inputFile = new File("F:/fileFolder/marsland.ml-alg-perspect.09.pdf");
//        File encryptedFile = new File("F:/fileFolder/encrypted.enc");
//        File decryptedFile = new File("F:/decrypted.pdf");
//        AESUtil.encryptFile(algorithm, key, ivParameterSpec, inputFile, encryptedFile);
//        AESUtil.decryptFile(
//                algorithm, key, ivParameterSpec, encryptedFile, decryptedFile);
//        //assert(inputFile).hasSameTextualContentAs(decryptedFile);
//    }
}
