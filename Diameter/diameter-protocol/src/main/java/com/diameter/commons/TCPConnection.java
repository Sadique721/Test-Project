package com.diameter.commons;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPConnection implements Connection {
  private static final String MODULE = "TCP-CONN";
  
  private static final int SO_LINGER_SECONDS = 1;
  
  private static final boolean SO_LINGER_ON = true;
  
  private static final short DEFAULT_BUFFER_SIZE = -1;
  
  private Socket clientSocket;
  
  private DiameterInputStream inStream;
  
  private OutputStream outStream;
  
  private String clientAddress;
  
  private int clientPort;
  
  private AtomicBoolean connected = new AtomicBoolean();
  
  private PeerConnectionData peerData;
  
  private ConnectorContext context;
  
  public TCPConnection(Socket clientSocket, ConnectorContext context, PeerConnectionData peerData) {
    this.clientSocket = clientSocket;
    this.context = context;
    this.clientAddress = clientSocket.getInetAddress().getHostAddress();
    this.clientPort = clientSocket.getPort();
    this.peerData = peerData;
  }
  
  public void init() {
    try {
      int sockRecBufSize, sockSendBufSize;
      boolean nagleAlgo;
      this.inStream = new DiameterInputStream(this.clientSocket.getInputStream());
      this.outStream = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
      if (this.peerData != null) {
        sockRecBufSize = this.peerData.getSocketReceiveBufferSize();
        sockSendBufSize = this.peerData.getSocketSendBufferSize();
        nagleAlgo = this.peerData.isNagleAlgoEnabled();
      } else {
        sockRecBufSize = this.context.getSocketReceiveBufferSize();
        sockSendBufSize = this.context.getSocketSendBufferSize();
        nagleAlgo = false;
      } 
      try {
        this.clientSocket.setSoTimeout(2000);
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("TCP-CONN", "Socket So Timeout set for: " + this.clientAddress + ": " + this.clientPort + " is: 2000 ms"); 
      } catch (IllegalArgumentException e) {
        LogManager.getLogger().warn("TCP-CONN", "Invalid Socket So Timeout size: 2000ms configured for " + this.clientAddress + ": " + this.clientPort + ". Continuing with default time: " + (
            
            (this.clientSocket.getSoTimeout() == 0) ? "DISABLED" : (this.clientSocket.getSoTimeout() + "ms")));
      } 
      try {
        if (sockSendBufSize == -1) {
          if (LogManager.getLogger().isInfoLogLevel())
            LogManager.getLogger().info("TCP-CONN", "Configured send buffer size: " + sockSendBufSize + " for " + this.clientAddress + ":" + this.clientPort + ", so using OS default send buffer size: " + this.clientSocket
                .getSendBufferSize()); 
        } else {
          this.clientSocket.setSendBufferSize(sockSendBufSize);
          if (LogManager.getLogger().isInfoLogLevel())
            LogManager.getLogger().info("TCP-CONN", "Socket send buffer size for " + this.clientAddress + ":" + this.clientPort + " is " + this.clientSocket.getSendBufferSize()); 
        } 
      } catch (IllegalArgumentException e) {
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("TCP-CONN", "Invalid send buffer size: " + sockSendBufSize + " configured for " + this.clientAddress + ":" + this.clientPort + ", so using OS default send buffer size: " + this.clientSocket
              .getSendBufferSize()); 
      } 
      try {
        if (sockRecBufSize == -1) {
          if (LogManager.getLogger().isInfoLogLevel())
            LogManager.getLogger().info("TCP-CONN", "Configured receive buffer size: " + sockRecBufSize + " for " + this.clientAddress + ":" + this.clientPort + ", so using OS default receive buffer size: " + this.clientSocket
                .getReceiveBufferSize()); 
        } else {
          this.clientSocket.setReceiveBufferSize(sockRecBufSize);
          if (LogManager.getLogger().isInfoLogLevel())
            LogManager.getLogger().info("TCP-CONN", "Socket receive buffer size for " + this.clientAddress + ":" + this.clientPort + " is " + this.clientSocket.getReceiveBufferSize()); 
        } 
      } catch (IllegalArgumentException e) {
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("TCP-CONN", "Invalid receive buffer size: " + sockRecBufSize + " configured for " + this.clientAddress + ":" + this.clientPort + ", so using OS default send buffer size: " + this.clientSocket
              .getReceiveBufferSize()); 
      } 
      this.clientSocket.setTcpNoDelay(!nagleAlgo);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TCP-CONN", "TCP No-Delay set for: " + this.clientAddress + ":" + this.clientPort + " is: " + this.clientSocket.getTcpNoDelay()); 
      this.clientSocket.setSoLinger(true, 1);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TCP-CONN", "TCP So Linger is set for: " + this.clientAddress + ":" + this.clientPort + " is: " + this.clientSocket
            
            .getSoLinger()); 
      this.connected.set(true);
    } catch (Exception e) {
      LogManager.getLogger().trace("TCP-CONN", e);
    } 
  }
  
  public String getSourceIpAddress() {
    return getClientAddress();
  }
  
  public int getSourcePort() {
    return getClientPort();
  }
  
  public String getLocalAddress() {
    return this.clientSocket.getLocalAddress().getHostAddress();
  }
  
  public int hashCode() {
    try {
      int hashCode = 0;
      InetAddress address = this.clientSocket.getInetAddress();
      byte[] addressByte = address.getAddress();
      byte[] portByte = APIUtility.intToByteArray(this.clientPort);
      hashCode = addressByte[2];
      hashCode = hashCode << 8 | addressByte[3] & 0xFF;
      hashCode = hashCode << 8 | portByte[2] & 0xFF;
      hashCode = hashCode << 8 | portByte[3] & 0xFF;
      return hashCode;
    } catch (Exception exception) {
      return super.hashCode();
    } 
  }
  
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass())
      return false; 
    try {
      TCPConnection connectionHandler = (TCPConnection)obj;
      if (this.clientAddress.equalsIgnoreCase(connectionHandler.clientAddress) && 
        this.clientPort == connectionHandler.clientPort)
        return true; 
    } catch (Exception e) {
      LogManager.getLogger().error("TCP-CONN", "Error during comparing TCP Connection. Reason:" + e.getMessage());
      LogManager.getLogger().trace("TCP-CONN", e);
    } 
    return super.equals(obj);
  }
  
  public boolean isConnected() {
    return this.connected.get();
  }
  
  public void write(Packet packet) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream(packet.getLength());
    packet.writeTo(buffer);
    this.outStream.write(buffer.toByteArray());
    this.outStream.flush();
  }
  
  public InetAddress getSourceInetAddress() {
    return this.clientSocket.getInetAddress();
  }
  
  public String getClientAddress() {
    return this.clientAddress;
  }
  
  public int getClientPort() {
    return this.clientPort;
  }
  
  public DiameterInputStream getInputStream() {
    return this.inStream;
  }
  
  public void closeConnection() {
    if (!this.connected.compareAndSet(true, false))
      return; 
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("TCP-CONN", "Closing Connection to " + this.clientAddress + "/" + this.clientPort); 
    try {
      if (this.inStream != null)
        this.inStream.close(); 
    } catch (Exception e) {
      LogManager.getLogger().error("TCP-CONN", "Error while closing input stream for peer: " + this.clientSocket.getInetAddress() + ", reason : " + e);
      LogManager.getLogger().trace("TCP-CONN", e);
    } 
    try {
      if (this.outStream != null)
        this.outStream.close(); 
    } catch (Exception e) {
      LogManager.getLogger().error("TCP-CONN", "Error while closing output stream for peer: " + this.clientSocket.getInetAddress() + ", reason : " + e);
      LogManager.getLogger().trace("TCP-CONN", e);
    } 
    try {
      if (this.clientSocket != null && !this.clientSocket.isClosed())
        this.clientSocket.close(); 
    } catch (Exception e) {
      LogManager.getLogger().trace("TCP-CONN", e);
      LogManager.getLogger().error("TCP-CONN", "Error while closing tcp socket for peer: " + this.clientSocket.getInetAddress() + ", reason : " + e);
    } 
  }
  
  public boolean isClosed() {
    return this.clientSocket.isClosed();
  }
  
  public boolean isInputShutdown() {
    return this.clientSocket.isInputShutdown();
  }
  
  public boolean isOutputShutdown() {
    return this.clientSocket.isOutputShutdown();
  }
  
  Socket getSocket() {
    return this.clientSocket;
  }
  
  public void read(byte[] data, int off, int len) {}
  
  public EliteSSLContextExt getEliteSSLContext() {
    return null;
  }
  
  public int getLocalPort() {
    return this.clientSocket.getLocalPort();
  }
  
  public SecurityProtocol getSecurityProtocol() {
    return SecurityProtocol.NONE;
  }
}
