package com.diameter.commons;

import java.io.UnsupportedEncodingException;

public class AvpUTF8String extends AvpOctetString {
  public static final String MODULE = "UTF8 String";
  
  public AvpUTF8String(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setStringValue(String data) {
    byte[] valueBuffer = null;
    if (data != null) {
      try {
        valueBuffer = data.getBytes("UTF-8");
      } catch (UnsupportedEncodingException ueExce) {
        LogManager.getLogger().trace("UTF8 String", ueExce);
      } 
      setValueBytes(valueBuffer);
    } 
  }
  
  public String getStringValue() {
    byte[] valueBuffer = null;
    valueBuffer = getValueBytes();
    if (valueBuffer != null)
      try {
        return new String(valueBuffer, "UTF-8");
      } catch (UnsupportedEncodingException ueExce) {
        LogManager.getLogger().trace("UTF8 String", ueExce);
      }  
    return null;
  }
  
  public void doPlus(String value) {
    if (value != null)
      setStringValue(getStringValue() + value); 
  }
}
