package com.diameter.commons;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLHandshakeException;

import com.diameter.stack.Stack;

public class ConnectionHandler implements Runnable, NetworkConnectionHandler {
  private static final String MODULE = "CONN-HNDLR";
  
  private Connection connection;
  
  private ConnectionRole conenctionRole = ConnectionRole.Responder;
  
  private List<NetworkConnectionEventListener> networkConnectionEventListeners;
  
  private ConnectorContext context;
  
  private ConnectionFactory connectionFactory;
  
  private String hostName;
  
  private final ReentrantLock connectionLock;
  
  private static final int MAX_MALFORMED_LIMIT = 10;
  
  private int malformedRequestCount = 0;
  
  public ConnectionHandler(Connection connection, ConnectionFactory connectionFactory, ConnectionRole connectionRole, ConnectorContext context) throws Exception {
    this.conenctionRole = connectionRole;
    this.context = context;
    this.connectionFactory = connectionFactory;
    this.connection = connection;
    this.networkConnectionEventListeners = new ArrayList<>();
    this.hostName = "";
    this.connectionLock = new ReentrantLock();
  }
  
  public void run() {
    ConnectionEvents event = ConnectionEvents.DISCONNECTED;
    try {
      doConnectionNegotiation();
      boolean connectionCloseEventGenerated = false;
      while (true) {
        try {
          DiameterPacket packet = this.connection.getInputStream().readDiameterPacket();
          this.malformedRequestCount = 0;
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("CONN-HNDLR", "Packet obtained from stream, assigning task to process packet"); 
          this.context.executeInAsync((Packet)packet, this);
        } catch (MalformedPacketException e) {
          this.malformedRequestCount++;
          if (!connectionCloseEventGenerated || this.malformedRequestCount % 100 == 0)
            LogManager.getLogger().error("CONN-HNDLR", "Consecutive: " + this.malformedRequestCount + " Malformed Diameter Packet recieved from Peer: " + this.connection
                
                .getClientAddress() + ", Reason: " + e.getMessage()); 
          if (!connectionCloseEventGenerated && isMaxMalformedLimitReached()) {
            connectionCloseEventGenerated = true;
            LogManager.getLogger().error("CONN-HNDLR", "Max Malformed Limit Reached, Sending DPR to Peer: " + this.connection.getClientAddress());
            event = ConnectionEvents.CONNECTION_DPR;
            notifyNetworkEventListener(event, this.networkConnectionEventListeners);
          } 
          LogManager.getLogger().trace("CONN-HNDLR", (Throwable)e);
        } 
      } 
    } catch (MalformedPacketException e) {
      LogManager.getLogger().error("CONN-HNDLR", "Discarding Malformed Diameter Packet, Closing Connection for peer: " + this.connection.getClientAddress() + ", reason : " + e.getMessage());
      LogManager.ignoreTrace((Exception)e);
      closeConnection(event);
    } catch (EOFException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("CONN-HNDLR", "End of Stream reached, Closing Connection for Peer: " + this.connection
            .getClientAddress() + ", Reason : " + e); 
      closeConnection(event);
    } catch (SSLHandshakeException e) {
      Stack.generateAlert(StackAlertSeverity.ERROR, null, "CONN-HNDLR", "Handshake fail of TLS on IP: " + this.connection
          .getClientAddress() + ". Reason: " + e
          .getMessage());
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("CONN-HNDLR", "Closing Connection due to Handshake Error handling client " + this.connection
            
            .getClientAddress() + ". Reason: " + e.getMessage()); 
      LogManager.getLogger().trace("CONN-HNDLR", e);
      closeConnection(ConnectionEvents.HANDSHAKE_FAIL);
    } catch (IOException ioExp) {
      LogManager.getLogger().error("CONN-HNDLR", "Closing Connection due to I/O Error handling client " + this.connection
          .getClientAddress() + ". Reason: " + ioExp.getMessage());
      LogManager.getLogger().trace("CONN-HNDLR", ioExp);
      closeConnection(event);
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("CONN-HNDLR", "Closing Connection due to General error handling client " + this.connection.getClientAddress() + ". Reason: " + e.getMessage()); 
      LogManager.getLogger().trace("CONN-HNDLR", e);
      closeConnection(event);
    } 
  }
  
  private void doConnectionNegotiation() throws Exception {
    DiameterPacket packet = this.connection.getInputStream().readDiameterPacket();
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("CONN-HNDLR", "Packet obtained from stream, assigning task to process packet"); 
    this.context.executeInSync((Packet)packet, this);
  }
  
  private boolean isMaxMalformedLimitReached() {
    if (this.malformedRequestCount >= 10)
      return true; 
    return false;
  }
  
  public boolean isConnected() {
    return this.connection.isConnected();
  }
  
  public boolean isResponder() {
    return (this.conenctionRole == ConnectionRole.Responder);
  }
  
  public void send(Packet packet) throws IOException {
    if (!this.connection.isConnected())
      throw new IOException("Connection is closed"); 
    try {
      this.connection.write(packet);
    } catch (IOException e) {
      if (this.connection.isClosed() || !this.connection.isConnected() || this.connection.isInputShutdown() || this.connection.isOutputShutdown()) {
        closeConnection(ConnectionEvents.DISCONNECTED);
      } else {
        LogManager.getLogger().trace("CONN-HNDLR", e);
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("CONN-HNDLR", "Error while sending packet to " + this.connection
              .getClientAddress() + ":" + this.connection.getClientPort() + " ignored. Reason: " + e
              .getMessage()); 
      } 
      throw e;
    } catch (Exception e) {
      LogManager.getLogger().trace("CONN-HNDLR", e);
    } 
  }
  
  public void closeConnection(ConnectionEvents event) {
    try {
      if (!this.connectionLock.tryLock(10L, TimeUnit.MILLISECONDS))
        return; 
      if (!this.connection.isConnected()) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("CONN-HNDLR", "Close connection invoked where connection is already closed."); 
        return;
      } 
      if (ConnectionEvents.DISCONNECTED == event)
        this.connection.closeConnection(); 
      try {
        if (this.context.removeConnectionHandler(this)) {
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("CONN-HNDLR", "Successfully removed Connection Handler(" + this.connection.getClientAddress() + ":" + this.connection.getClientPort() + ") from Total Connection Listeners"); 
        } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
          LogManager.getLogger().warn("CONN-HNDLR", "Failed to remove Connection Handler(" + this.connection.getClientAddress() + ":" + this.connection.getClientAddress() + ") from Total Connection Listeners");
        } 
      } catch (Exception e) {
        LogManager.getLogger().error("CONN-HNDLR", "Failed to remove Connection Handler( " + this.connection.getClientAddress() + ":" + this.connection.getClientAddress() + ") from Total Connection Listeners. Reason: " + e.getMessage());
        LogManager.getLogger().trace("CONN-HNDLR", e);
      } 
      if (this.networkConnectionEventListeners != null && !this.networkConnectionEventListeners.isEmpty())
        notifyNetworkEventListener(event, this.networkConnectionEventListeners); 
      this.connection.closeConnection();
    } catch (InterruptedException e1) {
      Thread.currentThread().interrupt();
      return;
    } finally {
      try {
        this.connectionLock.unlock();
      } catch (Exception e) {
        LogManager.ignoreTrace(e);
      } 
    } 
  }
  
  public void terminateConnection() {
    try {
      if (this.context.removeConnectionHandler(this)) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("CONN-HNDLR", "Successfully removed Connection Handler(" + this.connection
              .getClientAddress() + ":" + this.connection.getClientPort() + ") from Total Connection Listeners"); 
      } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
        LogManager.getLogger().warn("CONN-HNDLR", "Failed to remove Connection Handler(" + this.connection
            .getClientAddress() + ":" + this.connection.getClientAddress() + ") from Total Connection Listeners");
      } 
    } catch (Exception e) {
      LogManager.getLogger().error("CONN-HNDLR", "Failed to remove Connection Handler( " + this.connection
          .getClientAddress() + ":" + this.connection.getClientAddress() + ") from Total Connection Listeners. Reason: " + e
          .getMessage());
      LogManager.getLogger().trace("CONN-HNDLR", e);
    } finally {
      if (this.connection != null && this.connection.isConnected())
        this.connection.closeConnection(); 
    } 
  }
  
  private void notifyNetworkEventListener(ConnectionEvents event, List<NetworkConnectionEventListener> connectionEventListeners) {
    Map<PeerDataCode, String> eventParam;
    switch (event) {
      case CONNECTION_ESTABLISHED:
        for (NetworkConnectionEventListener connectionEventListener : connectionEventListeners)
          connectionEventListener.connectionEstablished(); 
        break;
      case HANDSHAKE_FAIL:
      case CONNECTION_BREAK:
      case DISCONNECTED:
        for (NetworkConnectionEventListener connectionEventListener : connectionEventListeners)
          connectionEventListener.connectionBreak(this, event); 
        break;
      case CONNECTION_DPR:
        eventParam = new HashMap<>(2);
        eventParam.put(PeerDataCode.DISCONNECT_REASON, "MALFORMED_PACKET");
        for (NetworkConnectionEventListener connectionEventListener : connectionEventListeners)
          connectionEventListener.connectionDPR(eventParam, event); 
        break;
    } 
  }
  
  public void addNetworkConnectionEventListener(NetworkConnectionEventListener networkConnectionEventListener) {
    if (networkConnectionEventListener != null)
      this.networkConnectionEventListeners.add(networkConnectionEventListener); 
  }
  
  public boolean equals(Object obj) {
    try {
      if (obj == null)
        return false; 
      if (getClass() != obj.getClass())
        return false; 
      ConnectionHandler connectionHandler = (ConnectionHandler)obj;
      return this.connection.equals(connectionHandler.connection);
    } catch (Exception e) {
      LogManager.ignoreTrace(e);
      return super.equals(obj);
    } 
  }
  
  public int hashCode() {
    try {
      return this.connection.hashCode();
    } catch (Exception e) {
      LogManager.ignoreTrace(e);
      return super.hashCode();
    } 
  }
  
  public InetAddress getSourceInetAddress() {
    return this.connection.getSourceInetAddress();
  }
  
  public String getSourceIpAddress() {
    return this.connection.getSourceIpAddress();
  }
  
  public int getSourcePort() {
    return this.connection.getSourcePort();
  }
  
  public String getHostName() {
    return this.hostName;
  }
  
  public void setHostName(String hostName) {
    this.hostName = hostName;
  }
  
  public String getLocalAddress() {
    return this.connection.getLocalAddress();
  }
  
  public void secureConnection(PeerConnectionData peerData, EliteSSLContextExt eliteSSLContext) throws HandShakeFailException {
    this.connection = this.connectionFactory.secureConnectionWithTLS(this.connection, this.conenctionRole, this.context, eliteSSLContext);
  }
  
  public int getLocalPort() {
    return this.connection.getLocalPort();
  }
  
  public SecurityProtocol getSecurityProtocol() {
    return this.connection.getSecurityProtocol();
  }
}
