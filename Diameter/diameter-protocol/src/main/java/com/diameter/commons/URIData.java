package com.diameter.commons;

public class URIData {
  private String strScheme;
  
  private String strHost;
  
  private int iPort = -1;
  
  private String strTransport = "sctp";
  
  private String strProtocol = "diameter";
  
  public String getScheme() {
    return this.strScheme;
  }
  
  public void setScheme(String strScheme) {
    this.strScheme = strScheme;
  }
  
  public String getHost() {
    return this.strHost;
  }
  
  public void setHost(String strHost) {
    this.strHost = strHost;
  }
  
  public int getPort() {
    return this.iPort;
  }
  
  public void setPort(int iPort) {
    this.iPort = iPort;
  }
  
  public String getTransport() {
    return this.strTransport;
  }
  
  public void setTransport(String strTransport) {
    this.strTransport = strTransport;
  }
  
  public String getProtocol() {
    return this.strProtocol;
  }
  
  public void setProtocol(String strProtocol) {
    this.strProtocol = strProtocol;
  }
}
