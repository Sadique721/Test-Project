package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class HandshakeProtocol {
    private int type = EAPConstant.HANDSHAKE_PROTOCOL; // 1 byte
    private int version; // 2 byte
    private int length; // 2 byte

    private ClientHello clientHello;

    private ServerCertificate serverCertificate;

    private ClientKeyExchange clientKeyExchange;

    private CertificateVerify certificateVerify;

    private byte[] encryptedMessage;

    private byte[] messageBytes;


    public HandshakeProtocol(InputStream in, boolean isFinishedMessage) throws IOException {

        version = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        length = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);

        messageBytes = new byte[length];
        in.read(messageBytes);

        InputStream inputStream = new ByteArrayInputStream(Arrays.copyOf(messageBytes, messageBytes.length));

        if (isFinishedMessage) { // length 80 for RSA and 40 for ECDHE
            encryptedMessage = new byte[length];
            inputStream.read(encryptedMessage);
            System.out.println("Finished Message: " + "Length: " + encryptedMessage.length+ "Encrypted data: " + RadiusUtil.getHexString(encryptedMessage));
            messageBytes = new byte[0];
           // messageBytes = encryptedMessage;
            return;
        }

        if (length > 4) {
            int handShakeType = inputStream.read() & 0x0ff;

            if (EAPConstant.CLIENT_HELLO.equals(handShakeType)) {
                clientHello = new ClientHello(inputStream);
            } else if (EAPConstant.SERVER_CERTIFICATE.equals(handShakeType)) {
                serverCertificate = new ServerCertificate(inputStream);
            } else if (EAPConstant.CLIENT_KEY_EXCHANGE.equals(handShakeType)) {
                clientKeyExchange = new ClientKeyExchange(inputStream);
            } else if (EAPConstant.CERTIFICATE_VERIFY.equals(handShakeType)) {
                certificateVerify = new CertificateVerify(inputStream);
                //  messageBytes = new byte[0];
            } else {
                messageBytes = new byte[0];
            }
        }

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ClientHello getClientHello() {
        return clientHello;
    }

    public void setClientHello(ClientHello clientHello) {
        this.clientHello = clientHello;
    }

    public ServerCertificate getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(ServerCertificate serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public ClientKeyExchange getClientKeyExchange() {
        return clientKeyExchange;
    }

    public void setClientKeyExchange(ClientKeyExchange clientKeyExchange) {
        this.clientKeyExchange = clientKeyExchange;
    }

    public CertificateVerify getCertificateVerify() {
        return certificateVerify;
    }

    public void setCertificateVerify(CertificateVerify certificateVerify) {
        this.certificateVerify = certificateVerify;
    }

    public byte[] getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(byte[] encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    // TODO: This needs to be done to get bytes from all lower level record
    public byte[] getBytes() {
        return messageBytes;
    }

    /*@Override
    public String toString() {
        return "HandshakeProtocol{" +
                "type=" + type +
                ", version=" + version +
                ", length=" + length +
                ", clientHello=" + clientHello +
                ", serverCertificate=" + serverCertificate +
                ", clientKeyExchange=" + clientKeyExchange +
                ", certificateVerify=" + certificateVerify +
                ", encryptedMessage=" + RadiusUtil.getHexString(encryptedMessage) +
                '}';
    }*/
}
