package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class EcgiField extends BaseField {
  private static final String NAME = "ECGI";
  
  private int mnc = 0;
  
  private int mcc = 0;
  
  private int eci = 0;
  
  private int spare = 0;
  
  public String getName() {
    return "ECGI";
  }
  
  public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
    int index = 0;
    this.mcc = getMCC(valueBuffer);
    this.mnc = getMNC(valueBuffer);
    int tmp = 0;
    this.spare = valueBuffer[index + 4] & 0xF0 & 0xFF;
    this.spare >>= 4;
    this.eci = valueBuffer[index + 4] & 0xFF & 0xF;
    this.eci <<= 24;
    tmp = valueBuffer[index + 5] & 0xFF;
    tmp <<= 16;
    this.eci |= tmp;
    tmp = valueBuffer[index + 6] & 0xFF;
    this.eci |= tmp << 8;
    this.eci |= valueBuffer[index + 7] & 0xFF;
    Map<String, Integer> fieldValueMap = new HashMap<>();
    fieldValueMap.put("ECGI-MCC", Integer.valueOf(this.mcc));
    fieldValueMap.put("ECGI-MNC", Integer.valueOf(this.mnc));
    fieldValueMap.put("ECGI-SPARE", Integer.valueOf(this.spare));
    fieldValueMap.put("ECGI-ECI", Integer.valueOf(this.eci));
    return fieldValueMap;
  }
}
