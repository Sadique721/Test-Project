package com.savbill.radius.aaa.eap.util;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MsMppeRecvKeyDecrypt {

    private static final int BLOCK_SIZE = 16;

    public static byte[] decryptKey(byte[] encryptedKey, String sharedSecret, byte[] requestAuthenticator) throws NoSuchAlgorithmException {
        // Extract the Salt from the encrypted key
        byte[] salt = Arrays.copyOfRange(encryptedKey, 2, 4);

        // Extract the encrypted String part
        byte[] encryptedString = Arrays.copyOfRange(encryptedKey, 4, encryptedKey.length);

        // Decrypt the encrypted String
        byte[] plaintext = decryptString(encryptedString, sharedSecret, requestAuthenticator, salt);

        // Extract the Key-Length and Key from the plaintext
        int keyLength = plaintext[0];
        byte[] key = Arrays.copyOfRange(plaintext, 1, 1 + keyLength);

        return key;
    }

    private static byte[] decryptString(byte[] encryptedString, String sharedSecret, byte[] requestAuthenticator, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        byte[] sharedSecretBytes = sharedSecret.getBytes();
        byte[] data = new byte[sharedSecretBytes.length + requestAuthenticator.length + salt.length];
        System.arraycopy(sharedSecretBytes, 0, data, 0, sharedSecretBytes.length);
        System.arraycopy(requestAuthenticator, 0, data, sharedSecretBytes.length, requestAuthenticator.length);
        System.arraycopy(salt, 0, data, sharedSecretBytes.length + requestAuthenticator.length, salt.length);

        byte[] b = md5.digest(data);

        byte[] plaintext = new byte[encryptedString.length];
        for (int i = 0; i < encryptedString.length; i += BLOCK_SIZE) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                plaintext[i + j] = (byte) (encryptedString[i + j] ^ b[j]);
            }

            if (i + BLOCK_SIZE < encryptedString.length) {
                byte[] nextData = new byte[sharedSecretBytes.length + BLOCK_SIZE];
                System.arraycopy(sharedSecretBytes, 0, nextData, 0, sharedSecretBytes.length);
                System.arraycopy(encryptedString, i, nextData, sharedSecretBytes.length, BLOCK_SIZE);
                b = md5.digest(nextData);
            }
        }

        return plaintext;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Example usage
        String encryptedKeyHex = "113487d21a6a1234275cbd2cad99f5f1eba44ff8cfec6536fa809ae2da39c07bda79059a2dfcf7c84a6b0c6cf89ee297b591a44c";
        byte[] encryptedKey = hexStringToByteArray(encryptedKeyHex);

        String sharedSecret = "testing123";

        String requestAuthenticatorHex = "d569e0085766fc950418fee78366e819";
        byte[] requestAuthenticator = hexStringToByteArray(requestAuthenticatorHex);

        byte[] decryptedKey = decryptKey(encryptedKey, sharedSecret, requestAuthenticator);

        // Print the decrypted key
        System.out.println(RadiusUtil.getHexString(decryptedKey));
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}

