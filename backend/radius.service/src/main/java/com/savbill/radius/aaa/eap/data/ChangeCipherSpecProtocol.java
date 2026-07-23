package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChangeCipherSpecProtocol {

    private int type = EAPConstant.CHANGE_CIPHER_SPEC;

    private int version = 771; // 2 byte

    private int length = 1; // 2 byte

    private byte[] changeCipherData = new byte[01];
    public ChangeCipherSpecProtocol(InputStream in) throws IOException {

        version = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        length = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        changeCipherData = new byte[length];
        in.read(changeCipherData);
        toString();
    }

    public ChangeCipherSpecProtocol() {

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

    public byte[] getChangeCipherData() {
        return changeCipherData;
    }

    public void setChangeCipherData(byte[] changeCipherData) {
        this.changeCipherData = changeCipherData;
    }

    @Override
    public String toString() {
        return "ChangeCipherSpecProtocol{" +
                "type=" + type +
                ", version=" + version +
                ", length=" + length +
                ", changeCipherData=" + RadiusUtil.getHexString(changeCipherData) +
                '}';
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(getLength() + 4);

        bos.write(getType() & 0x0ff);

        bos.write(getVersion() >> 8 & 0x0ff);
        bos.write(getVersion() & 0x0ff);

        bos.write(getLength() >> 8 & 0x0ff);
        bos.write(getLength() & 0x0ff);

        byte[] bytes = new byte[01];
        bytes[0] = 01;

        setChangeCipherData(bytes);

        bos.write(getChangeCipherData());

        return bos.toByteArray();

    }
}
