package com.savbill.radius.aaa.eap.util;


import com.savbill.radius.aaa.eap.data.SecurityKeys;
import com.savbill.radius.aaa.util.RadiusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class CalculateKeyingMaterial {

    private static final Logger log = LoggerFactory.getLogger(CalculateKeyingMaterial.class);

    public static SecurityKeys securityKeyForAES256(byte[] clientRandom, byte[] serverRandom, byte[] masterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        SecurityKeys securityKeys = new SecurityKeys();

        byte[] keyMaterial = calculateKeyingMaterial(clientRandom, serverRandom, masterSecret);

        String keyMaterialHexString = RadiusUtil.getHexString(keyMaterial);

        log.debug("Key material hex string: " + keyMaterialHexString);

        byte[] clientMacBytes = Arrays.copyOfRange(keyMaterial, 0, 32);
        String clientMACKey =  RadiusUtil.getHexString(clientMacBytes);
        securityKeys.setClientMACKey(clientMacBytes);

        log.debug( "Client Mac key equals: " + clientMACKey);

        byte[] serverMacBytes = Arrays.copyOfRange(keyMaterial, 32, 64);
        String serverMACKey =  RadiusUtil.getHexString(serverMacBytes);
        securityKeys.setServerMACKey(serverMacBytes);

        log.debug("Server Mac key equals: " + serverMACKey);

        byte[] clientWriteKeyBytes = Arrays.copyOfRange(keyMaterial, 64, 96);
        String clientWriteKey =  RadiusUtil.getHexString(clientWriteKeyBytes);
        securityKeys.setClientWriteKey(clientWriteKeyBytes);

        log.debug("client write key: " + clientWriteKey);

        byte[] serverWriteKeyBytes = Arrays.copyOfRange(keyMaterial, 96, 128);
        String serverWriteKey =  RadiusUtil.getHexString(serverWriteKeyBytes);
        securityKeys.setServerWriteKey(serverWriteKeyBytes);

        byte[] clientWriteIvBytes = Arrays.copyOfRange(keyMaterial, 128, 144);
        String clientWriteIvHex =  RadiusUtil.getHexString(clientWriteIvBytes);
        securityKeys.setClientIv(clientWriteIvBytes);
        log.debug("client write  IV key: " + clientWriteIvHex);

        byte[] serverWriteIvBytes = Arrays.copyOfRange(keyMaterial, 144, 160);
        String serverWriteIvHex =  RadiusUtil.getHexString(serverWriteIvBytes);
        securityKeys.setServerIv(serverWriteIvBytes);
        log.debug("server write  IV key: " + serverWriteIvHex);

        return securityKeys;
    }

    public static SecurityKeys securityKeyForAES128(byte[] clientRandom, byte[] serverRandom, byte[] masterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        SecurityKeys securityKeys = new SecurityKeys();

        byte[] keyMaterial = calculateKeyingMaterial(clientRandom, serverRandom, masterSecret);

        String keyMaterialHexString = RadiusUtil.getHexString(keyMaterial);

        log.debug("Key material hex string: " + keyMaterialHexString);

        byte[] first32Bytes = Arrays.copyOfRange(keyMaterial, 0, 20);
        String clientMACKey =  RadiusUtil.getHexString(first32Bytes);
        securityKeys.setClientMACKey(first32Bytes);

        log.debug( "Client Mac key equals: " + clientMACKey);

        byte[] next32Bytes = Arrays.copyOfRange(keyMaterial, 20, 40);
        String serverMACKey =  RadiusUtil.getHexString(next32Bytes);
        securityKeys.setServerMACKey(next32Bytes);

        log.debug("Server Mac key equals: " + serverMACKey);

        byte[] next64Bytes = Arrays.copyOfRange(keyMaterial, 40, 56);
        String clientWriteKey =  RadiusUtil.getHexString(next64Bytes);
        securityKeys.setClientWriteKey(next64Bytes);

        log.debug("client write key: " + clientWriteKey);

        byte[] next96Bytes = Arrays.copyOfRange(keyMaterial, 56, 72);
        String serverWriteKey =  RadiusUtil.getHexString(next96Bytes);
        securityKeys.setServerWriteKey(next96Bytes);

        log.debug("Server write key: " + serverWriteKey);

        byte[] clientWriteIvBytes = Arrays.copyOfRange(keyMaterial, 72, 88);
        String clientWriteIvHex =  RadiusUtil.getHexString(clientWriteIvBytes);
        securityKeys.setClientIv(clientWriteIvBytes);
        log.debug("client write  IV key: " + clientWriteIvHex);

        byte[] serverWriteIvBytes = Arrays.copyOfRange(keyMaterial, 88, 104);
        String serverWriteIvHex =  RadiusUtil.getHexString(serverWriteIvBytes);
        securityKeys.setServerIv(serverWriteIvBytes);
        log.debug("server write  IV key: " + serverWriteIvHex);

        return securityKeys;
    }

    protected static byte[] calculateKeyingMaterial(byte[] clientRandom, byte[] serverRandom, byte[] masterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        String keyExpansionStr = "key expansion";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(keyExpansionStr.getBytes(StandardCharsets.UTF_8));
        out.write(serverRandom);
        out.write(clientRandom);

        byte[] seedBytes = out.toByteArray();
        byte[] a0 = seedBytes;

        byte[] a1 = calculateHMAC(a0, masterSecret);
        byte[] a2 = calculateHMAC(a1, masterSecret);
        byte[] a3 = calculateHMAC(a2, masterSecret);
        byte[] a4 = calculateHMAC(a3, masterSecret);
        byte[] a5 = calculateHMAC(a4, masterSecret);

        byte[] p1 = calculateHMAC(concatenateByteArrays(a1, seedBytes), masterSecret);
        byte[] p2 = calculateHMAC(concatenateByteArrays(a2, seedBytes), masterSecret);
        byte[] p3 = calculateHMAC(concatenateByteArrays(a3, seedBytes), masterSecret);
        byte[] p4 = calculateHMAC(concatenateByteArrays(a4, seedBytes), masterSecret);
        byte[] p5 = calculateHMAC(concatenateByteArrays(a5, seedBytes), masterSecret);

        ByteArrayOutputStream key = new ByteArrayOutputStream();
        key.write(p1);
        key.write(p2);
        key.write(p3);
        key.write(p4);
        key.write(p5);

        return key.toByteArray();
    }


    public static byte[] calculateHMAC(byte[] message, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        hmacSha256.init(secretKey);
        return hmacSha256.doFinal(message);
    }

    public static byte[] calculateHMACSHA(byte[] message, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA1");
        hmacSha256.init(secretKey);
        return hmacSha256.doFinal(message);
    }

    // concat two array
    public static byte[] concatenateByteArrays(byte[] byteArray1, byte[] byteArray2) {
        byte[] concatenatedArray = new byte[byteArray1.length + byteArray2.length];
        System.arraycopy(byteArray1, 0, concatenatedArray, 0, byteArray1.length);
        System.arraycopy(byteArray2, 0, concatenatedArray, byteArray1.length, byteArray2.length);
        return concatenatedArray;
    }

    public static String encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(data);

        String encryptedString = RadiusUtil.getHexString(encryptedBytes);

        return encryptedString;
    }

    public static byte[] decryptAES(byte[] cipherText, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"AES/CBC/NoPadding");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(cipherText);
    }

    public static byte[] encryptAES(byte[] plaintext, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"AES/CBC/NoPadding");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(plaintext);
    }

    private static byte[] decryptWithPrivateKey(byte[] input, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(input);
    }

    public static byte[] encryptWithPublicKey(byte[] input, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(input);
    }

}
