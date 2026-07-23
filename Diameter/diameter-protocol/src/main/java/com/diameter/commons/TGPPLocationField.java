package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum TGPPLocationField {
  CGI(0),
  SAI(1),
  RAI(2),
  TAI(128),
  ECGI(129),
  TAI_AND_ECGI(130),
  UNKNOWN(-1);
  
  public final int type;
  
  private static Map<Integer, TGPPLocationField> fieldsMap;
  
  private static TGPPLocationField[] values;
  
  static {
    values = values();
    fieldsMap = new HashMap<>();
    for (TGPPLocationField field : values)
      fieldsMap.put(Integer.valueOf(field.type), field); 
  }
  
  TGPPLocationField(int type) {
    this.type = type;
  }
  
  public static TGPPLocationField getField(int type) {
    TGPPLocationField tgppLocationField = fieldsMap.get(Integer.valueOf(type));
    if (tgppLocationField != null)
      return tgppLocationField; 
    return UNKNOWN;
  }
  
  public static String fieldName(int type) {
    TGPPLocationField tgppLocationField = fieldsMap.get(Integer.valueOf(type));
    if (tgppLocationField != null)
      return tgppLocationField.name(); 
    return "UNKNOWN ( " + String.valueOf(type) + " )";
  }
}
