package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class TaiAndEcgiField extends BaseField {
  private static final String NAME = "TAI_ECGI";
  
  private int taiMnc = 0;
  
  private int taiMcc = 0;
  
  private int taiTac = 0;
  
  private int ecgiMnc = 0;
  
  private int ecgiMcc = 0;
  
  private int ecgiSpare = 0;
  
  private int ecgiEci = 0;
  
  public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
    this.taiMcc = getMCC(valueBuffer);
    this.taiMnc = getMNC(valueBuffer);
    this.taiTac = valueBuffer[4] & 0xFF;
    this.taiTac <<= 8;
    this.taiTac |= valueBuffer[5] & 0xFF;
    this.ecgiMcc = getMCC(valueBuffer, 5);
    this.ecgiMnc = getMNC(valueBuffer, 5);
    this.ecgiSpare = valueBuffer[9] & 0xF & 0xFF;
    this.ecgiSpare >>= 4;
    int tmp = 0;
    this.ecgiEci = valueBuffer[9] & 0xF & 0xFF;
    this.ecgiEci <<= 24;
    tmp = valueBuffer[10] & 0xFF;
    tmp <<= 16;
    this.ecgiEci |= tmp;
    tmp = valueBuffer[11] & 0xFF;
    this.ecgiEci |= tmp << 8;
    this.ecgiEci |= valueBuffer[12] & 0xFF;
    Map<String, Integer> fieldValueMap = new HashMap<>();
    fieldValueMap.put("TAC-MCC", Integer.valueOf(this.taiMcc));
    fieldValueMap.put("TAC-MNC", Integer.valueOf(this.taiMnc));
    fieldValueMap.put("TAC-TAC", Integer.valueOf(this.taiTac));
    fieldValueMap.put("ECGI-MCC", Integer.valueOf(this.ecgiMcc));
    fieldValueMap.put("ECGI-MNC", Integer.valueOf(this.ecgiMnc));
    fieldValueMap.put("ECGI-SPARE", Integer.valueOf(this.ecgiSpare));
    fieldValueMap.put("ECGI-ECI", Integer.valueOf(this.ecgiEci));
    return fieldValueMap;
  }
  
  public String getName() {
    return "TAI_ECGI";
  }
}
