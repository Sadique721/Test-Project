package com.savbill.radius.aaa.eap.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHello {

    int type = EAPConstant.CLIENT_HELLO;
    int length;
    int version;
    byte[] random = new byte[32];
    int sessionId;
    int cipherSuiteLength;
    List<Integer> cipherSuiteList;
    int compressionMethodLength;
    int compressionMethod;
    int extensionLength;
    List<ExtensionType> extensionTypeList = new ArrayList<>();

    public ClientHello(InputStream in) throws IOException {
        length = (in.read() & 0x0ff) << 16 | (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        version = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);
        in.read(random);
        sessionId = in.read() & 0x0ff;
        cipherSuiteLength = ((in.read() & 0x0ff) << 8 | (in.read() & 0x0ff));
        int cipherSuitLength = cipherSuiteLength / 2;
        cipherSuiteList = new ArrayList<>(cipherSuitLength);
        for (int i = cipherSuitLength; i > 0; i--) {
            Integer cipher = ((in.read() & 0x0ff) << 8 | (in.read() & 0x0ff));
            cipherSuiteList.add(cipher);
        }
        compressionMethodLength = in.read() & 0x0ff;
        compressionMethod = in.read() & 0x0ff;
        extensionLength = ((in.read() & 0x0ff) << 8| (in.read() & 0x0ff));
        readExtensionData(in);
        System.out.println(toString());
    }

    private void readExtensionData(InputStream in) throws IOException {
        do {
            int extensionType = (in.read() & 0x0ff << 8 | in.read() & 0x0ff);
            int extensionLength = (in.read() & 0x0ff << 8 | in.read() & 0x0ff);
            byte[] extensionData = new byte[extensionLength];
            in.read(extensionData);
            ExtensionType extensionType1 = new ExtensionType(extensionType, extensionLength, extensionData);
            extensionTypeList.add(extensionType1);
        }while (in.available() >1);
    }

    @Override
    public String toString() {
        return "ClientHello{" +
                "length=" + length +
                ", version=" + version +
                ", random=" + Arrays.toString(random) +
                ", sessionId=" + sessionId +
                ", cipherSuiteLength=" + cipherSuiteLength +
                ", cipherSuiteList=" + cipherSuiteList +
                ", compressionMethodLength=" + compressionMethodLength +
                ", compressionMethod=" + compressionMethod +
                ", extensionLength=" + extensionLength +
                ", extensionTypeList=" + extensionTypeList +
                '}';
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getRandom() {
        return random;
    }

    public void setRandom(byte[] random) {
        this.random = random;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getCipherSuiteLength() {
        return cipherSuiteLength;
    }

    public void setCipherSuiteLength(int cipherSuiteLength) {
        this.cipherSuiteLength = cipherSuiteLength;
    }

    public List<Integer> getCipherSuiteList() {
        return cipherSuiteList;
    }

    public void setCipherSuiteList(List<Integer> cipherSuiteList) {
        this.cipherSuiteList = cipherSuiteList;
    }

    public int getCompressionMethodLength() {
        return compressionMethodLength;
    }

    public void setCompressionMethodLength(int compressionMethodLength) {
        this.compressionMethodLength = compressionMethodLength;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    public void setCompressionMethod(int compressionMethod) {
        this.compressionMethod = compressionMethod;
    }

    public int getExtensionLength() {
        return extensionLength;
    }

    public void setExtensionLength(int extensionLength) {
        this.extensionLength = extensionLength;
    }

    public List<ExtensionType> getExtensionTypeList() {
        return extensionTypeList;
    }

    public void setExtensionTypeList(List<ExtensionType> extensionTypeList) {
        this.extensionTypeList = extensionTypeList;
    }
}
