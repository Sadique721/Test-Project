package com.savbill.radius.aaa.eap.data;

import java.util.Arrays;

public class ExtensionType {

    int extensionType; // 2byte
    int extensionLength; // 2 byte

    byte[] extensionData;

    public ExtensionType(int extensionType, int extensionLength, byte[] extensionData) {
        this.extensionType = extensionType;
        this.extensionLength = extensionLength;
        this.extensionData = extensionData;
    }

    public ExtensionType(int extensionType, int extensionLength) {
        this.extensionType = extensionType;
        this.extensionLength = extensionLength;
    }

    public void setExtensionData(byte[] extensionData) {
        this.extensionData = extensionData;
    }

    public int getExtensionType() {
        return extensionType;
    }

    public int getExtensionLength() {
        return extensionLength;
    }

    public byte[] getExtensionData() {
        return extensionData;
    }

    @Override
    public String toString() {
        return "ExtensionType{" +
                "extensionType=" + extensionType +
                ", extensionLength=" + extensionLength +
                ", extensionData=" + Arrays.toString(extensionData) +
                '}';
    }
}
