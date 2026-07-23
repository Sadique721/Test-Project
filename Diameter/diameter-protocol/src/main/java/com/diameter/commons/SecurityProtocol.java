package com.diameter.commons;

public enum SecurityProtocol {
  NONE(1, "NONE"),
  TLS(2, "TLS"),
  IPSEC(3, "IPSEC");
  
  private static final SecurityProtocol[] SECURITY_PROTOCOLS;
  
  public final int protocol;
  
  public final String protocolName;
  
  static {
    SECURITY_PROTOCOLS = values();
  }
  
  SecurityProtocol(int protocol, String protocolName) {
    this.protocol = protocol;
    this.protocolName = protocolName;
  }
  
  public static SecurityProtocol fromCode(int protocol) {
    for (int i = 0; i < SECURITY_PROTOCOLS.length; i++) {
      if ((SECURITY_PROTOCOLS[i]).protocol == protocol)
        return SECURITY_PROTOCOLS[i]; 
    } 
    return null;
  }
}
