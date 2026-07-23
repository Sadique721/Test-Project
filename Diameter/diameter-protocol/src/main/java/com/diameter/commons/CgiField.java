package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class CgiField extends BaseField {
  private static final String NAME = "CGI";
  
  private int mnc = 0;
  
  private int mcc = 0;
  
  private int lac = 0;
  
  private int ci = 0;
  
  public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
    this.mcc = getMCC(valueBuffer);
    this.mnc = getMNC(valueBuffer);
    this.lac = valueBuffer[4] & 0xFF;
    this.lac <<= 8;
    this.lac |= valueBuffer[5] & 0xFF;
    this.ci = valueBuffer[6] & 0xFF;
    this.ci <<= 8;
    this.ci |= valueBuffer[7] & 0xFF;
    Map<String, Integer> fieldValueMap = new HashMap<>();
    fieldValueMap.put("CGI-MCC", Integer.valueOf(this.mcc));
    fieldValueMap.put("CGI-MNC", Integer.valueOf(this.mnc));
    fieldValueMap.put("CGI-LAC", Integer.valueOf(this.lac));
    fieldValueMap.put("CGI-CI", Integer.valueOf(this.ci));
    return fieldValueMap;
  }
  
  public String getName() {
    return "CGI";
  }
}
