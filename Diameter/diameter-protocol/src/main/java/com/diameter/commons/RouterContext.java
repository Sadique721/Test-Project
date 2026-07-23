package com.diameter.commons;

public interface RouterContext {
  PeerData getPeerData(String paramString);
  
  DiameterPeerCommunicator getPeerCommunicator(String paramString);
  
  String getVirtualRoutingPeerName();
  
  void updateUnknownH2HDropStatistics(DiameterAnswer paramDiameterAnswer, String paramString1, String paramString2, RoutingActions paramRoutingActions);
  
  void updateDiameterStatsPacketDroppedStatistics(DiameterPacket paramDiameterPacket, String paramString1, String paramString2, RoutingActions paramRoutingActions);
  
  void updateRealmInputStatistics(DiameterPacket paramDiameterPacket, String paramString, RoutingActions paramRoutingActions);
  
  void updateRealmOutputStatistics(DiameterPacket paramDiameterPacket, String paramString, RoutingActions paramRoutingActions);
  
  void updateRealmTimeoutRequestStatistics(DiameterRequest paramDiameterRequest, String paramString, RoutingActions paramRoutingActions);
  
  void postRequestRouting(DiameterRequest paramDiameterRequest1, DiameterRequest paramDiameterRequest2, String paramString1, String paramString2, String paramString3);
  
  void preAnswerRouting(DiameterRequest paramDiameterRequest1, DiameterRequest paramDiameterRequest2, DiameterAnswer paramDiameterAnswer, String paramString1, String paramString2);
  
  void postAnswerRouting(DiameterRequest paramDiameterRequest1, DiameterRequest paramDiameterRequest2, DiameterAnswer paramDiameterAnswer1, DiameterAnswer paramDiameterAnswer2, String paramString1, String paramString2, String paramString3);
  
  CDRDriver<DiameterPacket> getDiameterCDRDriver(String paramString) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;
  
  RoutingEntry getRoutingEntry(String paramString);
}
