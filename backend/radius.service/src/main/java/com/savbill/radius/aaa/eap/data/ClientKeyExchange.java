package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.IOException;
import java.io.InputStream;

public class ClientKeyExchange {

    private int type = EAPConstant.CLIENT_KEY_EXCHANGE;

    private int length;
    private int publicKeyLength = 0 ;
    private byte[] publicKey;
    public ClientKeyExchange(InputStream in) throws IOException {
        length =  (in.read() & 0x0ff) << 16 |(in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        publicKeyLength = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff); // in.read() & 0x0ff; for DH or ECDHE i guess i would be one byte
        publicKey = new byte[publicKeyLength];
        in.read(publicKey);
        System.out.println(toString());
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPublicKeyLength() {
        return publicKeyLength;
    }

    public void setPublicKeyLength(int publicKeyLength) {
        this.publicKeyLength = publicKeyLength;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "ClientKeyExchange{" +
                "publicKeyLength=" + publicKeyLength +
                ", publicKey=" + RadiusUtil.getHexString(publicKey) +
                '}';
    }
}
