package com.diameter.commons;

import java.util.concurrent.ScheduledFuture;

public interface ConnectorContext {
  int getSocketReceiveBufferSize();
  
  int getSocketSendBufferSize();
  
  void executeInSync(Packet paramPacket, NetworkConnectionHandler paramNetworkConnectionHandler);
  
  void executeInAsync(Packet paramPacket, NetworkConnectionHandler paramNetworkConnectionHandler);
  
  boolean removeConnectionHandler(NetworkConnectionHandler paramNetworkConnectionHandler);
  
  String getNetworkAddress();
  
  int getNetworkPort();
  
  PeerConnectionData getPeerConnectionData(String paramString);
  
  <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> paramCallableSingleExecutionAsyncTask);
  
  EliteSSLContextExt createEliteSSLContext() throws Exception;
  
  SecurityStandard getDefaultSecurityStandard();
}
