package com.savbill.radius.aaa.eap.data;

import com.savbill.radius.aaa.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

public class ServerHello {

    private  int handShakeType = EAPConstant.SERVER_HELLO; //1byte
    private int length; //3 byte
    private int version = 771; // 2 byte
    private byte[] serverRandom; // 32 byte
    private int sessionId = 0; // 1 byte
    private int cipherSuite =  47; //103; //47; //61; //47; //49200; // 2 byte //c030  // 61  for 003d  Cipher Suite: TLS_RSA_WITH_AES_256_CBC_SHA256 (0x003d) // 47 : 002f aes cbc sha
    private int compressionMethod = 00 ; // 1 byte
    private int extensionLength; // 2 byte
    private List<ExtensionType> extensionTypeList = new ArrayList<>();
    // Generate a SecureRandom object
   private  SecureRandom secureRandom = new SecureRandom();
   int serverHelloFixedLength = 40;


    public ServerHello() {
        serverRandom = new byte[32]; // default server random value is 32 byte
        secureRandom.nextBytes(serverRandom);

        sessionId = 0;

        ExtensionType renegotiationinfo = new ExtensionType(65281,1);
        byte[] renegotiationBytes = {00};
        renegotiationinfo.setExtensionData(renegotiationBytes);

        extensionTypeList.add(renegotiationinfo);

        /*ExtensionType ecPointFormat = new ExtensionType(11,4);
        byte[] ecPointFormatBytes = {03,00,01,02};
        ecPointFormat.setExtensionData(ecPointFormatBytes);*/

        //extensionTypeList.add(ecPointFormat);

        ExtensionType encrypThenMac = new ExtensionType(22, 0);
        extensionTypeList.add(encrypThenMac);

        ExtensionType extendedMasterSecret = new ExtensionType(23,0);
        extensionTypeList.add(extendedMasterSecret);

        int extensionLength = 0;
        int noOfExtensions = extensionTypeList.size();
        for (ExtensionType extensionType: extensionTypeList) {
            extensionLength = extensionLength + extensionType.extensionLength;
        }
        extensionLength = extensionLength + noOfExtensions * 4;

        this.extensionLength = extensionLength;
        this.length = serverHelloFixedLength + extensionLength;

    }
    public  byte[] getBytes() throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(getLength() + 4);

        bos.write(getHandShakeType() & 0x0ff);

        bos.write(getLength() >> 16 & 0x0ff);
        bos.write(getLength() >> 8 & 0x0ff);
        bos.write(getLength() & 0x0ff);

        bos.write(getVersion() >> 8 & 0x0ff);
        bos.write(getVersion() & 0x0ff);

        bos.write(getServerRandom());

        bos.write(getSessionId() & 0x0ff);

        bos.write(getCipherSuite() >> 8 & 0x0ff);
        bos.write(getCipherSuite() & 0x0ff);

        bos.write(getCompressionMethod() & 0x0ff);

        bos.write(getExtensionLength() >> 8 & 0x0ff);
        bos.write(getExtensionLength() & 0x0ff);

        for (ExtensionType extensionType: getExtensionTypeList()) {
            bos.write(extensionType.getExtensionType() >> 8 & 0x0ff);
            bos.write(extensionType.getExtensionType() & 0x0ff);
            bos.write(extensionType.getExtensionLength() >> 8 & 0x0ff);
            bos.write(extensionType.getExtensionLength()  & 0x0ff);
            if (extensionType.getExtensionLength() > 0) {
                bos.write(extensionType.getExtensionData());
            }
        }

        return bos.toByteArray();
    }

    public int getHandShakeType() {
        return handShakeType;
    }

    public int getLength() {
        return length;
    }

    public int getVersion() {
        return version;
    }

    public byte[] getServerRandom() {
        return serverRandom;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getCipherSuite() {
        return cipherSuite;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    public int getExtensionLength() {
        return extensionLength;
    }

    public List<ExtensionType> getExtensionTypeList() {
        return extensionTypeList;
    }

    public SecureRandom getSecureRandom() {
        return secureRandom;
    }

    public static void main(String[] args) throws IOException {
        ServerHello serverHello = new ServerHello();
        byte[] bytes = serverHello.getBytes();
        String hexString = RadiusUtil.getHexString(bytes);
        System.out.println(hexString);

        String byteArray = "020000390303483109c7226a2c8a109921353cf1b2c13e263a13cf141e2de80ee70b3e84bb3c00c030000011ff01000100000b00040300010200170000";
        System.out.println("Wireshark byte array length: " + byteArray.length()/2);
    }
}
