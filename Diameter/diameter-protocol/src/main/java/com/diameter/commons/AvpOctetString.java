package com.diameter.commons;

import java.io.UnsupportedEncodingException;

public class AvpOctetString extends BaseDiameterAVP {
  public AvpOctetString(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setStringValue(String data) {
    if (data != null)
      if (data.startsWith("0x") || data.startsWith("0X")) {
        setValueBytes(DiameterUtility.getBytesFromHexValue(data));
      } else {
        try {
          setValueBytes(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
          setValueBytes(data.getBytes());
        } 
      }  
  }
  
  public String getStringValue() {
    return "0x" + DiameterUtility.bytesToHex(getValueBytes());
  }
  
  public void doPlus(String value) {
    if (value != null) {
      String hexValue = DiameterUtility.bytesToHex(value.getBytes());
      setValueBytes(DiameterUtility.appendBytes(getStringValue().getBytes(), hexValue.getBytes()));
    } 
  }
}
