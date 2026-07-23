package com.diameter.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum TransportProtocols {
  TCP(1, "TCP"),
  SCTP(2, "SCTP");
  
  public final int code;
  
  public final String protocolTypeStr;
  
  private static final Map<Integer, TransportProtocols> map;
  
  protected static final TransportProtocols[] TRANSPORT_PROTOCOLS;
  
  static {
    TRANSPORT_PROTOCOLS = values();
    map = new HashMap<>();
    for (TransportProtocols type : TRANSPORT_PROTOCOLS)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  TransportProtocols(int code, String protocolTypeStr) {
    this.code = code;
    this.protocolTypeStr = protocolTypeStr;
  }
  
  public static TransportProtocols fromProtocolTypeCode(int protocolTypeCode) {
    return map.get(Integer.valueOf(protocolTypeCode));
  }
  
  public static TransportProtocols fromProtocolTypeString(String protocolTypeStr) {
    if (protocolTypeStr != null)
      for (TransportProtocols protocol : TRANSPORT_PROTOCOLS) {
        if (protocol.protocolTypeStr.equalsIgnoreCase(protocolTypeStr))
          return protocol; 
      }  
    return null;
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static String getProtocolTypeString(int protocolTypeCode) {
    TransportProtocols protocolType = map.get(Integer.valueOf(protocolTypeCode));
    if (protocolType != null)
      return protocolType.protocolTypeStr; 
    return "INVALID PROTOCOL TYPE ";
  }
  
  public static ArrayList<String> getprotocolList() {
    ArrayList<String> list = new ArrayList<>();
    for (TransportProtocols protocol : TRANSPORT_PROTOCOLS)
      list.add(protocol.protocolTypeStr); 
    return list;
  }
}
