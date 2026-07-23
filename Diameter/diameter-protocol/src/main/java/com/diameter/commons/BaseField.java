package com.diameter.commons;

public abstract class BaseField implements TGPPField {
  protected static final int RIGHT_DIGIT_MASK = 15;
  
  protected static final int LEFT_DIGIT_MASK = 240;
  
  private static final int MCC_MNC_IGNORE_VALUE = 15;
  
  protected int getMCC(byte[] valueBuffer, int index) {
    int mcc = 0;
    int multiplier = 1;
    int mccbyte = valueBuffer[index + 2] & 0xFF & 0xF;
    if (mccbyte < 15) {
      mcc = mccbyte;
      multiplier *= 10;
    } 
    mccbyte = (valueBuffer[index + 1] & 0xF0 & 0xFF) >> 4;
    if (mccbyte < 15) {
      mcc += mccbyte * multiplier;
      multiplier *= 10;
    } 
    mccbyte = valueBuffer[index + 1] & 0xF & 0xFF;
    if (mccbyte < 15)
      mcc += mccbyte * multiplier; 
    return mcc;
  }
  
  protected int getMNC(byte[] valueBuffer, int index) {
    int mnc = 0;
    int multiplier = 1;
    int mncByte = (valueBuffer[index + 2] & 0xF0 & 0xFF) >> 4;
    if (mncByte < 15) {
      mnc = mncByte;
      multiplier *= 10;
    } 
    mncByte = (valueBuffer[index + 3] & 0xF0 & 0xFF) >> 4;
    if (mncByte < 15) {
      mnc += mncByte * multiplier;
      multiplier *= 10;
    } 
    mncByte = valueBuffer[index + 3] & 0xF & 0xFF;
    if (mncByte < 15)
      mnc += mncByte * multiplier; 
    return mnc;
  }
  
  protected int getMNC(byte[] valueBuffer) {
    return getMNC(valueBuffer, 0);
  }
  
  protected int getMCC(byte[] valueBuffer) {
    return getMCC(valueBuffer, 0);
  }
}
