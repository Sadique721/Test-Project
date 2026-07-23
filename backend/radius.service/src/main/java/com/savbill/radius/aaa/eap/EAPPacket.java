package com.savbill.radius.aaa.eap;

public class EAPPacket {

    int eapCodeType;
    int eapIdentifier;
    int eapLength;
    int type;
    String identity;
    private int flag = 32;

    private byte[] eapData;
    boolean isMoreFragmented;

    int eapTlsLength;

    public EAPPacket() {

    }
    public EAPPacket(int eapCodeType, int eapIdentifier, int eapLength, int type, String data, int flag) {
        this.eapCodeType = eapCodeType;
        this.eapIdentifier = eapIdentifier;
        this.eapLength = eapLength;
        this.type = type;
        this.identity = data;
        this.flag = flag;
    }

    public EAPPacket(int eapCodeType, int eapIdentifier, int eapLength) {
        this.eapCodeType = eapCodeType;
        this.eapIdentifier = eapIdentifier;
        this.eapLength = eapLength;
    }

    public int getEapCodeType() {
        return eapCodeType;
    }

    public void setEapCodeType(int eapCodeType) {
        this.eapCodeType = eapCodeType;
    }

    public int getEapIdentifier() {
        return eapIdentifier;
    }

    public void setEapIdentifier(int eapIdentifier) {
        this.eapIdentifier = eapIdentifier;
    }

    public int getEapLength() {
        return eapLength;
    }

    public void setEapLength(int eapLength) {
        this.eapLength = eapLength;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public byte[] getEapData() {
        return eapData;
    }

    public void setEapData(byte[] eapData) {
        this.eapData = eapData;
    }

    public boolean isMoreFragmented() {
        return isMoreFragmented;
    }

    public void setMoreFragmented(boolean moreFragmented) {
        isMoreFragmented = moreFragmented;
    }

    public int getEapTlsLength() {
        return eapTlsLength;
    }

    public void setEapTlsLength(int eapTlsLength) {
        this.eapTlsLength = eapTlsLength;
    }

    @Override
    public String toString() {
        return "EAPResponosePacket{" +
                "eapCodeType=" + eapCodeType +
                ", eapIdentifier=" + eapIdentifier +
                ", eapLength=" + eapLength +
                ", type=" + type +
                ", data='" + identity + '\'' +
                ", flag=" + flag +
                ", eapTlsLength=" + eapTlsLength +
                '}';
    }
}
