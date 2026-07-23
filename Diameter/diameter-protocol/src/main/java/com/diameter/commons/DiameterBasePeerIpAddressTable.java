package com.diameter.commons;

public class DiameterBasePeerIpAddressTable {
  private int dbpPeerIpAddressIndex;
  
  private IpAddressTypes dbpPeerIpAddressType;
  
  private String dbpPeerIpAddress;
  
  public DiameterBasePeerIpAddressTable(int dbpPeerIpAddressIndex, IpAddressTypes dbpPeerIpAddressType, String dbpPeerIpAddress) {
    this.dbpPeerIpAddressIndex = dbpPeerIpAddressIndex;
    this.dbpPeerIpAddressType = dbpPeerIpAddressType;
    this.dbpPeerIpAddress = dbpPeerIpAddress;
  }
  
  public int getPeerIpAddressIndex() {
    return this.dbpPeerIpAddressIndex;
  }
  
  public int getdbpPeerIpAddressType() {
    return this.dbpPeerIpAddressType.code;
  }
  
  public String getPeerIpAddress() {
    return this.dbpPeerIpAddress;
  }
}
