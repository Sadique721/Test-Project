package com.diameter.commons;

public class SocketDetail {
  private final String ipAddress;
  
  private final int port;
  
  public SocketDetail(String ipAddress, int port) {
    this.ipAddress = ipAddress;
    this.port = port;
  }
  
  public int hashCode() {
    return this.ipAddress.hashCode() + this.port;
  }
  
  public String getIPAddress() {
    return this.ipAddress;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof SocketDetail))
      return false; 
    SocketDetail that = (SocketDetail)obj;
    return (Equality.areEqual(this.ipAddress, that.ipAddress) && 
      Equality.areEqual(this.port, that.port));
  }
  
  public String toString() {
    if (this.ipAddress.contains(":"))
      return "[" + this.ipAddress + "]:" + this.port; 
    return this.ipAddress + ":" + this.port;
  }
}
