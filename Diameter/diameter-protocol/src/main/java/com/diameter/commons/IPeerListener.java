package com.diameter.commons;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public interface IPeerListener {
  void processReceivedDiameterPacket(Packet paramPacket, NetworkConnectionHandler paramNetworkConnectionHandler) throws UnhandledTransitionException;
  
  void handleEvent(IEventEnum paramIEventEnum, ConnectionEvents paramConnectionEvents) throws UnhandledTransitionException;
  
  void handleStateTransition(IStateTransitionData paramIStateTransitionData) throws UnhandledTransitionException;
  
  void setConnectionListener(NetworkConnectionHandler paramNetworkConnectionHandler);
  
  boolean isSameConnection(NetworkConnectionHandler paramNetworkConnectionHandler);
  
  boolean isPeerConnected();
  
  String getPeerName();
  
  PeerData getPeerData();
  
  String getHostIdentity();
  
  String getRealm();
  
  int getCommunicationPort();
  
  void onConnectionUp();
  
  void onConnectionDown();
  
  List<String> getHostIPAddresses();
  
  String getLocalIpAddress();
  
  InetAddress getRemoteInetAddress();
  
  void setRemoteIpAddress(String paramString);
  
  void setRemoteInetAddress(InetAddress paramInetAddress);
  
  int getLocalPort();
  
  void setRemotePort(int paramInt);
  
  void setHostIdentity(String paramString);
  
  DiameterPeerState registerStatusListener(DiameterPeerStatusListener paramDiameterPeerStatusListener);
  
  boolean isSendDPRonCloseEvent();
  
  void handleEvent(IEventEnum paramIEventEnum, ConnectionEvents paramConnectionEvents, Map<PeerDataCode, String> paramMap) throws UnhandledTransitionException;
  
  void sendDiameterRequest(DiameterRequest paramDiameterRequest, @Nonnull ResponseListener paramResponseListener) throws UnhandledTransitionException;
  
  void sendDiameterAnswer(DiameterAnswer paramDiameterAnswer) throws UnhandledTransitionException;
}
