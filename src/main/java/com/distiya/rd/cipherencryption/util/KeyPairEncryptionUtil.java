package com.distiya.rd.cipherencryption.util;

import com.distiya.rd.cipherencryption.data.Tuple1Data;
import io.vertx.core.buffer.Buffer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class KeyPairEncryptionUtil {

    private static final String ALGORITHM = "RSA";

    private static final Integer MAX_BLOCK_SIZE = 245;

    public static void generateKeyPair(int keyLength, String publicKeyLocation, String privateKeyLocation) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(keyLength);
        KeyPair keyPair = generator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        try(FileOutputStream fos = new FileOutputStream(publicKeyLocation)){
            fos.write(publicKey.getEncoded());
        }
        try(FileOutputStream fos = new FileOutputStream(privateKeyLocation)){
            fos.write(privateKey.getEncoded());
        }
    }

    public static Key getKeyFromFile(String keyLocation, boolean publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File keyFile = new File(keyLocation);
        byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        if(publicKey){
            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(keySpec);
        }
        else{
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return keyFactory.generatePrivate(keySpec);
        }
    }

    public static List<String> encryptFile(String fileLocation, String publicKeyLocation) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        File encryptFile = new File(fileLocation);
        byte[] fileContent = Files.readAllBytes(encryptFile.toPath());
        Tuple1Data<byte[]> rawBytes = new Tuple1Data<>(fileContent);
        PublicKey publicKey = (PublicKey) getKeyFromFile(publicKeyLocation, true);
        return encryptRawBytes(rawBytes,publicKey);
    }

    public static List<String> encryptString(String rawString, String publicKeyLocation) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        PublicKey publicKey = (PublicKey) getKeyFromFile(publicKeyLocation, true);
        return encryptRawBytes(new Tuple1Data<>(rawString.getBytes(StandardCharsets.UTF_8)),publicKey);
    }

    public static String decryptString(List<String> encodedStringSlices, String privateKeyLocation) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        PrivateKey privateKey = (PrivateKey) getKeyFromFile(privateKeyLocation, false);
        return new String(getDecryptedRawBytes(encodedStringSlices,privateKey).getT1(),StandardCharsets.UTF_8);
    }

    public static void decryptFile(List<String> encodedStringSlices, String privateKeyLocation, String fileLocation, String fileName, String fileType) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        File saveDirectory = new File(fileLocation);
        Path tempFile = Files.createTempFile(saveDirectory.toPath(),fileName, fileType);
        PrivateKey privateKey = (PrivateKey) getKeyFromFile(privateKeyLocation, false);
        try(FileOutputStream stream = new FileOutputStream(tempFile.toFile())){
            stream.write(getDecryptedRawBytes(encodedStringSlices,privateKey).getT1());
        }
    }

    public static List<String> encryptRawBytes(Tuple1Data<byte[]> rawBytes, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        List<String> encryptedList = new ArrayList<>();
        Cipher encryptCipher = Cipher.getInstance(ALGORITHM);
        encryptCipher.init(Cipher.ENCRYPT_MODE,publicKey);
        int totalBlocks = rawBytes.getT1().length / MAX_BLOCK_SIZE;
        if(rawBytes.getT1().length % MAX_BLOCK_SIZE > 0){
            totalBlocks = totalBlocks + 1;
        }
        for(int i = 0; i < totalBlocks; i++){
            int startIndex = i * MAX_BLOCK_SIZE;
            int endIndex = (i+1) * MAX_BLOCK_SIZE;
            if(endIndex > rawBytes.getT1().length){
                endIndex = rawBytes.getT1().length;
            }
            encryptedList.add(Base64.getEncoder().encodeToString(encryptCipher.doFinal(Arrays.copyOfRange(rawBytes.getT1(),startIndex,endIndex))));
        }
        return encryptedList;
    }

    public static Tuple1Data<byte[]> getDecryptedRawBytes(List<String> encodedString, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Buffer buffer = Buffer.buffer();
        Cipher decryptCipher = Cipher.getInstance(ALGORITHM);
        decryptCipher.init(Cipher.DECRYPT_MODE,privateKey);
        Tuple1Data<Boolean> hasErrors = Tuple1Data.of(Boolean.FALSE);
        encodedString.stream().map(s-> {
            try {
                return new Tuple1Data<>(decryptCipher.doFinal(Base64.getDecoder().decode(s)));
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                hasErrors.setT1(Boolean.TRUE);
                return null;
            }
        })
        .filter(Objects::nonNull)
        .filter(e->!hasErrors.getT1())
        .forEach(t->buffer.appendBytes(t.getT1()));
        return new Tuple1Data<>(buffer.getBytes());
    }
}
