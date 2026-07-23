package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public enum InbandSecurityId {
  NO_INBAND_SECURITY(0),
  TLS(1),
  IPSec(2);
  
  protected static final InbandSecurityId[] INBAND_SECURITY_IDS;
  
  private static final List<InbandSecurityId> list;
  
  public final int code;
  
  static {
    INBAND_SECURITY_IDS = values();
    list = new ArrayList<>();
    for (InbandSecurityId type : INBAND_SECURITY_IDS)
      list.add(type); 
  }
  
  InbandSecurityId(int code) {
    this.code = code;
  }
  
  public int getCode() {
    return this.code;
  }
  
  public static boolean isValid(InbandSecurityId value) {
    return list.contains(value);
  }
  
  public static InbandSecurityId fromCode(int value) {
    return list.get(value);
  }
  
  public static List<InbandSecurityId> keys() {
    return list;
  }
}
