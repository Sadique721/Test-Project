package com.diameter.commons;

import java.util.Map;

public class AvpEnumerated extends AvpInteger32 {
  private Map<Integer, String> supportedValues;
  
  public AvpEnumerated(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption, Map<Integer, String> supportedValues) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
    this.supportedValues = supportedValues;
  }
  
  public String getStringValue() {
    return getStringValue(false);
  }
  
  public final String getLogValue() {
    String strVlue = this.supportedValues.get(new Integer((int)getInteger()));
    if (strVlue != null && strVlue.length() > 0)
      return strVlue + " (" + getInteger() + ")"; 
    return getStringValue();
  }
  
  public String getStringValue(boolean bUseDictionaryValue) {
    if (bUseDictionaryValue) {
      String strVlue = this.supportedValues.get(new Integer((int)getInteger()));
      if (strVlue != null && strVlue.length() > 0)
        return strVlue; 
    } 
    return String.valueOf(getInteger());
  }
}
