package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class RaiField extends BaseField {
  private static final String NAME = "RAI";
  
  private int mnc = 0;
  
  private int mcc = 0;
  
  private int lac = 0;
  
  private int rac = 0;
  
  public String getName() {
    return "RAI";
  }
  
  public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
    this.mcc = getMCC(valueBuffer);
    this.mnc = getMNC(valueBuffer);
    this.lac = valueBuffer[4] & 0xFF;
    this.lac <<= 8;
    this.lac |= valueBuffer[5] & 0xFF;
    this.rac = valueBuffer[6] & 0xFF;
    this.rac <<= 8;
    this.rac |= valueBuffer[7] & 0xFF;
    Map<String, Integer> fieldValueMap = new HashMap<>();
    fieldValueMap.put("RAI-MCC", Integer.valueOf(this.mcc));
    fieldValueMap.put("RAI-MNC", Integer.valueOf(this.mnc));
    fieldValueMap.put("RAI-LAC", Integer.valueOf(this.lac));
    fieldValueMap.put("RAI-RAC", Integer.valueOf(this.rac));
    return fieldValueMap;
  }
}
