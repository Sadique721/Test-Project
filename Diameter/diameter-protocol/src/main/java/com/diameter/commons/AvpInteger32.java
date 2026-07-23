package com.diameter.commons;

import java.nio.ByteBuffer;

public class AvpInteger32 extends BaseDiameterAVP {
  public AvpInteger32(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setInteger(long lData) {
    int iData = (int)lData;
    ByteBuffer bData = ByteBuffer.allocate(4);
    bData.putInt(iData);
    byte[] valueBuffer = bData.array();
    setValueBytes(valueBuffer);
  }
  
  public long getInteger() {
    ByteBuffer bData = ByteBuffer.allocate(4);
    byte[] valueBuffer = getValueBytes();
    if (valueBuffer.length > 4) {
      bData.put(valueBuffer, 0, 4);
    } else {
      bData.put(valueBuffer);
    } 
    int data = bData.getInt(0);
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
          throw new IllegalArgumentException("Cannot convert " + value + " to IntegerAVP32.", numberFormatExp);
        } 
      }  
  }
}
