package com.diameter.commons;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class AvpUnsigned64 extends BaseDiameterAVP {
  public AvpUnsigned64(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setInteger(long lData) {
    if (lData < 0L)
      throw new NegativeValueException("Negative Value Found in " + getClass().getName()); 
    ByteBuffer bData = ByteBuffer.allocate(8);
    bData.putLong(lData);
    byte[] valueBuffer = bData.array();
    setValueBytes(valueBuffer);
  }
  
  private void setInteger(BigInteger data) {
    byte[] valueBuffer = data.toByteArray();
    if (valueBuffer[0] != 0)
      throw new NumberFormatException("Value: " + data.toString() + " out of Range."); 
    byte[] temp = new byte[8];
    System.arraycopy(valueBuffer, valueBuffer.length - 8, temp, 0, temp.length);
    setValueBytes(temp);
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
  
  private BigInteger getBigInteger() {
    byte[] temp = new byte[9];
    System.arraycopy(getValueBytes(), 0, temp, 1, 8);
    return new BigInteger(temp);
  }
  
  public String getStringValue() {
    if (Arrayz.isNullOrEmpty((Object[])new byte[][] { getValueBytes() }))
      return null; 
    long value = getInteger();
    if (value < 0L)
      return getBigInteger().toString(); 
    return String.valueOf(value);
  }
  
  public void setStringValue(String data) {
    try {
      setInteger(Long.parseLong(data));
    } catch (NumberFormatException e) {
      setInteger(new BigInteger(data));
    } 
  }
  
  public void doPlus(String value) {
    if (value == null || value.trim().length() == 0)
      return; 
    long avpValue = getInteger();
    if (avpValue < 0L) {
    	BigInteger bigInteger;
      try {
    	  bigInteger = new BigInteger(value);
      } catch (NumberFormatException e) {
        long dictionaryValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), value);
        if (dictionaryValue >= 0L) {
        	bigInteger = BigInteger.valueOf(dictionaryValue);
        } else {
          throw new IllegalArgumentException("Cannot convert " + value + " to AVPUnsigned64.", e);
        } 
      } 
      BigInteger bigValue = getBigInteger().add(bigInteger);
      if (bigValue.compareTo(BigInteger.ZERO) < 0)
        throw new IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range"); 
      setInteger(bigValue);
      return;
    } 
    try {
      long lValue = Long.parseLong(value);
      long result = avpValue + lValue;
      if (result < 0L) {
        BigInteger bigValue = getBigInteger().add(new BigInteger(value));
        if (bigValue.compareTo(BigInteger.ZERO) < 0)
          throw new IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range"); 
        setInteger(bigValue);
      } else {
        setInteger(result);
      } 
    } catch (NumberFormatException e) {
      long lValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), value);
      if (lValue < 0L) {
    	 BigInteger bigInteger;
        try {
        	bigInteger = new BigInteger(value);
        } catch (NumberFormatException numberFormatException) {
          throw new IllegalArgumentException("Cannot convert " + value + " to AVPUnsigned64.", numberFormatException);
        } 
        BigInteger bigValue = getBigInteger().add(bigInteger);
        if (bigValue.compareTo(BigInteger.ZERO) < 0)
          throw new IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range"); 
        setInteger(bigValue);
        return;
      } 
      long result = avpValue + lValue;
      if (result < 0L) {
        BigInteger bigValue = getBigInteger().add(new BigInteger(value));
        if (bigValue.compareTo(BigInteger.ZERO) < 0)
          throw new IllegalArgumentException("Cannot add " + value + ", Reason: Value Off Range"); 
        setInteger(bigValue);
      } else {
        setInteger(result);
      } 
    } 
  }
}
