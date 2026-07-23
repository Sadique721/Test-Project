package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class TaiField extends BaseField {
  private static final String NAME = "TAI";
  
  private int mnc = 0;
  
  private int mcc = 0;
  
  private int tac = 0;
  
  public String getName() {
    return "TAI";
  }
  
  public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
    this.mcc = getMCC(valueBuffer);
    this.mnc = getMNC(valueBuffer);
    this.tac = valueBuffer[4] & 0xFF;
    this.tac <<= 8;
    this.tac |= valueBuffer[5] & 0xFF;
    Map<String, Integer> fieldValueMap = new HashMap<>();
    fieldValueMap.put("TAC-MCC", Integer.valueOf(this.mcc));
    fieldValueMap.put("TAC-MNC", Integer.valueOf(this.mnc));
    fieldValueMap.put("TAC-TAC", Integer.valueOf(this.tac));
    return fieldValueMap;
  }
}