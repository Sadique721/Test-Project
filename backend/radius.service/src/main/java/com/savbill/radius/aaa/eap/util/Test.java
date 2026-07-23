package com.savbill.radius.aaa.eap.util;

import lombok.Getter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Optional;

@Getter
public class Test {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private KeyStore trustStore;

    private KeyStore keyStore;

    public Test() {
        // loadKeyStore(file);
    }
    /*
    Certificate from cf1 and cc folder has been added to this store, so certificate from relevant folder needs
    to be verified.
     */


    //keytool -importcert -file server.crt -alias server_certy -keystore /home/savbill/keystore/savbillkeystore.jks -storepass password
    public KeyStore loadTrustStoreOrKeyStore(String storePath, String storePassword, String instanceType) {

        try(InputStream fis = Files.newInputStream(Paths.get(storePath)))  {
            if (instanceType == null) {
                trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            } else {
                trustStore = KeyStore.getInstance(instanceType);
            }
            trustStore.load(fis, storePassword.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }

        return trustStore;
    }

    public boolean verifyCertificate(Certificate certificate) {
        // Assuming you have received the client certificate
        boolean isValid = false;
        try {

            // Verifying client certificate against CA certificates
            Enumeration<String> iterator = getTrustStore().aliases();
            while (iterator.hasMoreElements()) {
                String alias = iterator.nextElement();
                Certificate caCert = getTrustStore().getCertificate(alias);
                // Certificate verification failed
                if (caCert instanceof X509Certificate) {
                    try {
                        certificate.verify(caCert.getPublicKey());
                        isValid = true;

                        if (isValid)
                        System.out.println("validated by: " + ((X509Certificate) caCert).getSubjectX500Principal().getName());
                        break;
                    } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException |
                             SignatureException e) {
                        // throw new RuntimeException(e);
                        // log error and print stacktrace and set isValid = false instead of runtime exception
                    }
                }
            }

        } catch (CertificateException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
        if (isValid) {
            System.out.println("Client certificate is valid.");
        } else {
            System.out.println("Client certificate is not valid.");
        }
        return isValid;
    }
    public Certificate[] getCertificateChain() throws KeyStoreException {

        // Accessing CA certificates
        Certificate caCert1 = trustStore.getCertificate("ca2"); // home/savbill/cc/ca.crt
        Certificate serverCerty = trustStore.getCertificate("server_certy"); // home/savbill/cc/server.crt

        return new Certificate[]{serverCerty, caCert1};
    }

    public static void main(String[] args) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        Test test = new Test();
        KeyStore tustStore = test.loadTrustStoreOrKeyStore("/home/savbill/Documents/NCP/TEST/truststore.jks", "password", null);
        int size = test.getTrustStore().size();
        System.out.println( "Truststore "+ size);

        Optional<Certificate> certificate = parseCertificate("/home/savbill/Documents/NCP/TEST/server.crt");
        Certificate clientCertificate = certificate.get();
        test.verifyCertificate(clientCertificate);

        System.out.println(test.getTrustStore().getCertificate("serverca"));


        System.out.println(test.getTrustStore().getCertificate("clientca"));

        KeyStore keyStore = test.loadTrustStoreOrKeyStore("/home/savbill/Documents/NCP/TEST/keystore.p12", "password", "PKCS12");

       int aliases = keyStore.size();
        System.out.println("Key store size: " + aliases);


        Key certificate1 = keyStore.getKey("1", "password".toCharArray());

        System.out.println(keyStore.getCertificate("1"));

    }

    public static Optional<Certificate> parseCertificate(String filePath) {

        X509Certificate x509Certificate = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(filePath));
        } catch (NoSuchProviderException | CertificateException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(x509Certificate);
    }


}
