package com.diameter.commons;

import java.util.Map;

public interface NetworkConnectionEventListener {
  void connectionEstablished();
  
  void connectionBreak(NetworkConnectionHandler paramNetworkConnectionHandler, ConnectionEvents paramConnectionEvents);
  
  void connectionFailure(NetworkConnectionHandler paramNetworkConnectionHandler);
  
  void connectionDPR(Map<PeerDataCode, String> paramMap, ConnectionEvents paramConnectionEvents);
  
  void connectionCreated(NetworkConnectionHandler paramNetworkConnectionHandler);
}