package com.savbill.radius.aaa.eap.util;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.RadiusUtil;
import com.savbill.radius.entity.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EAPUtility {

    private static final Logger log = LoggerFactory.getLogger(EAPUtility.class);

    static int HMAC_SHA256_LENGTH = 32;
    static int HMAC_SHARED_SECRET_MAX_LENGTH = 64;

    public static byte[] getMessageAuthenticatorAttribute(RadiusPacket response, RadiusPacket request, Client cltData) {
        byte[] HmacHash = null;
        // Choose the hash algorithm (MD5 in this case)
        String algorithm = "HmacMD5";

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(RadiusUtil.getUtf8Bytes(cltData.getSharedKey()), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            byte[] responseBytes = getResponseBytes(response, request);

            log.debug("Calculated responsebyte for challenge: " + RadiusUtil.getHexString(responseBytes));

            HmacHash = mac.doFinal(responseBytes);
            log.debug(" Hmac of response: " + HmacHash);
            log.debug(" Hex string of response: " + RadiusUtil.getHexString(HmacHash));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return HmacHash;
    }

    public static boolean  validateEAPRequest(AccessRequest request, String sharedKey) throws NoSuchAlgorithmException {
        boolean isValid = false;

        log.debug("Shared secret from client : " + sharedKey);

        //copy request to another object
        //edit message-authenticator object 16 octet zero
        // fetch original message authenticator attributes from original request
        //create Hmac-md5 from updated request byte
        //  compare hmac-md5 hash value with message authenticator attribute data and return result

        System.out.println("Original Packet: " + request.toString());
        RadiusPacket radiusPacket = request.copyPacket();

        System.out.println("copied request packet" + radiusPacket.toString());

        String algorithm = "HmacMD5";
        SecretKeySpec secretKeySpec = new SecretKeySpec(sharedKey.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);

        try {
            mac.init(secretKeySpec);
            byte[] packetByte = createRequestPacketByte(radiusPacket);
            byte[] hashedValue = mac.doFinal(packetByte);
            byte[] messageAuthenticatorAttributeBytes = request.getAttribute(80).getAttributeData();

            isValid = Arrays.equals(hashedValue, messageAuthenticatorAttributeBytes);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return isValid;
    }

    private static byte[] createRequestPacketByte(RadiusPacket radiusPacket) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(radiusPacket.getLength());
        bos.write(radiusPacket.getPacketType() & 0x0ff);
        bos.write(radiusPacket.getPacketIdentifier() & 0xff);
        bos.write(radiusPacket.getLength() >> 8 & 0xff);
        bos.write(radiusPacket.getLength() & 0xff);
        bos.write(radiusPacket.getAuthenticator());

        RadiusAttribute messageAuthenticatorAttribute = radiusPacket.getAttribute(80);
        byte[] attributeData = messageAuthenticatorAttribute.getAttributeData();

        byte[] zeroData = {00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00};
        messageAuthenticatorAttribute.setAttributeData(zeroData);

        bos.write(radiusPacket.getAttributeBytes());
        bos.flush();

        byte[] byteArray = bos.toByteArray();
        // String hexString = RadiusUtil.getHexString(byteArray);
        //   DatatypeConverter.parseHexBinary(hexString.substring(2));
        return byteArray;
    }

    private static byte[] getResponseBytes(RadiusPacket response, RadiusPacket request) {
        byte[] byteArray = null;
        try {
            byte[] attributeBytes = response.getAttributeBytes();
            int packetLength = 20 + attributeBytes.length;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(packetLength);
            bos.write(response.getPacketType() & 0x0ff);
            bos.write(response.getPacketIdentifier() & 0xff);
            bos.write(packetLength >> 8 & 0xff);
            bos.write(packetLength & 0xff);
            bos.write(request.getAuthenticator());
            bos.write(attributeBytes);
            bos.flush();
            byteArray = bos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  byteArray;
    }

    public static byte[] P_HASH(byte[] secret, byte[] masterSecretLabel, byte[] client_server_random, int length, String PRFAlgorithm){
        byte[] result = new byte[length];
        byte[] seed = new byte[client_server_random.length + masterSecretLabel.length];
        System.arraycopy(masterSecretLabel, 0, seed, 0, masterSecretLabel.length);
        System.arraycopy(client_server_random, 0, seed, masterSecretLabel.length, client_server_random.length);
        byte[] A = seed;
        byte[] tmpSeed;
        int iResultPointer = 0;
        while((length - HMAC_SHA256_LENGTH) > 0){
            A = HMAC(PRFAlgorithm, A, secret);
            tmpSeed = HMAC(PRFAlgorithm, appendBytes(A, seed), secret);
            System.arraycopy(tmpSeed, 0, result, iResultPointer, tmpSeed.length);
            iResultPointer += HMAC_SHA256_LENGTH;
            length -= HMAC_SHA256_LENGTH;
        }
        A = HMAC(PRFAlgorithm, A, secret);
        tmpSeed = HMAC(PRFAlgorithm, appendBytes(A, seed), secret);
        tmpSeed = HMAC(PRFAlgorithm, appendBytes(A, seed), secret);
        System.arraycopy(tmpSeed, 0, result, iResultPointer, length);
        return result;
    }

    public static  byte[] HMAC(String hashFunction, byte[] dataToBeEncrypted,byte[] sharedSecret) {

        byte[] resultBytes = null;

//		if (hashFunction.equals("MD5")) {
//			resultBytes = new byte[HMAC_MD5_LENGTH]; // The final output result will be stored in this byte array
//		} else if (hashFunction.equals("SHA-1")) {
//			resultBytes = new byte[HMAC_SHA_LENGTH]; // The final output result will be stored in this byte array
//		}

        byte sharedSecretBytes[] = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // the shared secret bytes will be stored here

        byte[] ipad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];
        byte[] opad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];

        byte[] ipadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing ipad bytes with sharedSecret bytes
        byte[] opadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing opad bytes with sharedSecret bytes

        try {

            MessageDigest digest = (MessageDigest) MessageDigest.getInstance(hashFunction).clone();

            Arrays.fill(ipad, (byte) 0x36); // fills the inner pad byte array with 0x36 ( 54 in Decimal )
            Arrays.fill(opad, (byte) 0x5c); // fills the outer pad byte array with 0x5c ( 92 in Decimal )

            if (sharedSecret.length < HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is less than 64
                System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0,
                        sharedSecret.length); // copies the bytes from shared secret in sharedSecretBytes
                Arrays.fill(sharedSecretBytes, sharedSecret.length,
                        sharedSecretBytes.length - 1, (byte) 0x00); // pads the rest of the bytes with 0x00 ( 0 in Decimal )

			/*} else if (sharedSecret.length > HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is more than 64
				md5MessageDigest.update(sharedSecret);// appply MD5 on the the sharedSecret to convert it into 16 bytes sharedSecret
				sharedSecretBytes = md5MessageDigest.digest();// store the result bytes in sharedSecretBytes
				// pads the rest of the bytes with 0x00
				/*Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);*/

                //printBytes("SharedSecret Bytes",sharedSecretBytes);*/

            } else if (sharedSecret.length > HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is more than 64
                digest.update(sharedSecret);// appply MD5 on the the sharedSecret to convert it into 16 bytes sharedSecret
                sharedSecret = digest.digest();// store the result bytes in sharedSecretBytes
                // pads the rest of the bytes with 0x00
				/*Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);*/
                System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0, sharedSecret.length);
                //printBytes("SharedSecret Bytes",sharedSecretBytes);
                Arrays.fill(sharedSecretBytes, sharedSecret.length,
                        sharedSecretBytes.length - 1, (byte) 0x00);
            } else {
                System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0,
                        sharedSecret.length);
            }

            for (int i = 0; i < sharedSecretBytes.length; i++) {
                ipadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (ipad[i] & 0xFF)); // XORing the sharedSecretBytes with the ipad bytes
                opadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (opad[i] & 0xFF)); // XORing the sharedSecretBytes with the opad bytes
            }

            digest.update(ipadXORSharedSecret);
            digest.update(dataToBeEncrypted);
            byte[] tempResult = digest.digest(); // generating the encrypted value of the bytes set
            digest.update(opadXORSharedSecret);
            digest.update(tempResult);
            resultBytes = digest.digest();

        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {

        } catch (CloneNotSupportedException cns) {

        }
        return resultBytes;
    }

    /***
     * appends bytes of newArrayBytes to the oldArrayBytes.
     * if the oldArrayBytes is null, then it will assign newArrayBytes to the OldArrayBytes
     * if the newArrayBytes is null, then it will do nothing, simply return OldArrayBytes
     * @param oldArrayBytes
     * @param newArrayBytes
     * @return
     */
    public static byte[] appendBytes(byte[] oldArrayBytes,byte[] newArrayBytes)
    {
        if(oldArrayBytes == null)
            return(newArrayBytes);

        byte[] tempArrayBytes = oldArrayBytes;
        if(newArrayBytes != null)
        {
            tempArrayBytes = new byte[oldArrayBytes.length + newArrayBytes.length];
            System.arraycopy(oldArrayBytes,0,tempArrayBytes,0,oldArrayBytes.length);
            System.arraycopy(newArrayBytes,0,tempArrayBytes,oldArrayBytes.length,newArrayBytes.length);
        }
        return(tempArrayBytes);
    }

}


