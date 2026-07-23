package com.savbill.radius.aaa.eap;

import com.savbill.radius.aaa.eap.data.EAPConstant;
import com.savbill.radius.aaa.util.RadiusUtil;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

@Getter
public class ApplicationData {
    private  int type = EAPConstant.APPLICATION_DATA;
    private int version; // 2 byte
    private int length; // 2 byte
    private byte[] encryptedDataBytes;

    public ApplicationData(InputStream in) throws IOException {
        this.version = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        this.length = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);

        encryptedDataBytes = new byte[length];
        in.read(encryptedDataBytes);
    }

    @Override
    public String toString() {
        return "ApplicationData{" +
                "type=" + type +
                ", version=" + version +
                ", length=" + length +
                ", encryptedDataBytes=" + RadiusUtil.getHexString(encryptedDataBytes) +
                '}';
    }
}
