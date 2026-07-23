package com.savbill.radius.aaa.eap.util;


import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;

public class EncryptedPrivateKeyReader {

    static {
        // Add Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());
    }

    /*public static PrivateKey decryptPrivateKey(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, char[] password) throws Exception {
        // Extract encryption algorithm OID and encrypted data
        String algOID = encryptedPrivateKeyInfo.getEncryptionAlgorithm().getAlgorithm().getId();
        byte[] encryptedData = encryptedPrivateKeyInfo.getEncryptedData();

        // Get cipher instance
        Cipher cipher = Cipher.getInstance(algOID, "BC");

        // Get PBE parameters
        PBEParameterSpec pbeParamSpec = encryptedPrivateKeyInfo.getEncryptionAlgorithm().getParameters().getParameterSpec(PBEParameterSpec.class);

        // Create PBE key
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algOID, "BC");
        SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);

        // Decrypt the private key
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedData);

        // Convert decrypted key bytes to PrivateKey
        // You need to adapt this part based on the format of the decrypted key
        // For example, if it's an RSA private key, you would typically use PKCS8EncodedKeySpec and KeyFactory
        // For simplicity, assuming it's in PKCS#8 format
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        return keyFactory.generatePrivate(keySpec);
    }*/

    public static void main(String[] args) throws Exception {
        // Obtain encrypted private key info (e.g., from a PEM file)
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = obtainEncryptedPrivateKeyInfo();

        // Decrypt the private key
        char[] password = "your_password".toCharArray();
     //   PrivateKey privateKey = decryptPrivateKey(encryptedPrivateKeyInfo, password);

        // Use the private key as needed
    }

    // Method to obtain EncryptedPrivateKeyInfo (e.g., from a PEM file)
    public static EncryptedPrivateKeyInfo obtainEncryptedPrivateKeyInfo() {
        // Implement this method to read EncryptedPrivateKeyInfo from a source (e.g., PEM file)
        // For demonstration purposes, return a dummy EncryptedPrivateKeyInfo
        // Replace this with actual code to obtain EncryptedPrivateKeyInfo
        // For example, you can use PEMParser from Bouncy Castle library to read PEM file
        return new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.5.13")), new byte[]{}); // Dummy example
    }
}
