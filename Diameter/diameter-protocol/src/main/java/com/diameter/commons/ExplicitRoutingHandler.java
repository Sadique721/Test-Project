package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public class ExplicitRoutingHandler {
  private static final String MODULE = "EXP-RTNG-HNDLR";
  
  private static final int OWN_PATH_RECORD_NOT_FOUND = -1;
  
  public void handle(DiameterPacket diameterPacket) throws ExplicitRoutingFailedException {
    if (diameterPacket.isResponse())
      return; 
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("EXP-RTNG-HNDLR", "Handling Explicit Routing for Session-ID=" + diameterPacket
          .getAVPValue("0:263")); 
    try {
      AvpGrouped explicitPathAVP = (AvpGrouped)diameterPacket.getAVP("2011:35003");
      if (explicitPathAVP == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("EXP-RTNG-HNDLR", "Not performing Explicit Routing, Reason: Explicit-Path AVP not found"); 
        return;
      } 
      List<IDiameterAVP> explicitPathRecordAVPs = explicitPathAVP.getSubAttributeList("2011:35001");
      if (explicitPathRecordAVPs.size() == 0) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Sending " + ResultCode.DIAMETER_ER_NOT_AVAILABLE + ", Reason: " + "Explicit-Path" + " Grouped AVP does not contain " + "Explicit-Path-Record" + " AVP."); 
        throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, "Explicit-Path Grouped AVP does not contain Explicit-Path-Record AVP.");
      } 
      int ownPathRecordIndex = getOwnPathRecordIndex(explicitPathRecordAVPs);
      if (ownPathRecordIndex == -1) {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("EXP-RTNG-HNDLR", "Own Explicit-Path-Record not found for Session-ID=" + diameterPacket
              .getAVPValue("0:263")); 
        IDiameterAVP destHost = diameterPacket.getAVP("0:293");
        if (isOngoingPathDiscovery(destHost, explicitPathRecordAVPs)) {
          appendOwnIdentity(explicitPathAVP);
          return;
        } 
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("EXP-RTNG-HNDLR", "Not participating in Explicit Routing, Reason: Explicit Path already discovered for Session-ID=" + diameterPacket
              .getAVPValue("0:263")); 
        return;
      } 
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("EXP-RTNG-HNDLR", "Own Explicit-Path-Record found for Session-ID=" + diameterPacket
            .getAVPValue("0:263")); 
      if (ownPathRecordIndex != 1) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Sending " + ResultCode.DIAMETER_INVALID_PROXY_PATH_STACK + ", Reason: Own idenity is present but not in first Explicit Path Record"); 
        throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_INVALID_PROXY_PATH_STACK, "Own idenity is present but not in first path record");
      } 
      if (explicitPathRecordAVPs.size() == 1) {
        handleERDestination();
        return;
      } 
      handleERProxy(diameterPacket, explicitPathAVP, explicitPathRecordAVPs);
    } catch (ClassCastException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Sending " + ResultCode.DIAMETER_ER_NOT_AVAILABLE + ", Reason: Unable to parse " + "Explicit-Path" + " AVP, Reason: " + e
            
            .getMessage()); 
      throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, e);
    } 
  }
  
  private void appendOwnIdentity(AvpGrouped explicitPathAVP) throws ExplicitRoutingFailedException {
    AvpGrouped ownExplicitPath = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute("2011:35001");
    if (ownExplicitPath == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Unable to append Own Path-Record, Sending " + ResultCode.DIAMETER_ER_NOT_AVAILABLE + ", Reason: " + "Explicit-Path-Record" + " not found in Dictionary"); 
      throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, "Explicit-Path-Record not found in Dictionary");
    } 
    IDiameterAVP subAvp = DiameterDictionary.getInstance().getKnownAttribute("2011:35004");
    if (subAvp == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Unable to append Own Proxy Host, Sending " + ResultCode.DIAMETER_ER_NOT_AVAILABLE + ", Reason: " + "Hw-Proxy-Host" + " not found in Dictionary"); 
      throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, "Hw-Proxy-Host not found in Dictionary");
    } 
    subAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
    ownExplicitPath.addSubAvp(subAvp);
    subAvp = DiameterDictionary.getInstance().getKnownAttribute("2011:35002");
    if (subAvp == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Not Appending Own Proxy-Realm, Reason: Proxy-Realm not found in Dictionary"); 
    } else {
      subAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
      ownExplicitPath.addSubAvp(subAvp);
    } 
    explicitPathAVP.addSubAvp((IDiameterAVP)ownExplicitPath);
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("EXP-RTNG-HNDLR", "Appended Own Explicit-Path-Record"); 
  }
  
  private boolean isOngoingPathDiscovery(IDiameterAVP destHostAVP, List<IDiameterAVP> explicitPathRecordAVPs) {
    if (destHostAVP == null || destHostAVP.getStringValue() == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("EXP-RTNG-HNDLR", "Explicit Path Discovery is in progress. Reason: Destination Host not found"); 
      return true;
    } 
    AvpGrouped explicitPathRecord = (AvpGrouped)explicitPathRecordAVPs.get(0);
    IDiameterAVP proxyHostAvp = explicitPathRecord.getSubAttribute("2011:35004");
    if (proxyHostAvp != null && 
      !destHostAVP.getStringValue().equals(proxyHostAvp.getStringValue())) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("EXP-RTNG-HNDLR", "Explicit Path Discovery is in progress. Reason: Proxy-Host of first Explicit-Path-Record does not match the Destination Host"); 
      return true;
    } 
    return false;
  }
  
  private void handleERProxy(DiameterPacket diameterPacket, AvpGrouped explicitPathAVP, List<IDiameterAVP> explicitPathRecordAVPs) throws ExplicitRoutingFailedException {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("EXP-RTNG-HNDLR", "Handling ER Proxy in Explicit Routing."); 
    ArrayList<IDiameterAVP> subAvpList = explicitPathAVP.getGroupedAvp();
    for (int index = 0; index < subAvpList.size(); index++) {
      if ("2011:35001".equalsIgnoreCase(((IDiameterAVP)subAvpList.get(index)).getAVPId())) {
        subAvpList.remove(index);
        break;
      } 
    } 
    explicitPathAVP.setGroupedAvp(subAvpList);
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("EXP-RTNG-HNDLR", "Popping Own Explicit-Path-Record AVP from Explicit Path Stack."); 
    AvpGrouped nextExplicitPathRecord = (AvpGrouped)explicitPathRecordAVPs.get(1);
    if (nextExplicitPathRecord == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Not populating Destination AVPs, Sending: " + ResultCode.DIAMETER_ER_NOT_AVAILABLE + ", Reason: Next " + "Explicit-Path-Record" + " AVP not found in Explicit Path."); 
      throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, "Next Explicit-Path-Record AVP not found in Explicit Path.");
    } 
    IDiameterAVP pathAvp = nextExplicitPathRecord.getSubAttribute("2011:35004");
    if (pathAvp == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Not populating Destination AVPs, Reason: Hw-Proxy-Host AVP not found in Next Explicit-Path-Record"); 
      throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, "Hw-Proxy-Host AVP not found in Next Explicit-Path-Record");
    } 
    IDiameterAVP destAvp = diameterPacket.getAVP("0:293");
    String value = pathAvp.getStringValue();
    if (value != null) {
      if (destAvp == null) {
        destAvp = DiameterDictionary.getInstance().getAttribute("0:293");
        diameterPacket.addAvp(destAvp);
      } 
      destAvp.setStringValue(value);
    } 
    destAvp = diameterPacket.getAVP("0:283");
    if (destAvp != null) {
      pathAvp = nextExplicitPathRecord.getSubAttribute("2011:35002");
      if (pathAvp == null)
        return; 
      value = pathAvp.getStringValue();
      if (value != null)
        destAvp.setStringValue(value); 
    } 
  }
  
  private void handleERDestination() {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("EXP-RTNG-HNDLR", "Handling ER Destination in Explicit Routing."); 
  }
  
  private int getOwnPathRecordIndex(List<IDiameterAVP> explicitPathRecordAVPs) throws ExplicitRoutingFailedException {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("EXP-RTNG-HNDLR", "Checking for Own Explicit-Path-Record availability in Explicit-Path."); 
    for (int index = 0; index < explicitPathRecordAVPs.size(); index++) {
      if (isOwnProxyHost((AvpGrouped)explicitPathRecordAVPs.get(index)))
        return index + 1; 
    } 
    return -1;
  }
  
  private boolean isOwnProxyHost(AvpGrouped explicitPathRecord) throws ExplicitRoutingFailedException {
    IDiameterAVP hwProxyHost = explicitPathRecord.getSubAttribute("2011:35004");
    if (hwProxyHost == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("EXP-RTNG-HNDLR", "Sending " + ResultCode.DIAMETER_ER_NOT_AVAILABLE + ", Reason: " + "Hw-Proxy-Host" + " AVP not found in " + "Explicit-Path-Record"); 
      throw new ExplicitRoutingFailedException(ResultCode.DIAMETER_ER_NOT_AVAILABLE, "Hw-Proxy-Host AVP not found in Explicit-Path-Record");
    } 
    String ownIdentity = Parameter.getInstance().getOwnDiameterIdentity();
    if (ownIdentity.equals(hwProxyHost.getStringValue()))
      return true; 
    return false;
  }
}
