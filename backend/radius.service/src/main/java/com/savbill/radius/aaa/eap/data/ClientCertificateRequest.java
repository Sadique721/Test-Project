package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientCertificateRequest {

    private  int handShakeType = EAPConstant.CERTIFICATE_REQUEST; //1byte
    private int length; // 3 byte

    // by default certificate supported count charge
    private int certificateTypeCount = 3;

    List<Integer> supportedCertificateType = new ArrayList<>(certificateTypeCount);

    private int signatureAlgorithmLength = 40;

    private List<Integer> signatureAlgorithm = new ArrayList<>(signatureAlgorithmLength/2); //TODO: validation required and needs to be update in future

    private int distinguishedNamesLength = 0; // 2 byte

    private  int distinguishedLength = 0;

    private byte[] distinguishedData;

    public ClientCertificateRequest(byte[] distinguishedData) {
        // Below algorithms will be based on support for this algorithm in system
        // right now added every algorithm as per reference from pcap.
        signatureAlgorithm.add(1027); //0403
        signatureAlgorithm.add(1283); //0503
        signatureAlgorithm.add(1539); //0603
        signatureAlgorithm.add(2055); //0807
        signatureAlgorithm.add(2056); //0808
        signatureAlgorithm.add(2057); //0809
        signatureAlgorithm.add(2058); //080a
        signatureAlgorithm.add(2059); //080b
        signatureAlgorithm.add(1025); //0401
        signatureAlgorithm.add(2052); //0804
        signatureAlgorithm.add(2053); //0805
        signatureAlgorithm.add(2054); //0806
        signatureAlgorithm.add(1281); //0501
        signatureAlgorithm.add(1537); //0601
        signatureAlgorithm.add(771); //0303
        signatureAlgorithm.add(769); //0301
        signatureAlgorithm.add(770); //0302
        signatureAlgorithm.add(1026); //0402
        signatureAlgorithm.add(1282); //0502
        signatureAlgorithm.add(1538); //0602

        supportedCertificateType.add(1);
        supportedCertificateType.add(2);
        supportedCertificateType.add(64);

        this.distinguishedData = distinguishedData;

        if (getDistinguishedData() != null) {
            this.distinguishedLength = this.distinguishedData.length;
            this.distinguishedNamesLength = this.distinguishedLength + 2;
        }

        this.length = distinguishedNamesLength + signatureAlgorithmLength + certificateTypeCount + 5;

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

    public int getCertificateTypeCount() {
        return certificateTypeCount;
    }

    public void setCertificateTypeCount(int certificateTypeCount) {
        this.certificateTypeCount = certificateTypeCount;
    }

    public List<Integer> getSupportedCertificateType() {
        return supportedCertificateType;
    }

    public void setSupportedCertificateType(List<Integer> supportedCertificateType) {
        this.supportedCertificateType = supportedCertificateType;
    }

    public int getSignatureAlgorithmLength() {
        return signatureAlgorithmLength;
    }

    public void setSignatureAlgorithmLength(int signatureAlgorithmLength) {
        this.signatureAlgorithmLength = signatureAlgorithmLength;
    }

    public List<Integer> getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(List<Integer> signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public int getDistinguishedNamesLength() {
        return distinguishedNamesLength;
    }

    public void setDistinguishedNamesLength(int distinguishedNamesLength) {
        this.distinguishedNamesLength = distinguishedNamesLength;
    }

    public byte[] getDistinguishedData() {
        return distinguishedData;
    }

    public void setDistinguishedData(byte[] distinguishedData) {
        this.distinguishedData = distinguishedData;
    }

    public int getDistinguishedLength() {
        return distinguishedLength;
    }

    public void setDistinguishedLength(int distinguishedLength) {
        this.distinguishedLength = distinguishedLength;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(getLength());

        bos.write(getHandShakeType() & 0x0ff);

        bos.write(getLength() >> 16 & 0x0ff);
        bos.write(getLength() >> 8 & 0x0ff);
        bos.write(getLength() & 0x0ff);

        bos.write(getCertificateTypeCount() & 0x0ff);
        for (Integer supportedType: getSupportedCertificateType()) {
            bos.write(supportedType & 0x0ff);
        }

        bos.write(getSignatureAlgorithmLength() >> 8 & 0x0ff);
        bos.write(getSignatureAlgorithmLength() & 0x0ff);

        for (Integer signatureAlgorithm: getSignatureAlgorithm()) {
            bos.write(signatureAlgorithm >> 8 & 0x0ff);
            bos.write(signatureAlgorithm & 0x0ff);
        }

        bos.write(getDistinguishedNamesLength() >> 8 & 0x0ff);
        bos.write(getDistinguishedNamesLength() & 0x0ff);

        bos.write(getDistinguishedLength() >> 8 & 0x0ff);
        bos.write(getDistinguishedLength() & 0x0ff);

        if (getDistinguishedData() != null)
            bos.write(getDistinguishedData());
        System.out.println(toString());

        return  bos.toByteArray();
    }

    @Override
    public String toString() {
        return "ClientCertificateRequest{" +
                "handShakeType=" + handShakeType +
                ", length=" + length +
                ", certificateTypeCount=" + certificateTypeCount +
                ", supportedCertificateType=" + supportedCertificateType +
                ", signatureAlgorithmLength=" + signatureAlgorithmLength +
                ", signatureAlgorithm=" + signatureAlgorithm +
                ", distinguishedNamesLength=" + distinguishedNamesLength +
                ", distinguishedLength=" + distinguishedLength +
                ", distinguishedData=" + RadiusUtil.getHexString(distinguishedData) +
                '}';
    }

    public static void main(String[] args) throws IOException {
        ClientCertificateRequest clientRequest = new ClientCertificateRequest(null);
        byte[] bytes = clientRequest.getBytes();
        System.out.println(RadiusUtil.getHexString(bytes));
    }
}
