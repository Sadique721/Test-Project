package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum IpAddressTypes {
  UNKNOWN(0, "unknown"),
  IPV4(1, "ipv4"),
  IPV6(2, "ipv6"),
  IPV4Z(3, "ipv4z"),
  IPV6Z(4, "ipv6z"),
  DNS(16, "dns");
  
  public final int code;
  
  public final String ipAddressTypeStr;
  
  private static final Map<Integer, IpAddressTypes> map;
  
  private static final IpAddressTypes[] IP_ADDRESS_TYPES;
  
  static {
    IP_ADDRESS_TYPES = values();
    map = new HashMap<>();
    for (IpAddressTypes type : IP_ADDRESS_TYPES)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  IpAddressTypes(int code, String ipAddressTypeStr) {
    this.code = code;
    this.ipAddressTypeStr = ipAddressTypeStr;
  }
  
  public static IpAddressTypes fromIpAddressTypeCode(int ipAddressTypeCode) {
    return map.get(Integer.valueOf(ipAddressTypeCode));
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static String getIpAddressTypeString(int ipAddressTypeCode) {
    IpAddressTypes ipAddressType = map.get(Integer.valueOf(ipAddressTypeCode));
    if (ipAddressType != null)
      return ipAddressType.ipAddressTypeStr; 
    return "INVALID IP ADDRESS TYPE";
  }
}
