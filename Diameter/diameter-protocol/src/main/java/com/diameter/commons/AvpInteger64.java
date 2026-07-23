package com.diameter.commons;

import java.nio.ByteBuffer;

public class AvpInteger64 extends BaseDiameterAVP {
  public AvpInteger64(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setInteger(long iData) {
    ByteBuffer bData = ByteBuffer.allocate(8);
    bData.putLong(iData);
    byte[] valueBuffer = bData.array();
    setValueBytes(valueBuffer);
  }
  
  public long getInteger() {
    ByteBuffer bData = ByteBuffer.allocate(8);
    byte[] valueBuffer = getValueBytes();
    if (valueBuffer.length > 8) {
      bData.put(valueBuffer, 0, 8);
    } else {
      bData.put(valueBuffer);
    } 
    long data = bData.getLong(0);
    return data;
  }
  
  public String getStringValue() {
    return String.valueOf(getInteger());
  }
  
  public void setStringValue(String data) {
    setInteger(Long.parseLong(data));
  }
  
  public void doPlus(String value) {
    if (value != null)
      try {
        setInteger(Long.parseLong(value) + getInteger());
      } catch (NumberFormatException numberFormatExp) {
        long longValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), value);
        if (longValue >= 0L) {
          setInteger(getInteger() + longValue);
        } else {
          throw new IllegalArgumentException("Cannot convert " + value + " to IntegerAVP64.", numberFormatExp);
        } 
      }  
  }
}
