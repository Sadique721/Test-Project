package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum LoadBalancerType {
  ROUND_ROBIN("ROUND_ROBIN"),
  FAIL_OVER("FAIL_OVER"),
  SWITCH_OVER("SWITCH_OVER");
  
  public final String type;
  
  protected static final LoadBalancerType[] types;
  
  private static final Map<String, LoadBalancerType> map;
  
  static {
    types = values();
    map = new HashMap<>();
    for (LoadBalancerType type : types)
      map.put(type.type, type); 
  }
  
  LoadBalancerType(String type) {
    this.type = type;
  }
  
  public String getTypeID() {
    return this.type;
  }
  
  public static LoadBalancerType fromTypeID(String type) {
    return map.get(type);
  }
}
