package com.savbill.radius.aaa.eap.util;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Getter
public class KeyStoreImpl {

    private KeyStore keystore;

    private KeyStore trustStore;

    private String keyStorePassword;

    public KeyStoreImpl(String keyStorePath, String keyStorePassword, String trustStorePath, String trustStorePassword) {

        System.out.println("Initialized from radius policy");
        this.keystore = loadKeyStore(keyStorePath, keyStorePassword);
        this.trustStore = loadKeyStore(trustStorePath, trustStorePassword);
        this.keyStorePassword = keyStorePassword;
    }


    //keytool -importcert -file server.crt -alias server_certy -keystore /home/savbill/keystore/savbillkeystore.jks -storepass password
    private static KeyStore loadKeyStore(String keyStorePath, String password) {
        KeyStore keyStore = null;
        try(InputStream fis = Files.newInputStream(Paths.get(String.valueOf(keyStorePath))))  {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fis, password.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
        return keyStore;
    }

    public boolean verifyCertificate(Certificate certificate) {
        boolean isValid = false;
        try {
            // Verifying client certificate against CA certificates
            Enumeration<String> iterator = getTrustStore().aliases();
            while (iterator.hasMoreElements()) {
                String alias = iterator.nextElement();
                Certificate caCert = getTrustStore().getCertificate(alias);
                // Certificate verification failed
                if (caCert instanceof X509Certificate) {
                    certificate.verify(caCert.getPublicKey());
                    isValid = true;
                    break;
                }
            }

        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | SignatureException |
                 InvalidKeyException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        if (isValid) {
            System.out.println("Client certificate is valid.");
        } else {
            System.out.println("Client certificate is not valid.");
        }
        return isValid;
    }
    public Key getServerKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        return getKeystore().getKey("1", getKeyStorePassword().toCharArray());
    }
}
