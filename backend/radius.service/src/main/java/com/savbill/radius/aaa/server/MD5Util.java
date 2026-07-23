package com.savbill.radius.aaa.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String getMD5Hash(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte[] digest = md.digest();
        return convertByteArrayToHex(digest);
    }

    private static String convertByteArrayToHex(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length * 2);
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        String message = "This is a sample message";
//        String hashedMessage = getMD5Hash(message);
//        System.out.println("Original message: " + message);
//        System.out.println("MD5 hash: " + hashedMessage);
//    }
}
