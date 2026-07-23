package com.diameter.commons;

import java.util.List;

public class VirtualAgent extends RelayAgent {
  public static final String MODULE = "VIRTUAL-AGNT";
  
  public VirtualAgent(RouterContext routerContext, IDiameterSessionManager diameterSessionManager) {
    super(routerContext, diameterSessionManager);
  }
  
  protected void postRequestProcessing(DiameterRequest destinationRequest) {
    IDiameterAVP originHostAvp = destinationRequest.getAVP("0:264");
    if (originHostAvp != null) {
      originHostAvp.setStringValue("*");
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Origin-Host AVP replaced for Packet with Session-ID=" + destinationRequest.getAVPValue("0:263")); 
    } 
    IDiameterAVP originRealm = destinationRequest.getAVP("0:296");
    if (originRealm != null) {
      originRealm.setStringValue("*");
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Origin-Realm AVP replaced for Packet with Session-ID=" + destinationRequest.getAVPValue("0:263")); 
    } 
    IDiameterAVP destRealmAVP = destinationRequest.getAVP("0:283");
    if (destRealmAVP == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Destination-Realm AVP not replaced for Request with Session-ID=" + destinationRequest.getAVPValue("0:263") + ", Reason: " + "Destination-Realm" + " AVP not Arrived in Request"); 
    } else if (Parameter.getInstance().getOwnDiameterRealm().equals(destRealmAVP.getStringValue())) {
      destRealmAVP.setStringValue("*");
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Destination-Realm AVP replaced for Request with Session-ID=" + destinationRequest.getAVPValue("0:263")); 
    } 
    IDiameterAVP destHostAVP = destinationRequest.getAVP("0:293");
    if (destHostAVP == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Destination-Host AVP not replaced for Request with Session-ID=" + destinationRequest.getAVPValue("0:263") + ", Reason: " + "Destination-Host" + " AVP not Arrived in Request"); 
    } else if (Parameter.getInstance().getOwnDiameterIdentity().equals(destHostAVP.getStringValue())) {
      destHostAVP.setStringValue("*");
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Destination-Host AVP replaced for Request with Session-ID=" + destinationRequest.getAVPValue("0:263")); 
    } 
    List<IDiameterAVP> routeRecords = destinationRequest.getAVPList("0:282");
    if (routeRecords == null)
      return; 
    for (IDiameterAVP routeRecord : routeRecords)
      destinationRequest.removeAVP(routeRecord); 
  }
  
  protected void postAnswerProcessing(DiameterAnswer diameterAnswer) {
    IDiameterAVP originHostAvp = diameterAnswer.getAVP("0:264");
    if (originHostAvp != null) {
      originHostAvp.setStringValue("*");
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Origin-Host AVP replaced for Packet with Session-ID=" + diameterAnswer.getAVPValue("0:263")); 
    } 
    IDiameterAVP originRealm = diameterAnswer.getAVP("0:296");
    if (originRealm != null) {
      originRealm.setStringValue("*");
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("VIRTUAL-AGNT", "Origin-Realm AVP replaced for Packet with Session-ID=" + diameterAnswer.getAVPValue("0:263")); 
    } 
  }
}
