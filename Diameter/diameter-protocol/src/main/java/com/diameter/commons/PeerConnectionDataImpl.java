package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;

public class PeerConnectionDataImpl implements PeerConnectionData {
  private int remotePort = 3868;
  
  private int localPort = 0;
  
  private int timeout = 3000;
  
  private TransportProtocols trasportProtocol = TransportProtocols.TCP;
  
  private String localIP = null;
  
  private String rempteIpAddress = "";
  
  private InetAddress remoteInetAddress;
  
  private InetAddress localInetAddress;
  
  private boolean isTcpNagleAlgoEnabled;
  
  private int socketSendBufferSize = -1;
  
  private int socketReceiveBufferSize = -1;
  
  private EliteSSLParameter eliteSSLParameter;
  
  private SecurityStandard securityStandard = SecurityStandard.NONE;
  
  public void setRemotePort(int remotePort) {
    this.remotePort = remotePort;
  }
  
  public int getRemotePort() {
    return this.remotePort;
  }
  
  public void setPeerTimeout(int timeout) {
    this.timeout = timeout;
  }
  
  public int getPeerTimeout() {
    return this.timeout;
  }
  
  public void setTransportProtocol(TransportProtocols trasportProtocol) {
    this.trasportProtocol = trasportProtocol;
  }
  
  public void setLocalIPAddress(String localIP) {
    if (localIP != null && localIP.trim().length() > 0)
      this.localIP = localIP; 
  }
  
  public TransportProtocols getTransportProtocol() {
    return this.trasportProtocol;
  }
  
  public String getLocalIPAddress() {
    return this.localIP;
  }
  
  public String getRemoteIPAddress() {
    return this.rempteIpAddress;
  }
  
  public InetAddress getLocalInetAddress() {
    return this.localInetAddress;
  }
  
  public InetAddress getRemoteInetAddress() {
    return this.remoteInetAddress;
  }
  
  public void setLocalInetAddress(InetAddress localInetAddress) {
    if (localInetAddress != null && localInetAddress.toString().trim().length() > 0)
      this.localInetAddress = localInetAddress; 
  }
  
  public void setRemoteInetAddress(InetAddress hostInetAddress) {
    if (hostInetAddress != null && hostInetAddress.toString().trim().length() > 0)
      this.remoteInetAddress = hostInetAddress; 
  }
  
  public int getLocalPort() {
    return this.localPort;
  }
  
  public void setLocalPort(int localPort) {
    this.localPort = localPort;
  }
  
  public void setRemoteIPAddress(String remoteIpAddress) {
    this.rempteIpAddress = remoteIpAddress;
  }
  
  public void setSocketSendBufferSize(int size) {
    this.socketSendBufferSize = size;
  }
  
  public void setSocketReceiveBufferSize(int size) {
    this.socketReceiveBufferSize = size;
  }
  
  public void setTCPNagleAlgo(boolean value) {
    this.isTcpNagleAlgoEnabled = value;
  }
  
  public int getSocketReceiveBufferSize() {
    return this.socketReceiveBufferSize;
  }
  
  public int getSocketSendBufferSize() {
    return this.socketSendBufferSize;
  }
  
  public boolean isNagleAlgoEnabled() {
    return this.isTcpNagleAlgoEnabled;
  }
  
  public void setSSLParameter(EliteSSLParameter eliteSSLParameter) {
    this.eliteSSLParameter = eliteSSLParameter;
  }
  
  public SecurityStandard getSecurityStandard() {
    return this.securityStandard;
  }
  
  public void setSecurityStandard(SecurityStandard securityStandard) {
    this.securityStandard = securityStandard;
  }
  
  public String toString() {
    StringWriter stringBuffer = new StringWriter();
    PrintWriter out = new PrintWriter(stringBuffer);
    out.println("\tLocal Address: " + ((this.localInetAddress != null) ? this.localInetAddress : "") + ":" + this.localPort);
    out.println("\tRemote Address: " + ((this.remoteInetAddress != null) ? this.remoteInetAddress : "") + ":" + this.remotePort);
    out.println("\tTimeout: " + this.timeout);
    out.println("\tTransport Protocol: " + this.trasportProtocol);
    out.println("\tSocket Send buffer size: " + this.socketSendBufferSize);
    out.println("\tSocket Receive buffer size: " + this.socketReceiveBufferSize);
    out.println("\tTCP Nagle Algorithm: " + this.isTcpNagleAlgoEnabled);
    out.close();
    return stringBuffer.toString();
  }
  
  public EliteSSLParameter getSSLParameter() {
    return this.eliteSSLParameter;
  }
}
