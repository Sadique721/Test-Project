package com.savbill.radius.aaa.eap.generater;

import com.savbill.radius.aaa.eap.EAPSession;
import com.savbill.radius.aaa.eap.data.ServerKeyExchange;
import com.savbill.radius.aaa.eap.util.*;
import com.savbill.radius.aaa.eap.util.*;
import com.savbill.radius.aaa.util.RadiusUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class ServerKeyExchangeGenerator {
    @Autowired
    ECDHKeyExchangeImpl keyExchange = new ECDHKeyExchangeImpl();
    DHKeyExchange dhKeyExchange;



    public ServerKeyExchange generateServeKeyExchange(byte[] random, byte[] serverRandom, int cipherSuite, byte[] serverHelloServerRandom, byte[] clientRandom, EAPSession eapSession) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, InvalidAlgorithmParameterException, SignatureException, InvalidKeyException {
        PrivateKey serverPrivateKey = CertificateUtil.readPrivateKey("server.key");

        if (Integer.valueOf(103).equals(cipherSuite)) {
            dhKeyExchange = new DHKeyExchange(false);
            byte[] dheParam = dhKeyExchange.generateParameters();

            ServerKeyExchange serverKeyExchangeLatest = new ServerKeyExchange();

            byte[] client_server_random = CalculateKeyingMaterial.concatenateByteArrays(clientRandom, serverHelloServerRandom);
            byte[] valueToBeEncrypted = CalculateKeyingMaterial.concatenateByteArrays(client_server_random, dheParam);

            byte[] signature = RSAPSSSignature.sign(serverPrivateKey, valueToBeEncrypted);

            serverKeyExchangeLatest.setServerKeyExchangeDHE(dheParam);
            serverKeyExchangeLatest.setSignature(signature);
            serverKeyExchangeLatest.setSignatureLength(signature.length);

            eapSession.setDheKeyExchange(dhKeyExchange);
            return  serverKeyExchangeLatest;
        }

        KeyPair keyPair = keyExchange.generateECDHKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        byte[] publicKeyBytes = Arrays.copyOfRange(publicKey.getEncoded(), 12, publicKey.getEncoded().length);

        System.out.println("Original public key: " + RadiusUtil.getHexString(publicKey.getEncoded()));
        System.out.println("Public key string: " + RadiusUtil.getHexString(publicKeyBytes));


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(random);
        out.write(serverRandom);
        out.write(DatatypeConverter.parseHexBinary("03001d20"));
        out.write(publicKeyBytes);

        byte[] messageBytes = out.toByteArray();

        System.out.println("Generated messaged bytes for signing: " + RadiusUtil.getHexString(messageBytes));

        byte[] signature = RSAPSSSignature.sign(serverPrivateKey, messageBytes);

        System.out.println("Length of signature will be: " + signature.length);
        System.out.println("Signature for server hello: " + RadiusUtil.getHexString(signature));


        ServerKeyExchange exch = new ServerKeyExchange(publicKeyBytes, signature);

        System.out.println(RadiusUtil.getHexString(exch.getBytes()));


        return exch ;
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        ServerKeyExchangeGenerator gen = new ServerKeyExchangeGenerator();
       // gen.generateServeKeyExchange(random, serverHello.getServerRandom());
    }

}
