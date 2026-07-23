package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CertificateVerify {
    private int type = EAPConstant.CERTIFICATE_VERIFY; // 1 byte

    private int length; // 3 byte

    private int signatureAlgorithm; // 2byte

    private int signatureLength; // 2 byte

    private byte[] signature;
    public CertificateVerify(InputStream in) throws IOException {
        length = (in.read() & 0x0ff) << 16 | (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        signatureAlgorithm = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        signatureLength = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        signature = new byte[signatureLength];
        in.read(signature);
        //System.out.println("Signature: " + toString());
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

    @Override
    public String toString() {
        return "CertificateVerify{" +
                "type=" + type +
                ", length=" + length +
                ", signatureAlgorithm=" + signatureAlgorithm +
                ", signatureLength=" + signatureLength +
                ", signature=" + RadiusUtil.getHexString(signature) +
                '}';
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(getType() & 0x0ff);

        out.write(getLength() >> 16 & 0x0ff);
        out.write(getLength() >> 8 & 0x0ff);
        out.write(getLength() & 0x0ff);

        out.write(getSignatureAlgorithm() >> 8 & 0x0ff);
        out.write(getSignatureAlgorithm() & 0x0ff);

        out.write(getSignatureLength() >> 8 & 0x0ff);
        out.write(getSignatureLength() & 0x0ff);

        out.write(getSignature());

        byte[] bytes = out.toByteArray();

        System.out.println("Certificate verify string: " + RadiusUtil.getHexString(bytes));
        return bytes;

    }
}
