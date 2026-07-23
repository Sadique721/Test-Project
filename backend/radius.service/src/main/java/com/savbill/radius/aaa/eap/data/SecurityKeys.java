package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

public class SecurityKeys {

    private byte[] clientMACKey;
    private byte[] serverMACKey;
    private byte[] clientWriteKey;
    private byte[] serverWriteKey;
    private byte[] clientIv;
    private byte[] serverIv;

    public byte[] getClientMACKey() {
        return clientMACKey;
    }

    public void setClientMACKey(byte[] clientMACKey) {
        this.clientMACKey = clientMACKey;
    }

    public byte[] getServerMACKey() {
        return serverMACKey;
    }

    public void setServerMACKey(byte[] serverMACKey) {
        this.serverMACKey = serverMACKey;
    }

    public byte[] getClientWriteKey() {
        return clientWriteKey;
    }

    public void setClientWriteKey(byte[] clientWriteKey) {
        this.clientWriteKey = clientWriteKey;
    }

    public byte[] getServerWriteKey() {
        return serverWriteKey;
    }

    public void setServerWriteKey(byte[] serverWriteKey) {
        this.serverWriteKey = serverWriteKey;
    }

    public byte[] getClientIv() {
        return clientIv;
    }

    public void setClientIv(byte[] clientIv) {
        this.clientIv = clientIv;
    }

    public byte[] getServerIv() {
        return serverIv;
    }

    public void setServerIv(byte[] serverIv) {
        this.serverIv = serverIv;
    }

    @Override
    public String toString() {
        return "SecurityKeys{" +
                "\n length: " + clientMACKey.length + " clientMACKey: " + RadiusUtil.getHexString(clientMACKey) +
                "\n length: " + serverMACKey.length + ", serverMACKey: " + RadiusUtil.getHexString(serverMACKey) +
                "\n length: " + clientWriteKey.length + ", clientWriteKey: " + RadiusUtil.getHexString(clientWriteKey) +
                "\n length: " + serverWriteKey.length + ", serverWriteKey: " + RadiusUtil.getHexString(serverWriteKey) +
                "\n}";
    }
}
