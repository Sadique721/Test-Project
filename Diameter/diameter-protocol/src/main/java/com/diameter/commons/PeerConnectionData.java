package com.diameter.commons;


import java.net.InetAddress;

public interface PeerConnectionData {
  int getRemotePort();
  
  int getPeerTimeout();
  
  TransportProtocols getTransportProtocol();
  
  String getLocalIPAddress();
  
  String getRemoteIPAddress();
  
  InetAddress getLocalInetAddress();
  
  InetAddress getRemoteInetAddress();
  
  int getLocalPort();
  
  int getSocketReceiveBufferSize();
  
  int getSocketSendBufferSize();
  
  boolean isNagleAlgoEnabled();
  
  EliteSSLParameter getSSLParameter();
  
  SecurityStandard getSecurityStandard();
  
  void setRemoteInetAddress(InetAddress paramInetAddress);
  
  void setRemoteIPAddress(String paramString);
  
  void setRemotePort(int paramInt);
  
  void setPeerTimeout(int paramInt);
  
  void setTransportProtocol(TransportProtocols paramTransportProtocols);
  
  void setLocalIPAddress(String paramString);
  
  void setLocalPort(int paramInt);
  
  void setSocketReceiveBufferSize(int paramInt);
  
  void setSocketSendBufferSize(int paramInt);
  
  void setTCPNagleAlgo(boolean paramBoolean);
  
  void setSSLParameter(EliteSSLParameter paramEliteSSLParameter);
  
  void setSecurityStandard(SecurityStandard paramSecurityStandard);
  
  void setLocalInetAddress(InetAddress paramInetAddress);
}
