package com.savbill.radius.aaa.eap.util;

import com.savbill.radius.aaa.eap.data.SecurityKeys;
import com.savbill.radius.aaa.util.RadiusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.savbill.radius.aaa.eap.util.CalculateKeyingMaterial.calculateHMAC;
import static com.savbill.radius.aaa.eap.util.CalculateKeyingMaterial.calculateHMACSHA;

public class CalculateMasterSecret {

    private static final Logger log = LoggerFactory.getLogger(CalculateMasterSecret.class);

    // concat two array
    public static byte[] concatenateByteArrays(byte[] byteArray1, byte[] byteArray2) {
        byte[] concatenatedArray = new byte[byteArray1.length + byteArray2.length];
        System.arraycopy(byteArray1, 0, concatenatedArray, 0, byteArray1.length);
        System.arraycopy(byteArray2, 0, concatenatedArray, byteArray1.length, byteArray2.length);
        return concatenatedArray;
    }

    public static byte[] calculateMasterSecret(byte[] clientRandom, byte[] serverRandom, byte[] preMasterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String masterKey = "master secret";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(masterKey.getBytes(StandardCharsets.UTF_8));
        out.write(clientRandom);
        out.write(serverRandom);

        byte[] seedBytes = out.toByteArray();

        byte[] a0 = seedBytes;

        byte[] a1 = calculateHMAC(a0, preMasterSecret);
        byte[] a2 = calculateHMAC(a1, preMasterSecret);

        byte[] p1 = calculateHMAC(concatenateByteArrays(a1, seedBytes), preMasterSecret);
        byte[] p2 = calculateHMAC(concatenateByteArrays(a2, seedBytes), preMasterSecret);


        byte[] masterSecretBytes = concatenateByteArrays(p1, p2);

        byte[] first48Bytes = Arrays.copyOfRange(masterSecretBytes, 0, 48);

        log.debug("Master secret: " + RadiusUtil.getHexString(first48Bytes));
        log.debug("Master secret length New: " + first48Bytes.length);

        return first48Bytes;
    }

    public static byte[] calculateMSKForTLS128(byte[] clientRandom, byte[] serverRandom, byte[] masterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String masterKey = "client EAP encryption";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(masterKey.getBytes(StandardCharsets.UTF_8));
        out.write(clientRandom);
        out.write(serverRandom);

        byte[] seedBytes = out.toByteArray();

        byte[] a0 = seedBytes;

        byte[] a1 = calculateHMAC(a0, masterSecret);
        byte[] a2 = calculateHMAC(a1, masterSecret);
        byte[] a3 = calculateHMAC(a2, masterSecret);
        byte[] a4 = calculateHMAC(a3, masterSecret);

        byte[] p1 = calculateHMAC(concatenateByteArrays(a1, seedBytes), masterSecret);
        byte[] p2 = calculateHMAC(concatenateByteArrays(a2, seedBytes), masterSecret);
        byte[] p3 = calculateHMAC(concatenateByteArrays(a3, seedBytes), masterSecret);
        byte[] p4 = calculateHMAC(concatenateByteArrays(a4, seedBytes), masterSecret);


        byte[] p1p2 = concatenateByteArrays(p1, p2);
        byte[] p3p4 = concatenateByteArrays(p3, p4);

        byte[] p1p2p3p4 = concatenateByteArrays(p1p2, p3p4);

        log.debug("Client key encryption length: " + p1p2p3p4.length);
        log.debug("Client key encryption data:\n " + RadiusUtil.getHexString(p1p2p3p4));

        return p1p2p3p4;
    }

    public static byte[] calculateMSKForTTLS128(byte[] clientRandom, byte[] serverRandom, byte[] masterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String masterKey = "ttls keying material";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(masterKey.getBytes(StandardCharsets.UTF_8));
        out.write(clientRandom);
        out.write(serverRandom);

        byte[] seedBytes = out.toByteArray();

        byte[] a0 = seedBytes;

        byte[] a1 = calculateHMAC(a0, masterSecret);
        byte[] a2 = calculateHMAC(a1, masterSecret);
        byte[] a3 = calculateHMAC(a2, masterSecret);
        byte[] a4 = calculateHMAC(a3, masterSecret);

        byte[] p1 = calculateHMAC(concatenateByteArrays(a1, seedBytes), masterSecret);
        byte[] p2 = calculateHMAC(concatenateByteArrays(a2, seedBytes), masterSecret);
        byte[] p3 = calculateHMAC(concatenateByteArrays(a3, seedBytes), masterSecret);
        byte[] p4 = calculateHMAC(concatenateByteArrays(a4, seedBytes), masterSecret);


        byte[] p1p2 = concatenateByteArrays(p1, p2);
        byte[] p3p4 = concatenateByteArrays(p3, p4);

        byte[] p1p2p3p4 = concatenateByteArrays(p1p2, p3p4);

        System.out.println("Client key encryption length: " + p1p2p3p4.length);
        System.out.println("Client key encryption data:\n " + RadiusUtil.getHexString(p1p2p3p4));

        return p1p2p3p4;
    }

    public static byte[] calculateIV64(byte[] clientRandom, byte[] serverRandom) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String masterKey = "client EAP encryption";
        byte[] masterSecret = "".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(masterKey.getBytes(StandardCharsets.UTF_8));
        out.write(clientRandom);
        out.write(serverRandom);

        byte[] seedBytes = out.toByteArray();

        byte[] a0 = seedBytes;

        byte[] a1 = calculateHMAC(a0, masterSecret );
        byte[] a2 = calculateHMAC(a1, masterSecret);

        byte[] p1 = calculateHMAC(concatenateByteArrays(a1, seedBytes), masterSecret);
        byte[] p2 = calculateHMAC(concatenateByteArrays(a2, seedBytes), masterSecret);

        byte[] clientKeyEncryptionIV = concatenateByteArrays(p1, p2);

        log.debug("Client key encryption  IV length: " + clientKeyEncryptionIV.length);
        log.debug("Client key encryption  IV Data: \n" + RadiusUtil.getHexString(clientKeyEncryptionIV));

        return clientKeyEncryptionIV;
    }


    /**
     * rfc7627 section 4
     * @param handShakeMessagesHash hash of clientHello to client key exchange
     * @param preMasterSecret
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] calculateExtendedMasterSecret(byte[] handShakeMessagesHash, byte[] preMasterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String masterKey = "extended master secret";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(masterKey.getBytes(StandardCharsets.UTF_8));
        out.write(handShakeMessagesHash);

        byte[] seedBytes = out.toByteArray();

        byte[] a0 = seedBytes;

        byte[] a1 = calculateHMAC(a0, preMasterSecret);
        byte[] a2 = calculateHMAC(a1, preMasterSecret);

        byte[] p1 = calculateHMAC(concatenateByteArrays(a1, seedBytes), preMasterSecret);
        byte[] p2 = calculateHMAC(concatenateByteArrays(a2, seedBytes), preMasterSecret);


        byte[] masterSecretBytes = concatenateByteArrays(p1, p2);

        byte[] first48Bytes = Arrays.copyOfRange(masterSecretBytes, 0, 48);

        log.debug("Extended Master secret: " + RadiusUtil.getHexString(first48Bytes));
        log.debug("Extended Master secret length New: " + first48Bytes.length);

        return first48Bytes;
    }



    public static byte[] calculateMasterSecretSHA(byte[] clientRandom, byte[] serverRandom, byte[] preMasterSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String masterKey = "master secret";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(masterKey.getBytes(StandardCharsets.UTF_8));
        out.write(clientRandom);
        out.write(serverRandom);

        byte[] seedBytes = out.toByteArray();

        byte[] a0 = seedBytes;

        byte[] a1 = calculateHMACSHA(a0, preMasterSecret);
        byte[] a2 = calculateHMACSHA(a1, preMasterSecret);

        byte[] p1 = calculateHMACSHA(concatenateByteArrays(a1, seedBytes), preMasterSecret);
        byte[] p2 = calculateHMACSHA(concatenateByteArrays(a2, seedBytes), preMasterSecret);


        byte[] masterSecretBytes = concatenateByteArrays(p1, p2);

        byte[] first48Bytes = Arrays.copyOfRange(masterSecretBytes, 0, 48);

        log.debug("Master secret: " + RadiusUtil.getHexString(first48Bytes));
        log.debug("Master secret length New: " + first48Bytes.length);

        return first48Bytes;
    }

    public static void main(String[] args) throws Exception {
        byte[] bytes = calculateMSKForTTLS128(RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34"), RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401"), RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055"));

        byte[] encryptedData = RadiusUtil.getBytesFromHexString("adcb89d4f309d3cf3f005859dca22352124128b42fc35aa912d5aba553e8b3063dd89ddf19e6c7c27dce790b3633b965e5f30d9110c4e2dbaa3bcaf97cbab524552bb260587414ad36e5bd10e476a5696d5e0263");

        byte[] first64 = Arrays.copyOfRange(bytes, 0, 64);

        System.out.println("First 64: " + RadiusUtil.getHexString(first64));

        byte[] last64 = Arrays.copyOfRange(bytes, 64, 128);

        System.out.println("Last 64: " + RadiusUtil.getHexString(last64));

        SecurityKeys securityKeys = CalculateKeyingMaterial.securityKeyForAES128(RadiusUtil.getBytesFromHexString("1a3d3123b1c741046d872cc65fc0c76594b9c71cd55d4fdbcfdb040379485d34"), RadiusUtil.getBytesFromHexString("64bdffb4ea83506349557259710ab11bf5b6eab11bdefde2444f574e47524401"), RadiusUtil.getBytesFromHexString("09dc304ff95198f466b01bde66ca79efabf56866cb36f4195d6fbba939297cec15de8e64c3e57aca9912a59d0cf8d055"));

        String dataTobeEncryted = "000000014000000b626f6200000000024000001868656c6c6f0000000000000000000000";

        byte[] dataTobeEncrypted = RadiusUtil.getBytesFromHexString(dataTobeEncryted);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(dataTobeEncrypted);

        int remainder = (dataTobeEncrypted.length + 1) % 16;
        int padding = 16-remainder;

        for (int i=0; i < padding + 1; i++) {
            out.write(padding & 0x0ff);
        }


        System.out.println("Final data to be encrypted: " + RadiusUtil.getHexString(out.toByteArray()) + "length: " + out.toByteArray().length);

        byte[] bytes1 = CalculateKeyingMaterial.encryptAES(out.toByteArray(), securityKeys.getClientWriteKey(), securityKeys.getClientIv());

        byte[] decryptedData = CalculateKeyingMaterial.decryptAES(Arrays.copyOfRange(encryptedData, 0, 64), securityKeys.getClientWriteKey(), securityKeys.getClientIv());

        System.out.println("Decrypted data: " + RadiusUtil.getHexString(decryptedData));

        System.out.println("Decrypted data length: " + decryptedData.length);

        byte[] applicationData = Arrays.copyOfRange(decryptedData, 16, decryptedData.length);

        System.out.println("Application data: " + RadiusUtil.getHexString(applicationData));
        System.out.println("Application data length: " + applicationData.length);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(applicationData);

        int code = ( (inputStream.read() & 0x0ff) << 24 | inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

        int flag = inputStream.read() & 0x0ff;

        int length = (inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

        byte[] value = new byte[length - 8];

        inputStream.read(value);

        int paddingUser = inputStream.read() & 0x0ff;

        byte[] finalValue = Arrays.copyOfRange(value, 0, (value.length-paddingUser));

        System.out.println("User name is: " + RadiusUtil.getStringFromUtf8(finalValue));

        int code1 = ( (inputStream.read() & 0x0ff) << 24 | inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

        int flag1 = inputStream.read() & 0x0ff;

        int length1 = (inputStream.read() & 0x0ff) << 16 | (inputStream.read() & 0x0ff) << 8 | (inputStream.read() & 0x0ff);

        byte[] value1 = new byte[length1-8];

        inputStream.read(value1);

        System.out.println( "password bytes: "+ RadiusUtil.getHexString(value1));

        byte[] bytes2 = removeZeroBytes(value1);

        System.out.println( "password bytes value: " +  RadiusUtil.getStringFromUtf8(bytes2));


    }

    public static byte[] removeZeroBytes(byte[] input) {
        int nonZeroCount = 0;

        // Count non-zero bytes
        for (byte b : input) {
            if (b != 0x00) {
                nonZeroCount++;
            }
        }

        System.out.println("Number of non-zero: " + nonZeroCount);
        // Create a new array with the size of non-zero bytes
        byte[] result = new byte[nonZeroCount];
        int index = 0;

        // Copy non-zero bytes to the new array
        for (byte b : input) {
            if (b != 0x00) {
                result[index++] = b;
            }
        }

        System.out.println("size of result with non-zero bytes: " + result.length);
        return result;
    }

}
