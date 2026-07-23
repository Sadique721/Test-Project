package com.diameter.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AvpTime extends AvpOctetString {
  public AvpTime(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setTime(long lTime) {
    
  }
  
  public void setTime(Date dDate) {
    setTime(dDate.getTime() / 1000L);
  }
  
  public long getTime() {
    return new Date().getTime();
  }
  
  public String getStringValue() {
    return String.valueOf(getTime() / 1000L);
  }
  
  public void setStringValue(String data) {
    setTime(Long.parseLong(data));
  }
  
  public final String getLogValue() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    return sdf.format(new Date(getTime()));
  }
  
  public void doPlus(String value) {
    if (value != null)
      try {
        setTime(getTime() + Long.parseLong(value));
      } catch (NumberFormatException numberFormatException) {} 
  }
}