package com.diameter.commons;

import java.io.IOException;
import java.net.InetAddress;

public interface NetworkConnectionHandler {
  void send(Packet paramPacket) throws IOException;
  
  boolean isConnected();
  
  void addNetworkConnectionEventListener(NetworkConnectionEventListener paramNetworkConnectionEventListener);
  
  void closeConnection(ConnectionEvents paramConnectionEvents);
  
  boolean isResponder();
  
  String getSourceIpAddress();
  
  int getSourcePort();
  
  String getHostName();
  
  void setHostName(String paramString);
  
  String getLocalAddress();
  
  void secureConnection(PeerConnectionData paramPeerConnectionData, EliteSSLContextExt paramEliteSSLContextExt) throws HandShakeFailException;
  
  int getLocalPort();
  
  SecurityProtocol getSecurityProtocol();
  
  void terminateConnection();
  
  InetAddress getSourceInetAddress();
}
