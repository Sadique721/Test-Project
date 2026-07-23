package com.savbill.radius.aaa.eap.util;

import com.savbill.radius.aaa.util.RadiusUtil;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Optional;


public class CertificateUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    public static Optional<Certificate> parseCertificate(String filePath) {

        X509Certificate x509Certificate = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            x509Certificate = (X509Certificate) certificateFactory.generateCertificate(CertificateUtil.class.getClassLoader().getResourceAsStream(filePath));
        } catch (NoSuchProviderException | CertificateException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(x509Certificate);
    }

    public static Optional<Certificate> parseCertificate(byte[] certificateBytes) {
        X509Certificate x509Certificate = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(certificateBytes);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            x509Certificate = (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
        } catch (NoSuchProviderException | CertificateException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(x509Certificate);
    }

    /**
     * This method will be used to get private key which not encrypted with password
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    // This approach is used till now to load file from cc folder
    // privateKey.add("/home/savbill/Downloads/certificate/private.key"); ////1.2.840.113549.1.1.1 RSA encryption  readPKCS8PrivateKeySecondApproach
    public static PrivateKey readPrivateKey(String fileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        File tempFile = readData(fileName);

        try (FileReader keyReader = new FileReader(tempFile)) {

            PEMParser pemParser = new PEMParser(keyReader);
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());

            byte[] encoded = privateKeyInfo.getEncoded();

            System.out.println(privateKeyInfo.getPrivateKeyAlgorithm());
            System.out.println(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm().getId());
            System.out.println(encoded.length);

            // Extract the ASN.1 sequence from PrivateKeyInfo
            ASN1Sequence seq = (ASN1Sequence) ASN1Sequence.fromByteArray(privateKeyInfo.getEncoded());

            // Create a PKCS8EncodedKeySpec from the ASN.1 sequence
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(seq.getEncoded());

            // Generate the RSA private key
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");

            return kf.generatePrivate(keySpec);
        }
    }

    private static File readData(String fileName) throws IOException {
        try (InputStream inputStream = CertificateUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                System.out.println("Resource not found: " + fileName);
                return null;
            }

            // Create a temporary file and write the input stream to it
            File tempFile = File.createTempFile("tempResource", null);
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        }
    }

    public static PrivateKey readEncryptedPrivateKey(String filePath) throws Exception {
        try (FileReader keyReader = new FileReader(filePath)) {

            PEMParser pemParser = new PEMParser(keyReader);
            Object o = pemParser.readObject();

            System.out.println("pem object: " + o);

            // PrivateKey decryptedPrivateKey = getDecryptedPrivateKey(new byte[32], "password".toCharArray());
            // System.out.println("Decrypted privatekey: " + RadiusUtil.getHexString(decryptedPrivateKey.getEncoded()));
            return null;
        }



/*
    public static PrivateKey getDecryptedPrivateKey(byte[] encryptedPrivateKeyBytes, char[] password) throws Exception {
        // Parse the EncryptedPrivateKeyInfo
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKeyBytes);

        // Create PBE key and initialize cipher
        Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName(), "BC");
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName(), "BC");
        AlgorithmParameters pbeParamSpec = encryptedPrivateKeyInfo.getAlgParameters();

        // Decrypt the private key
        byte[] decryptedBytes = cipher.doFinal(encryptedPrivateKeyInfo.getEncryptedData());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedBytes);

        // Generate the private key
        KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
        return kf.generatePrivate(keySpec);
    }
*/

/*
    public static void main(String[] args) throws Exception {
        System.out.println(readEncryptedPrivateKey("/home/savbill/cc/server1.key"));
    }
*/

    }

    public static void main(String[] args) throws Exception {
        //  readEncryptedPrivateKey("/home/savbill/locat_certy/server.key"); // 1.2.840.113549.1.5.13
        // readPrivateKey("/home/savbill/locat_certy/server.key");

        // Optional<Certificate> serverCertificate = parseCertificate("/home/savbill/Documents/certificate/server.crt");
        // Optional<Certificate> clientCertificate = parseCertificate("/home/savbill/Documents/certificate/client.crt");

        //System.out.println("Server certificate: " + serverCertificate);
        //System.out.println("Client certificate: " + clientCertificate);

        //  PrivateKey serverKey = readPrivateKey("/home/savbill/Documents/certificate/server.key");
        // PrivateKey clientKey = readPrivateKey("/home/savbill/Documents/certificate/client.key");

        Optional<Certificate> caCertificate = parseCertificate("/home/savbill/pcapapril/9/ca.pem");
        X509Certificate certificate = (X509Certificate) caCertificate.get();
        Principal subjectDN = certificate.getSubjectDN();
        String hexString = RadiusUtil.getHexString(certificate.getSubjectX500Principal().getEncoded());
        System.out.println("Certificate DN: " + hexString);
        System.out.println(subjectDN);
        //  System.out.println("Server Key: " + serverKey);
        //  System.out.println("Client Key: " + clientKey);
    }
}
