package com.diameter.commons;

public class APIUtility {
  public static byte[] intToByteArray(int integer) {
    int byteNum = (40 - Integer.numberOfLeadingZeros((integer < 0) ? (integer ^ 0xFFFFFFFF) : integer)) / 8;
    byte[] byteArray = new byte[4];
    for (int n = 0; n < byteNum; n++)
      byteArray[3 - n] = (byte)(integer >>> n * 8); 
    return byteArray;
  }
}
