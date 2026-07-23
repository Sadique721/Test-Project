package com.diameter.commons;

import java.net.InetAddress;

import com.diameter.stack.DiameterStack;

public class VirtualConnectionHandler implements NetworkConnectionHandler {
  private VirtualOutputStream outputStream;
  
  private PeerData peerData;
  
  private VirtualInputStream inputStream;
  
  public VirtualConnectionHandler(DiameterStack stack, PeerData peerData, VirtualOutputStream outputStream) {
    this.outputStream = outputStream;
    this.peerData = peerData;
    this.inputStream = (packet -> stack.handleReceivedMessage(packet, this));
  }
  
  public void send(Packet packet) {
    this.outputStream.send(packet);
  }
  
  public boolean isConnected() {
    return true;
  }
  
  public void addNetworkConnectionEventListener(NetworkConnectionEventListener networkConnectionEventListener) {}
  
  public void closeConnection(ConnectionEvents event) {}
  
  public boolean isResponder() {
    return true;
  }
  
  public String getSourceIpAddress() {
    return this.peerData.getRemoteIPAddress();
  }
  
  public int getSourcePort() {
    return this.peerData.getRemotePort();
  }
  
  public String getHostName() {
    return this.peerData.getHostIdentity();
  }
  
  public void setHostName(String hostName) {}
  
  public String getLocalAddress() {
    if (this.peerData.getLocalIPAddress() == null)
      return "0.0.0.0"; 
    return this.peerData.getLocalIPAddress();
  }
  
  public VirtualInputStream getInputStream() {
    return this.inputStream;
  }
  
  public void secureConnection(PeerConnectionData peerData, EliteSSLContextExt eliteSSLContext) throws HandShakeFailException {
    throw new HandShakeFailException("Secure connection unsuppored in virtual connection");
  }
  
  public int getLocalPort() {
    return this.peerData.getLocalPort();
  }
  
  public SecurityProtocol getSecurityProtocol() {
    return SecurityProtocol.NONE;
  }
  
  public void terminateConnection() {}
  
  public InetAddress getSourceInetAddress() {
    return this.peerData.getRemoteInetAddress();
  }
}
