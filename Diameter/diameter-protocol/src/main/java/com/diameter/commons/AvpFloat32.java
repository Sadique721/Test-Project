package com.diameter.commons;

import java.nio.ByteBuffer;

public class AvpFloat32 extends BaseDiameterAVP {
  public AvpFloat32(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setFloat(double iData) {
    float fData = (float)iData;
    ByteBuffer bData = ByteBuffer.allocate(4);
    bData.putFloat(fData);
    byte[] valueBuffer = bData.array();
    setValueBytes(valueBuffer);
  }
  
  public double getFloat() {
    ByteBuffer bData = ByteBuffer.allocate(4);
    byte[] valueBuffer = getValueBytes();
    if (valueBuffer.length > 4) {
      bData.put(valueBuffer, 0, 4);
    } else {
      bData.put(valueBuffer);
    } 
    float data = bData.getFloat(0);
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
          throw new IllegalArgumentException("Cannot convert " + value + " to FloatAVP32.", numberFormatExp);
        } 
      }  
  }
}
