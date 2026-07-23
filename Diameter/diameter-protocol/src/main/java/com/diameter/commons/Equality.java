package com.diameter.commons;

public class Equality {
  public static boolean areEqual(Object oThis, Object oThat) {
    return (oThis == null) ? ((oThat == null)) : oThis.equals(oThat);
  }
  
  public static boolean areEqual(int iThis, int iThat) {
    return (iThis == iThat);
  }
  
  public static boolean areEqual(long lThis, long lThat) {
    return (lThis == lThat);
  }
  
  public static boolean areEqual(char cThis, char cThat) {
    return (cThis == cThat);
  }
  
  public static boolean areEqual(byte bThis, byte bThat) {
    return (bThis == bThat);
  }
  
  public static boolean areEqual(double dThis, double dThat) {
    return (dThis == dThat);
  }
  
  public static boolean areEqual(float fThis, float fThat) {
    return (fThis == fThat);
  }
}
