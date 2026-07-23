package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum SecurityStandard {
  RFC_6733("RFC 6733"),
  RFC_3588_DYNAMIC("RFC 3588 Dynamic"),
  RFC_3588_TLS("RFC 3588 TLS"),
  NONE("NONE");
  
  public final String val;
  
  private static final Map<String, SecurityStandard> map;
  
  static {
    map = new HashMap<>();
    for (SecurityStandard securityStandard : values())
      map.put(securityStandard.val, securityStandard); 
  }
  
  SecurityStandard(String val) {
    this.val = val;
  }
  
  public static SecurityStandard fromSecurityStandardVal(String val) {
    return map.get(val);
  }
}
