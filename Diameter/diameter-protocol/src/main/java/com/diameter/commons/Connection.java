package com.diameter.commons;

import java.io.IOException;
import java.net.InetAddress;

public interface Connection {
  public static final int DEFAULT_TIMEOUT_IN_MS = 5000;
  
  String getSourceIpAddress();
  
  int getSourcePort();
  
  String getLocalAddress();
  
  boolean isConnected();
  
  void write(Packet paramPacket) throws IOException;
  
  void read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  String getClientAddress();
  
  int getClientPort();
  
  DiameterInputStream getInputStream();
  
  void closeConnection();
  
  boolean isClosed();
  
  boolean isInputShutdown();
  
  boolean isOutputShutdown();
  
  EliteSSLContextExt getEliteSSLContext();
  
  int getLocalPort();
  
  SecurityProtocol getSecurityProtocol();
  
  InetAddress getSourceInetAddress();
}