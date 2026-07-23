package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum ServiceTypes {
  ACCT(1, "Acct"),
  AUTH(2, "Auth"),
  BOTH(3, "Both");
  
  public final int code;
  
  public final String serviceTypeStr;
  
  private static final Map<Integer, ServiceTypes> map;
  
  private static final ServiceTypes[] SERVICE_TYPES;
  
  static {
    SERVICE_TYPES = values();
    map = new HashMap<>();
    for (ServiceTypes type : SERVICE_TYPES)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  ServiceTypes(int code, String serviceTypeStr) {
    this.code = code;
    this.serviceTypeStr = serviceTypeStr;
  }
  
  public static ServiceTypes fromServiceTypeCode(int serviceTypeCode) {
    return map.get(Integer.valueOf(serviceTypeCode));
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static String getServiceTypeString(int serviceTypeCode) {
    ServiceTypes serviceType = map.get(Integer.valueOf(serviceTypeCode));
    if (serviceType != null)
      return serviceType.serviceTypeStr; 
    return "INVALID SERVICE TYPE";
  }
}
