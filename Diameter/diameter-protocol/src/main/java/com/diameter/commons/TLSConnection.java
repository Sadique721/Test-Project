package com.diameter.commons;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TLSConnection implements Connection {
  private static final String MODULE = "TLS-CONN";
  
  public static final int SUCCESS = 1;
  
  public static final int FAILUER = 2;
  
  private TCPConnection tcpConnection;
  
  private SSLSocket sslSocket;
  
  private DiameterInputStream inStream;
  
  private OutputStream outStream;
  
  private ConnectorContext context;
  
  private AtomicBoolean connected = new AtomicBoolean();
  
  private boolean isHandShakeComplete = false;
  
  private TLSConnectionMode tlsMode;
  
  private EliteSSLContextExt eliteSSLContext;
  
  public TLSConnection(ConnectorContext context, TCPConnection connection, TLSConnectionMode tlsMode, EliteSSLContextExt eliteSSLContext) {
    this.context = context;
    this.tcpConnection = connection;
    this.tlsMode = tlsMode;
    this.eliteSSLContext = eliteSSLContext;
  }
  
  public synchronized void startHandshake() throws HandShakeFailException {
    if (!this.isHandShakeComplete) {
      if (TLSConnectionMode.CLIENT == this.tlsMode) {
        wrapSocketAsClient();
      } else {
        wrapSocketAsServer();
      } 
      this.connected.set(true);
      this.isHandShakeComplete = true;
    } 
  }
  
  private boolean wrapSocketAsClient() throws HandShakeFailException {
    try {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Fetch peer connection data based on IP Address = " + this.tcpConnection.getSocket().getInetAddress().getHostAddress()); 
      if (this.eliteSSLContext == null)
        throw new HandShakeFailException("SSLContext not found for IP-Address = " + this.tcpConnection.getSocket().getInetAddress().getHostAddress()); 
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Start creating TLS Socket as client"); 
      SSLSocketFactory sslFactory = this.eliteSSLContext.getSSLSocketFactory();
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Create TLS Socket on IP Address = " + this.context.getNetworkAddress() + ":" + this.context.getNetworkPort()); 
      this.sslSocket = (SSLSocket)sslFactory.createSocket(this.tcpConnection.getSocket(), this.tcpConnection.getClientAddress(), this.tcpConnection.getClientPort(), true);
      List<String> enabledCipherLst = this.eliteSSLContext.getEnabledCiphersuites();
      if (enabledCipherLst == null || enabledCipherLst.isEmpty())
        throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + this.tcpConnection.getClientAddress() + ". Reason: Enabled CipherSuite Not Configured for TLSVersion: " + this.eliteSSLContext
            .getEliteSSLParameter().getMaxTlsVersion()); 
      String[] strAry = new String[enabledCipherLst.size()];
      strAry = enabledCipherLst.<String>toArray(strAry);
      this.sslSocket.setEnabledCipherSuites(strAry);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Enabled Cipher Cuites : " + Arrays.toString((Object[])this.sslSocket.getEnabledCipherSuites())); 
      List<TLSVersion> supportedVersionList = this.eliteSSLContext.getEnabledTLSVersion();
      if (supportedVersionList == null || supportedVersionList.isEmpty())
        throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + this.tcpConnection.getClientAddress() + ". Reason: Min-Max TLS Version not properly configured"); 
      String[] supportedTLSVersions = new String[supportedVersionList.size()];
      for (int i = 0; i < supportedVersionList.size(); i++)
        supportedTLSVersions[i] = ((TLSVersion)supportedVersionList.get(i)).version; 
      this.sslSocket.setEnabledProtocols(supportedTLSVersions);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Supported TLS Version: " + Arrays.toString((Object[])this.sslSocket.getEnabledProtocols())); 
      this.inStream = new DiameterInputStream(this.sslSocket.getInputStream());
      this.outStream = new DataOutputStream(new BufferedOutputStream(this.sslSocket.getOutputStream()));
      this.sslSocket.addHandshakeCompletedListener(event -> {
            if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
              LogManager.getLogger().info("TLS-CONN", "Handshake success for TLS Client on IP : " + this.tcpConnection.getClientAddress()); 
          });
      long timeout = 5000L;
      if (this.eliteSSLContext != null && this.eliteSSLContext.getEliteSSLParameter().getHandshakeTimeout() > 0L)
        timeout = this.eliteSSLContext.getEliteSSLParameter().getHandshakeTimeout(); 
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Wait for " + timeout + "ms to complete handshake"); 
      ScheduledFuture<Integer> futurTask = this.context.scheduleCallableSingleExecutionTask(new HandshakeTask(this.sslSocket));
      if (2 == ((Integer)futurTask.get(timeout, TimeUnit.MILLISECONDS)).intValue())
        throw new HandShakeFailException("HandShake Fail"); 
    } catch (ExecutionException e) {
      throw new HandShakeFailException("HandShake fail  for TLS Client on IP : " + this.tcpConnection.getClientAddress() + " . Reason: " + e.getMessage(), e);
    } catch (TimeoutException e) {
      throw new HandShakeFailException("Handshake time exceeded  for TLS Client on IP : " + this.tcpConnection.getClientAddress() + ". Reason: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + this.tcpConnection.getClientAddress() + ". Reason: " + e.getMessage(), e);
    } 
    return false;
  }
  
  private class HandshakeTask implements CallableSingleExecutionAsyncTask<Integer> {
    private SSLSocket socket;
    
    public HandshakeTask(SSLSocket socket) {
      this.socket = socket;
    }
    
    public long getInitialDelay() {
      return 0L;
    }
    
    public TimeUnit getTimeUnit() {
      return TimeUnit.SECONDS;
    }
    
    public Integer execute(AsyncTaskContext context) {
      try {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("TLS-CONN", "Start Handshaking for TLS Client on IP : " + TLSConnection.this.tcpConnection.getClientAddress()); 
        this.socket.startHandshake();
        return Integer.valueOf(1);
      } catch (IOException ex) {
        LogManager.getLogger().trace("TLS-CONN", ex);
        if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
          LogManager.getLogger().error("TLS-CONN", "Handshake fail for TLS Client on IP : " + TLSConnection.this.tcpConnection.getClientAddress() + ", reason : " + ex.getMessage()); 
        LogManager.getLogger().trace("TLS-CONN", ex);
        return Integer.valueOf(2);
      } 
    }
  }
  
  private boolean wrapSocketAsServer() throws HandShakeFailException {
    try {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Fetching peer connection data based on IP Address = " + this.tcpConnection.getClientAddress()); 
      if (this.eliteSSLContext == null)
        throw new HandShakeFailException("SSLContext not found for IP-Address = " + this.tcpConnection.getClientAddress()); 
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Start creating TLS Socket as server"); 
      SSLSocketFactory sslFactory = this.eliteSSLContext.getSSLSocketFactory();
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Create TLS Socket on IP Address = " + this.context.getNetworkAddress() + ":" + this.context.getNetworkPort()); 
      this.sslSocket = (SSLSocket)sslFactory.createSocket(this.tcpConnection.getSocket(), this.context.getNetworkAddress(), this.context.getNetworkPort(), true);
      this.sslSocket.setUseClientMode(false);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Client aurhentication set to :" + this.eliteSSLContext.isClientCertificateRequestRequired()); 
      if (this.eliteSSLContext.getEliteSSLParameter().isClientCertificateRequestRequired())
        if (this.eliteSSLContext.getEliteSSLParameter().isValidateCertificateCA()) {
          this.sslSocket.setNeedClientAuth(false);
        } else {
          this.sslSocket.setWantClientAuth(false);
        }  
      List<String> enabledCipherLst = this.eliteSSLContext.getEnabledCiphersuites();
      if (enabledCipherLst == null || enabledCipherLst.isEmpty())
        throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + this.tcpConnection.getClientAddress() + ". Reason: Enabled CipherSuite Not Configured for TLSVersion: " + this.eliteSSLContext
            .getEliteSSLParameter().getMaxTlsVersion()); 
      String[] strAry = new String[enabledCipherLst.size()];
      strAry = enabledCipherLst.<String>toArray(strAry);
      this.sslSocket.setEnabledCipherSuites(strAry);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Enabled Cipher Cuites : " + Arrays.toString((Object[])this.sslSocket.getEnabledCipherSuites())); 
      List<TLSVersion> supportedVersionList = this.eliteSSLContext.getEnabledTLSVersion();
      if (supportedVersionList == null || supportedVersionList.isEmpty())
        throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + this.tcpConnection.getClientAddress() + ". Reason: Min-Max TLS Version not properly configured"); 
      String[] supportedTLSVersions = new String[supportedVersionList.size()];
      for (int i = 0; i < supportedVersionList.size(); i++)
        supportedTLSVersions[i] = ((TLSVersion)supportedVersionList.get(i)).version; 
      this.sslSocket.setEnabledProtocols(supportedTLSVersions);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "Suppoted TLS Version: " + Arrays.toString((Object[])this.sslSocket.getEnabledProtocols())); 
      this.inStream = new DiameterInputStream(this.sslSocket.getInputStream());
      this.outStream = new DataOutputStream(new BufferedOutputStream(this.sslSocket.getOutputStream()));
      this.sslSocket.addHandshakeCompletedListener(arg0 -> {
            if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
              LogManager.getLogger().info("TLS-CONN", "Handshake Completed for TLS Server on IP-Address : " + this.tcpConnection.getClientAddress()); 
          });
      this.sslSocket.startHandshake();
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("TLS-CONN", "TLS Socket created on IP Address = " + this.context.getNetworkAddress() + ":" + this.context.getNetworkPort()); 
      return true;
    } catch (Exception e) {
      LogManager.getLogger().trace("TLS-CONN", e);
      throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + this.tcpConnection.getClientAddress() + ". Reason: " + e.getMessage(), e);
    } 
  }
  
  public String getSourceIpAddress() {
    return this.tcpConnection.getSourceIpAddress();
  }
  
  public int getSourcePort() {
    return this.tcpConnection.getSourcePort();
  }
  
  public String getLocalAddress() {
    return this.tcpConnection.getLocalAddress();
  }
  
  public int hashCode() {
    return this.tcpConnection.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    try {
      TLSConnection tlsConnection = (TLSConnection)obj;
      return this.tcpConnection.equals(tlsConnection.tcpConnection);
    } catch (Exception ex) {
      LogManager.getLogger().error("TLS-CONN", "Error during comparing TLS Connection. Reason:" + ex.getMessage());
      LogManager.getLogger().trace("TLS-CONN", ex);
      return super.equals(obj);
    } 
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
  
  public String getClientAddress() {
    return this.tcpConnection.getClientAddress();
  }
  
  public int getClientPort() {
    return this.tcpConnection.getClientPort();
  }
  
  public DiameterInputStream getInputStream() {
    return this.inStream;
  }
  
  public boolean isClosed() {
    return this.sslSocket.isClosed();
  }
  
  public boolean isInputShutdown() {
    return this.sslSocket.isInputShutdown();
  }
  
  public boolean isOutputShutdown() {
    return this.sslSocket.isOutputShutdown();
  }
  
  public void closeConnection() {
    if (!this.connected.compareAndSet(true, false))
      return; 
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("TLS-CONN", "Closing Connection to " + this.tcpConnection.getClientAddress() + "/" + this.tcpConnection.getClientPort()); 
    try {
      if (this.inStream != null)
        this.inStream.close(); 
    } catch (Exception e) {
      LogManager.getLogger().error("TLS-CONN", "Error while closing input stream for peer: " + this.sslSocket.getInetAddress() + ", reason : " + e);
      LogManager.getLogger().trace("TLS-CONN", e);
    } 
    try {
      if (this.outStream != null)
        this.outStream.close(); 
    } catch (Exception e) {
      LogManager.getLogger().error("TLS-CONN", "Error while closing output stream for peer: " + this.sslSocket.getInetAddress() + ", reason : " + e);
      LogManager.getLogger().trace("TLS-CONN", e);
    } 
    try {
      if (this.sslSocket != null && !this.sslSocket.isClosed())
        this.sslSocket.close(); 
    } catch (Exception e) {
      LogManager.getLogger().trace("TLS-CONN", e);
      LogManager.getLogger().error("TLS-CONN", "Error while closing ssl socket for peer: " + this.sslSocket.getInetAddress() + ", reason : " + e);
    } 
  }
  
  public void read(byte[] data, int off, int len) {}
  
  public boolean isClient() {
    return (this.tlsMode == TLSConnectionMode.CLIENT);
  }
  
  public boolean isHandshakeCompleted() {
    return this.isHandShakeComplete;
  }
  
  public EliteSSLContextExt getEliteSSLContext() {
    return this.eliteSSLContext;
  }
  
  public int getLocalPort() {
    return this.tcpConnection.getLocalPort();
  }
  
  public SecurityProtocol getSecurityProtocol() {
    return SecurityProtocol.TLS;
  }
  
  public InetAddress getSourceInetAddress() {
    return this.tcpConnection.getSourceInetAddress();
  }
}
