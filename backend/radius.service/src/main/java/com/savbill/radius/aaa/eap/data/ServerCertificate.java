package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.eap.util.CertificateUtil;
import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Optional;

public class ServerCertificate {

    private  int handShakeType = EAPConstant.SERVER_CERTIFICATE; //1byte
    private int length; // 3 byte

    private int certificateLength; // 3 byte

    //TODO: below 4 instance variable will be part of  list

    private int serverCertificateLength; // 3 byte

    byte[] serverCertificate;

    private int caCertificateLength; // 3 byte

    byte[] caCertificate;

    public ServerCertificate(byte[] serverCertificate, byte[] caCertificate) {
        this.serverCertificate = serverCertificate;
        this.caCertificate = caCertificate;

        this.serverCertificateLength = this.serverCertificate.length;
        this.caCertificateLength = this.caCertificate.length;

        this.certificateLength = getServerCertificateLength() + getCaCertificateLength() + 6; // bytes for that field

        this.length = getCertificateLength() + 3;
    }

    public ServerCertificate(InputStream in) throws IOException {

        length = (in.read() & 0x0ff) << 16 | (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        certificateLength = (in.read() & 0x0ff) << 16 | (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        serverCertificateLength = (in.read() & 0x0ff) << 16 | (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        serverCertificate = new byte[serverCertificateLength];
        in.read(serverCertificate);
        caCertificateLength = (in.read() & 0x0ff) << 16 | (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        caCertificate = new byte[caCertificateLength];
        in.read(caCertificate);
        System.out.println(toString());
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

    public int getCertificateLength() {
        return certificateLength;
    }

    public void setCertificateLength(int certificateLength) {
        this.certificateLength = certificateLength;
    }

    public byte[] getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(byte[] serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public byte[] getCaCertificate() {
        return caCertificate;
    }

    public void setCaCertificate(byte[] caCertificate) {
        this.caCertificate = caCertificate;
    }

    public int getServerCertificateLength() {
        return serverCertificateLength;
    }

    public void setServerCertificateLength(int serverCertificateLength) {
        this.serverCertificateLength = serverCertificateLength;
    }

    public int getCaCertificateLength() {
        return caCertificateLength;
    }

    public void setCaCertificateLength(int caCertificateLength) {
        this.caCertificateLength = caCertificateLength;
    }

    public byte[] getBytes() throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(getLength() + 4);

        bos.write(getHandShakeType() & 0x0ff);

        bos.write(getLength() >> 16 & 0x0ff);
        bos.write(getLength() >> 8 & 0x0ff);
        bos.write(getLength() & 0x0ff);

        bos.write(getCertificateLength() >> 16 & 0x0ff);
        bos.write(getCertificateLength() >> 8 & 0x0ff);
        bos.write(getCertificateLength() & 0x0ff);

        bos.write(getServerCertificateLength() >> 16 & 0x0ff);
        bos.write(getServerCertificateLength() >> 8 & 0x0ff);
        bos.write(getServerCertificateLength() & 0x0ff);

        bos.write(getServerCertificate());

        bos.write(getCaCertificateLength() >> 16 & 0x0ff);
        bos.write(getCaCertificateLength() >> 8 & 0x0ff);
        bos.write(getCaCertificateLength() & 0x0ff);

        bos.write(getCaCertificate());

      //  System.out.println(toString());

        return  bos.toByteArray();
    }

    /*@Override
    public String toString() {
        return "ServerCertificate{" +
                "handShakeType=" + handShakeType +
                ", length=" + length +
                ", certificateLength=" + certificateLength +
                ", serverCertificateLength=" + serverCertificateLength +
                ", serverCertificate=" + RadiusUtil.getHexString(serverCertificate) +
                ", caCertificateLength=" + caCertificateLength +
                ", caCertificate=" + RadiusUtil.getHexString(caCertificate) +
                '}';
    }*/

    public static void main(String[] args) throws CertificateEncodingException, IOException {
        Optional<Certificate> serverCertificate = CertificateUtil.parseCertificate("/home/savbill/locat_certy/server.crt");
        Optional<Certificate> clientCertificate = CertificateUtil.parseCertificate("/home/savbill/locat_certy/ca.pem");

        ServerCertificate serverCertificate1 = new ServerCertificate(serverCertificate.get().getEncoded(), clientCertificate.get().getEncoded());
        byte[] bytes = serverCertificate1.getBytes();
        System.out.println(RadiusUtil.getHexString(bytes));
    }
}
