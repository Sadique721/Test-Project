package com.savbill.radius.aaa.eap.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class MsMppeRecvKey {

    private static final int VENDOR_TYPE = 17;
    private static final int MIN_VENDOR_LENGTH = 4;
    private static final byte SALT_MSB = (byte) 0x80; // Most significant bit set
    private static final int BLOCK_SIZE = 16;

    public static byte[] encryptKey(byte[] key, String sharedSecret, byte[] requestAuthenticator) throws NoSuchAlgorithmException {
        // Generate the salt with MSB set and unique within a given Access-Accept packet
        byte[] salt = generateSalt();

        // Construct the plaintext (Key-Length + Key + Padding)
        byte[] plaintext = constructPlaintext(key);

        // Encrypt the plaintext
        byte[] encryptedString = encryptPlaintext(plaintext, sharedSecret, requestAuthenticator, salt);

        // Construct the final attribute
        byte[] attribute = new byte[salt.length + encryptedString.length];
      //  attribute[0] = (byte) VENDOR_TYPE;
       // attribute[1] = (byte) (MIN_VENDOR_LENGTH + salt.length + encryptedString.length);
        System.arraycopy(salt, 0, attribute, 0, salt.length);
        System.arraycopy(encryptedString, 0, attribute, salt.length, encryptedString.length);

        return attribute;
    }

    private static byte[] generateSalt() {
        Random random = new Random();
        byte[] salt = new byte[2];
        salt[0] = SALT_MSB;
        salt[1] = (byte) random.nextInt(256);
        return salt;
    }

    private static byte[] constructPlaintext(byte[] key) {
        int keyLength = key.length;
        int paddedLength = ((keyLength + 1 + BLOCK_SIZE - 1) / BLOCK_SIZE) * BLOCK_SIZE;
        byte[] plaintext = new byte[paddedLength];
        plaintext[0] = (byte) keyLength;
        System.arraycopy(key, 0, plaintext, 1, keyLength);
        // Padding with zeroes is already done by default since the array is initialized with zeroes
        return plaintext;
    }

    private static byte[] encryptPlaintext(byte[] plaintext, String sharedSecret, byte[] requestAuthenticator, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        byte[] sharedSecretBytes = sharedSecret.getBytes();
        byte[] data = new byte[sharedSecretBytes.length + requestAuthenticator.length + salt.length];
        System.arraycopy(sharedSecretBytes, 0, data, 0, sharedSecretBytes.length);
        System.arraycopy(requestAuthenticator, 0, data, sharedSecretBytes.length, requestAuthenticator.length);
        System.arraycopy(salt, 0, data, sharedSecretBytes.length + requestAuthenticator.length, salt.length);

        byte[] b = md5.digest(data);

        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i += BLOCK_SIZE) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                ciphertext[i + j] = (byte) (plaintext[i + j] ^ b[j]);
            }

            if (i + BLOCK_SIZE < plaintext.length) {
                byte[] nextData = new byte[sharedSecretBytes.length + BLOCK_SIZE];
                System.arraycopy(sharedSecretBytes, 0, nextData, 0, sharedSecretBytes.length);
                System.arraycopy(ciphertext, i, nextData, sharedSecretBytes.length, BLOCK_SIZE);
                b = md5.digest(nextData);
            }
        }

        return ciphertext;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Example usage
        byte[] key = "ExampleKey12345".getBytes();
        String sharedSecret = "SharedSecret";
        byte[] requestAuthenticator = new byte[16]; // Example 16-byte Request Authenticator
        new Random().nextBytes(requestAuthenticator);

        byte[] attribute = encryptKey(key, sharedSecret, requestAuthenticator);

        // Print the attribute in hexadecimal format
        for (byte b : attribute) {
            System.out.printf("%02x ", b);
        }
    }
}

