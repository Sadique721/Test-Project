package com.diameter.commons;

public enum RedirectHostAVPFormat {
  DIAMETERURI("DIAMETERURI"),
  HOSTIDENTITY("HOST IDENTITY"),
  IP("IP");
  
  private String name;
  
  RedirectHostAVPFormat(String name) {
    this.name = name;
  }
  
  public static RedirectHostAVPFormat fromRedirectHostAVPFormat(String redirectHostAVPFormatStr) {
    if (DIAMETERURI.name.equalsIgnoreCase(redirectHostAVPFormatStr))
      return DIAMETERURI; 
    if (HOSTIDENTITY.name.equalsIgnoreCase(redirectHostAVPFormatStr))
      return HOSTIDENTITY; 
    if (IP.name.equalsIgnoreCase(redirectHostAVPFormatStr))
      return IP; 
    return null;
  }
  
  public String getStrFormat() {
    return this.name;
  }
}
