package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class SaiField extends BaseField {
  private static final String NAME = "SAI";
  
  private int mnc = 0;
  
  private int mcc = 0;
  
  private int lac = 0;
  
  private int sac = 0;
  
  public String getName() {
    return "SAI";
  }
  
  public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
    this.mcc = getMCC(valueBuffer);
    this.mnc = getMNC(valueBuffer);
    this.lac = valueBuffer[4] & 0xFF;
    this.lac <<= 8;
    this.lac |= valueBuffer[5] & 0xFF;
    this.sac = valueBuffer[6] & 0xFF;
    this.sac <<= 8;
    this.sac |= valueBuffer[7] & 0xFF;
    Map<String, Integer> fieldValueMap = new HashMap<>();
    fieldValueMap.put("SAI-MCC", Integer.valueOf(this.mcc));
    fieldValueMap.put("SAI-MNC", Integer.valueOf(this.mnc));
    fieldValueMap.put("SAI-LAC", Integer.valueOf(this.lac));
    fieldValueMap.put("SAI-SAC", Integer.valueOf(this.sac));
    return fieldValueMap;
  }
}
