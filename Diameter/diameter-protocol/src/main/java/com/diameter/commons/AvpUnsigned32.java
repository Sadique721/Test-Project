package com.diameter.commons;

import java.nio.ByteBuffer;
import java.util.Map;

public class AvpUnsigned32 extends BaseDiameterAVP {
  private Map<Integer, String> supportedValues;
  
  public AvpUnsigned32(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption, Map<Integer, String> supportedValues) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
    this.supportedValues = supportedValues;
  }
  
  public void setInteger(long lData) {
    if (lData < 0L)
      throw new NegativeValueException("Negative Value Found in " + getClass().getName()); 
    if (lData > 4294967295L)
      throw new MaxValueException("Value exceeds maximum limit in " + getClass().getName()); 
    ByteBuffer bLongData = ByteBuffer.allocate(8);
    bLongData.putLong(lData);
    ByteBuffer bData = ByteBuffer.allocate(4);
    bLongData.position(4);
    for (int i = 0; i < 4; i++)
      bData.put(bLongData.get()); 
    byte[] valueBuffer = bData.array();
    setValueBytes(valueBuffer);
  }
  
  public long getInteger() {
    ByteBuffer bData = ByteBuffer.allocate(8);
    byte[] valueBuffer = getValueBytes();
    bData.position(4);
    if (valueBuffer.length > 4) {
      bData.put(valueBuffer, 0, 4);
    } else {
      bData.put(valueBuffer);
    } 
    long data = bData.getLong(0);
    return data;
  }
  
  public String getStringValue() {
    return getStringValue(false);
  }
  
  public String getStringValue(boolean bUseDictionaryValue) {
    if (bUseDictionaryValue) {
      String strVlue = this.supportedValues.get(new Integer((int)getInteger()));
      if (strVlue != null && strVlue.length() > 0)
        return strVlue; 
    } 
    return String.valueOf(getInteger());
  }
  
  public final String getLogValue() {
    String strVlue = this.supportedValues.get(new Integer((int)getInteger()));
    if (strVlue != null && strVlue.length() > 0)
      return strVlue + " (" + getInteger() + ")"; 
    return getStringValue();
  }
  
  public void setStringValue(String data) {
    if (data == null) {
      setInteger(0L);
    } else {
      long longValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), data);
      if (longValue >= 0L) {
        setInteger(longValue);
      } else {
        try {
          setInteger(Long.parseLong(data));
        } catch (NumberFormatException ex) {
          throw new IllegalArgumentException("Cannot convert " + data + " to UnsignedIntegerAttribute.", ex);
        } 
      } 
    } 
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
          throw new IllegalArgumentException("Cannot convert " + value + " to AVPUnsigned32.", numberFormatExp);
        } 
      }  
  }
}
