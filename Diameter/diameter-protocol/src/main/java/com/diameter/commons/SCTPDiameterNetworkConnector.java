package com.diameter.commons;

import com.diameter.stack.DiameterStack;

public class SCTPDiameterNetworkConnector extends SCTPNetworkConnector {
  private int socketReceiveBufferSize = 32767;
  
  private int socketSendBufferSize = 32767;
  
  private String networkAddress = "127.0.0.1";
  
  private int networkPort = 3869;
  
  private EliteSSLParameter sslParameter;
  
  private SecurityStandard securityStandard = SecurityStandard.NONE;
  
  public SCTPDiameterNetworkConnector(DiameterStack diameterStack) {
    super(diameterStack);
  }
  
  public boolean start(ConnectionFactory connectionFactory) {
    return super.start(connectionFactory);
  }
  
  public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
    this.socketReceiveBufferSize = socketReceiveBufferSize;
  }
  
  protected int getSocketReceiveBufferSize() {
    return this.socketReceiveBufferSize;
  }
  
  public void setSocketSendBufferSize(int socketSendBufferSize) {
    this.socketSendBufferSize = socketSendBufferSize;
  }
  
  protected int getSocketSendBufferSize() {
    return this.socketSendBufferSize;
  }
  
  public void setNetworkAddress(String address) {
    this.networkAddress = address;
  }
  
  public String getNetworkAddress() {
    return this.networkAddress;
  }
  
  public void setNetworkPort(int port) {
    this.networkPort = port;
  }
  
  public int getNetworkPort() {
    return this.networkPort;
  }
  
  protected String getThreadIdentifier() {
    return "DIA-STACK-CONN-SCTP";
  }
  
  public boolean stop() {
    return super.stop();
  }
  
  public EliteSSLParameter getDefalutSSLParameter() {
    return this.sslParameter;
  }
  
  public void setDefalutSSLParameter(EliteSSLParameter eliteSSLParameter) {
    this.sslParameter = eliteSSLParameter;
  }
  
  public void setSecurityStandard(SecurityStandard securityStandard) {
    this.securityStandard = securityStandard;
  }
  
  public SecurityStandard geSecurityStandard() {
    return this.securityStandard;
  }
}