package com.savbill.radius.aaa.eap.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.security.*;

@Component
public class ECDHKeyExchangeImpl {

    private KeyPair keyPair;

    public KeyPair generateECDHKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        // Add Bouncy Castle Provider
        Security.addProvider(new BouncyCastleProvider());

        // Enable X25519 curve if not already enabled
        //  System.setProperty("org.bouncycastle.ec.disable", "false");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("X25519", "BC");

        return keyPairGenerator.generateKeyPair();
    }
}
