package com.diameter.commons;


import java.io.UnsupportedEncodingException;

public class AvpDiameterIdentity extends AvpOctetString {
  private static final String MODULE = "AVP DIAMETER IDENTITY";
  
  public AvpDiameterIdentity(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setStringValue(String data) {
    if (data == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("AVP DIAMETER IDENTITY", "NULL Diameter Identity"); 
      return;
    } 
    super.setStringValue(data);
  }
  
  public String getStringValue() throws InvalidDiameterIdentityException {
    String identity;
    byte[] valueBuffer = null;
    valueBuffer = getValueBytes();
    try {
      identity = new String(valueBuffer, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LogManager.getLogger().trace("AVP DIAMETER IDENTITY", e);
      identity = new String(valueBuffer);
    } 
    return identity;
  }
  
  public void doPlus(String value) {
    if (value != null)
      setStringValue(getStringValue() + value); 
  }
}
