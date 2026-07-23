package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ServerKeyExchange {

    private  int handShakeType = EAPConstant.SERVER_KEY_EXCHANGE; //1byte
    private int length = 296; //3 byte if its ECDHE_RSA

    private int curveType = 3; //1 byte
    private int namedCurve = 29; // 2 byte

    private int pubKeyLength = 32; // 1 byte

    private byte[] publicKey; // 32 byte

    private int signatureAlgorithm = 1025; // 2 byte //TODO: further we have to breakdown this // 0401: 1025 //0804: 2052

    private int signatureLength = 256; // 2 byte

    private byte[] signature; // 256 bytes

    private byte[] serverKeyExchangeDHE;


    public ServerKeyExchange(byte[] publicKey, byte[] signature) {
        this.publicKey = publicKey;
        this.signature = signature;
    }

    public ServerKeyExchange() {

    }

    public int getHandShakeType() {
        return handShakeType;
    }

    public void setHandShakeType(int handShakeType) {
        this.handShakeType = handShakeType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCurveType() {
        return curveType;
    }

    public void setCurveType(int curveType) {
        this.curveType = curveType;
    }

    public int getNamedCurve() {
        return namedCurve;
    }

    public void setNamedCurve(int namedCurve) {
        this.namedCurve = namedCurve;
    }

    public int getPubKeyLength() {
        return pubKeyLength;
    }

    public void setPubKeyLength(int pubKeyLength) {
        this.pubKeyLength = pubKeyLength;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public int getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(int signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public int getSignatureLength() {
        return signatureLength;
    }

    public void setSignatureLength(int signatureLength) {
        this.signatureLength = signatureLength;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(getHandShakeType() & 0x0ff);

        int newLength = getServerKeyExchangeDHE().length + 4 + getSignature().length;
        setLength(newLength);

        bos.write(getLength() >> 16 & 0x0ff);
        bos.write(getLength() >> 8 & 0x0ff);
        bos.write(getLength() & 0x0ff);

      /*  bos.write(getCurveType() & 0x0ff);
        bos.write(getNamedCurve() >> 8 & 0x0ff);
        bos.write(getNamedCurve() & 0x0ff);

        bos.write(getPubKeyLength());
        bos.write(getPublicKey());*/

        bos.write(getServerKeyExchangeDHE());

        bos.write(getSignatureAlgorithm() >> 8 & 0x0ff);
        bos.write(getSignatureAlgorithm() & 0x0ff);

        bos.write(getSignatureLength() >> 8 & 0x0ff);
        bos.write(getSignatureLength() & 0x0ff);

        bos.write(getSignature());

        System.out.println(toString());

        return  bos.toByteArray();
    }

    public byte[] getServerKeyExchangeDHE() {
        return serverKeyExchangeDHE;
    }

    public void setServerKeyExchangeDHE(byte[] serverKeyExchangeDHE) {
        this.serverKeyExchangeDHE = serverKeyExchangeDHE;
    }

    @Override
    public String toString() {
        return "ServerKeyExchange{" +
                "handShakeType=" + handShakeType +
                ", length=" + length +
                ", curveType=" + curveType +
                ", namedCurve=" + namedCurve +
                ", pubKeyLength=" + pubKeyLength +
                ", publicKey=" + Arrays.toString(publicKey) +
                ", signatureAlgorithm=" + signatureAlgorithm +
                ", signatureLength=" + signatureLength +
                ", signature=" + Arrays.toString(signature) +
                "DHE: " + RadiusUtil.getHexString(getServerKeyExchangeDHE()) +
                '}';
    }


}
