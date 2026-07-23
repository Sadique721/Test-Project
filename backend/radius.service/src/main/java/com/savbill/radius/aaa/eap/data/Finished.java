package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Finished {

    public int type = EAPConstant.FINISHED;

    public int length =  12; // 3 bytes;

    public  byte[] verifyData;

    public  Finished() {

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

    public byte[] getVerifyData() {
        return verifyData;
    }

    public void setVerifyData(byte[] verifyData) {
        this.verifyData = verifyData;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(getType() & 0x0ff);

        bos.write(getLength() >> 16 & 0x0ff);
        bos.write(getLength() >> 8 & 0x0ff);
        bos.write(getLength() & 0x0ff);

        bos.write(getVerifyData());

        return bos.toByteArray();
    }

    @Override
    public String toString() {
        return "Finished{" +
                "type=" + type +
                ", length=" + length +
                ", verifyData=" + RadiusUtil.getHexString(verifyData) +
                '}';
    }
}
