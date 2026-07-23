package com.diameter.commons;

public interface INetworkConnector {
  boolean start(ConnectionFactory paramConnectionFactory);
  
  void openConnection(IPeerListener paramIPeerListener);
  
  boolean stop();
  
  String getNetworkAddress();
  
  int getNetworkPort();
  
  SocketDetail getBondSocketDetail();
  
  ServiceRemarks getRemarks();
  
  EliteSSLParameter getDefalutSSLParameter();
  
  SecurityStandard geSecurityStandard();
  
  TransportProtocols getTransportProtocol();
}
