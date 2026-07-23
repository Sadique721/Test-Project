package com.savbill.radius.aaa.eap;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.util.RadiusException;
import com.savbill.radius.aaa.util.RadiusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class EAPAttribute extends RadiusAttribute {

    private static final Logger log = LoggerFactory.getLogger(EAPAttribute.class);

    public EAPAttribute() {

    }
    private EAPPacket responsePacket;

    public EAPAttribute(int type) {
        setAttributeType(type);
    }

    public void readAttribute(byte[] data, int offset, int length)
            throws RadiusException {
        if (length < 2)
            throw new RadiusException("attribute length too small: " + length);
        int attrType = data[offset] & 0x0ff;
        int attrLen = data[offset + 1] & 0x0ff;
        byte[] attrData = new byte[attrLen - 2];
        System.arraycopy(data, offset + 2, attrData, 0, attrLen - 2);
        setAttributeType(attrType);
        setAttributeData(attrData);
        setAttributeLength(attrLen);
        // readEAPPacket(data);
    }

    public void readEAPPacket(byte[] eapMessage)  {

        log.debug("Received EAP packet is: " + eapMessage);

        int pos = 0;
        int eapCodeType = eapMessage[pos] & 0x0ff;
        int eapIdentifier = eapMessage[++pos] & 0x0ff;
        int eapLength = (eapMessage[++pos] & 0x0ff) << 8 | (eapMessage[++pos] & 0x0ff);
        int type = eapMessage[++pos] & 0x0ff;

        //  if (type == 13) {

        // } else if (type == 1) {

        byte[] attrData = new byte[eapMessage.length - 5];

        System.arraycopy(eapMessage, ++pos, attrData, 0, eapMessage.length - 5);

        String data = null;
        try {
            data = new String(attrData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.responsePacket = new EAPPacket(eapCodeType,eapIdentifier,eapLength,type,data,0);
        //}

    }

    public byte[] writeAttribute() {

        byte[] attrData = getAttributeData();

        int len = 0;

        if (getAttributeData() != null) {
           len = getAttributeData().length;
        }
        // write vendor ID
        ByteArrayOutputStream bos = new ByteArrayOutputStream(255);
            if (responsePacket != null ) {
                bos.write(responsePacket.getEapCodeType() & 0x0ff);
                bos.write(responsePacket.getEapIdentifier() & 0x0ff);
                bos.write(responsePacket.getEapLength() >> 8 & 0x0ff);
                bos.write(responsePacket.getEapLength() & 0x0ff);

                if (!Integer.valueOf(3).equals(responsePacket.getEapCodeType())) {
                    bos.write(responsePacket.getType() & 0x0ff);
                    bos.write(responsePacket.getFlag() & 0x0ff);

                     int lengthFlag = responsePacket.getFlag() & 0x80;
                    boolean lengthIncluded = lengthFlag > 1;

                    if (lengthIncluded) {
                        bos.write(responsePacket.getEapTlsLength() >> 24 & 0x0ff);
                        bos.write(responsePacket.getEapTlsLength() >> 16 & 0x0ff);
                        bos.write(responsePacket.getEapTlsLength() >> 8 & 0x0ff);
                        bos.write(responsePacket.getEapTlsLength() & 0x0ff);
                    }

                    if (responsePacket.getEapData() != null && responsePacket.getEapData().length > 0) {
                        try {
                            bos.write(responsePacket.getEapData());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                byte[] byteArray = bos.toByteArray();

                log.debug("Hex string of EAP response: " + RadiusUtil.getHexString(byteArray));

                // check data length
                attrData = bos.toByteArray();
                len = attrData.length;
                if (len > 253)
                    throw new RuntimeException("EAP attribute too long: "
                            + bos.size());
            }

        // compose attribute
        byte[] attr = new byte[len + 2];
        attr[0] = AccessRequest.EAP; // code
        attr[1] = (byte) (len + 2); // length
        System.arraycopy(attrData, 0, attr, 2, len);
        log.debug(" EAP data: " + RadiusUtil.getHexString(attr));
        return attr;
    }

    @Override
    public String toString() {

        return  super.toString() + "\nEAPAttribute{" +
                "responosePacket=" + responsePacket +
                '}';
    }

    public void setAttributeValue(EAPPacket eapResponsePacket) {
        this.responsePacket = eapResponsePacket;
    }

    public EAPPacket getResponsePacket() {
        return responsePacket;
    }
}
