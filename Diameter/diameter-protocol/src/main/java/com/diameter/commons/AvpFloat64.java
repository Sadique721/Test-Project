package com.diameter.commons;

import java.nio.ByteBuffer;

public class AvpFloat64 extends BaseDiameterAVP {
  public AvpFloat64(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setFloat(double iData) {
    ByteBuffer bData = ByteBuffer.allocate(8);
    bData.putDouble(iData);
    byte[] valueBuffer = bData.array();
    setValueBytes(valueBuffer);
  }
  
  public double getFloat() {
    ByteBuffer bData = ByteBuffer.allocate(8);
    byte[] valueBuffer = getValueBytes();
    if (valueBuffer.length > 8) {
      bData.put(valueBuffer, 0, 8);
    } else {
      bData.put(valueBuffer);
    } 
    double data = bData.getDouble(0);
    return data;
  }
  
  public String getStringValue() {
    return String.valueOf(getFloat());
  }
  
  public void setStringValue(String data) {
    setFloat(Double.parseDouble(data));
  }
  
  public void doPlus(String value) {
    if (value != null)
      try {
        setFloat(Float.parseFloat(value) + getFloat());
      } catch (NumberFormatException numberFormatExp) {
        float floatValue = (float)DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), value);
        if (floatValue >= 0.0F) {
          setFloat(getFloat() + floatValue);
        } else {
          throw new IllegalArgumentException("Cannot convert " + value + " to FloatAVP64.", numberFormatExp);
        } 
      }  
  }
}
