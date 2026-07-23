package com.diameter.commons;

import java.util.Set;

public interface DiameterPeerConfig {
  String getPeerId();
  
  int getPeerFirmwareRevison();
  
  StorageTypes getPeerStorageType();
  
  RowStatus getPeerRowStatus();
  
  int getDbpPeerPortListen();
  
  TransportProtocols getDbpPeerTransportProtocol();
  
  SecurityProtocol getDbpPeerSecurity();
  
  int getDbpPeerPortConnect();
  
  Set<ApplicationEnum> getDbpAppAdvFromPeer();
  
  Set<ApplicationEnum> getDbpAppAdvToPeer();
  
  DiameterBasePeerVendorTable[] getDbpPeerVendorTable();
  
  DiameterBasePeerIpAddressTable[] getPeerIpAddressIndex();
  
  String getPeerIpAddresses();
  
  long getDbpPerPeerStatsTimeoutConnAtmpts();
  
  int getPCBState();
  
  long getDbpPerPeerInfoStateDuration();
  
  int getPeerState();
  
  long getPeerWatchDogInterval();
  
  long getDbpPeerIndex();
  
  boolean isConnectionInitiationEnabled();
  
  String getPeerLocalIpAddresses();
}
