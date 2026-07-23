package com.savbill.radius.aaa.eap.data;

import java.io.ByteArrayOutputStream;

public class ServerHelloDone {

    private  int handShakeType = EAPConstant.SERVER_HELLO_DONE; //1byte
    private int length = 0; // 3 byte

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

    public byte[] getBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4);

        bos.write(getHandShakeType() & 0x0ff);

        bos.write(getLength() >> 16 & 0x0ff);
        bos.write(getLength() >> 8 & 0x0ff);
        bos.write(getLength() & 0x0ff);

        return bos.toByteArray();
    }
    @Override
    public String toString() {
        return "ServerHelloDone{" +
                "handShakeType=" + handShakeType +
                ", length=" + length +
                '}';
    }
}
