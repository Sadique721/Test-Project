package com.diameter.commons;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DiameterConfiguration extends Observable implements DiameterConfigProvider {
  private Map<String, DiameterPeerConfig> peerConfigMap;
  
  private MIBIndexRecorder mibIndexRecorder;
  
  public DiameterConfiguration(MIBIndexRecorder mibIndexRecorder) {
    this.mibIndexRecorder = mibIndexRecorder;
    this.peerConfigMap = Collections.synchronizedMap(new LinkedHashMap<>());
  }
  
  public void init(Collection<DiameterPeer> diameterPeerList) {
    ConcurrentHashMap<String, DiameterPeerConfig> tempPeerConfigMap = new ConcurrentHashMap<>();
    if (diameterPeerList != null)
      for (DiameterPeer peer : diameterPeerList) {
        if (peer == null || Strings.isNullOrBlank(peer.getHostIdentity()))
          continue; 
        this.mibIndexRecorder.recordIndexFor(peer.getPeerData());
        tempPeerConfigMap.put(peer.getHostIdentity(), peer.getPeerConfig());
      }  
    this.peerConfigMap = tempPeerConfigMap;
  }
  
  public String getAllPeerConfigSummary() {
    StringBuilder summary = new StringBuilder();
    for (Map.Entry<String, DiameterPeerConfig> entry : this.peerConfigMap.entrySet()) {
      summary.append("\n");
      summary.append(getPeerConfigSummary(entry.getKey(), entry.getValue()));
    } 
    return summary.toString();
  }
  
  public String getPeerConfigSummary(String hostIdentity) {
    return getPeerConfigSummary(hostIdentity, this.peerConfigMap.get(hostIdentity));
  }
  
  private String getPeerConfigSummary(String hostIdentity, DiameterPeerConfig peerConfig) {
    if (peerConfig == null)
      return "Peer: " + hostIdentity + " is not registered."; 
    TableFormatter formatter = new TableFormatter(new String[] { "PEER: " + hostIdentity }, new int[] { 60 }, 2);
    formatter.add("Peer ID            : " + peerConfig.getPeerId(), 0);
    formatter.add("Peer Index         : " + peerConfig.getDbpPeerIndex(), 0);
    formatter.add("Local Port         : " + peerConfig.getDbpPeerPortConnect(), 0);
    formatter.add("IP Addresses       : " + peerConfig.getPeerIpAddresses(), 0);
    formatter.add("Transport Protocol : " + (peerConfig.getDbpPeerTransportProtocol()).protocolTypeStr, 0);
    formatter.add("Security           : " + (peerConfig.getDbpPeerSecurity()).protocolName, 0);
    formatter.add("Firmware Revision  : " + getPeerFirmwareRevision(peerConfig), 0);
    formatter.add("Supported Vendors  : " + getPeerSupportedVendorSummary(peerConfig.getDbpPeerVendorTable()), 0);
    Set<ApplicationEnum> appTable = peerConfig.getDbpAppAdvFromPeer();
    if (appTable != null && appTable.size() > 0) {
      formatter.addNewLine();
      formatter.add("--Supported Applications--", 3);
      formatter.add(getPeerAppAdvSumary(appTable));
    } else {
      formatter.add("--No Applications registered--", 3);
    } 
    return formatter.getFormattedValues();
  }
  
  private String getPeerFirmwareRevision(DiameterPeerConfig peerConfig) {
    return (peerConfig.getPeerFirmwareRevison() == 0) ? "NONE" : String.valueOf(peerConfig.getPeerFirmwareRevison());
  }
  
  private String getPeerSupportedVendorSummary(DiameterBasePeerVendorTable[] vendorTable) {
    String vendors = "None";
    if (vendorTable.length > 0)
      vendors = vendorTable[0].getDbpPeerVendorId(); 
    for (int i = 1; i < vendorTable.length; i++)
      vendors = vendors + ", " + vendorTable[i].getDbpPeerVendorId(); 
    return vendors;
  }
  
  private String getPeerAppAdvSumary(Set<ApplicationEnum> appTable) {
    TableFormatter innerTable = new TableFormatter(new String[] { "Vendor-Id", "Application-Id", "Service-Type" }, new int[] { 18, 18, 18 }, 1);
    Iterator<ApplicationEnum> it = appTable.iterator();
    while (it.hasNext()) {
      ApplicationEnum applicationEnum = it.next();
      innerTable.addRecord(new String[] { String.valueOf(applicationEnum.getVendorId()), 
            String.valueOf(applicationEnum.getApplicationId()), 
            (applicationEnum.getApplicationType()).serviceTypeStr });
    } 
    return innerTable.getFormattedValues();
  }
  
  public String getDCCPeerConfig(String hostIdentity, DiameterPeerConfig peerConfig) {
    if (peerConfig == null)
      return "Peer: " + hostIdentity + " not registered."; 
    TableFormatter formatter = new TableFormatter(new String[] { hostIdentity }, new int[] { 60 }, 2);
    formatter.add("Peer Id            : " + peerConfig.getPeerId(), 0);
    formatter.add("Peer Index         : " + peerConfig.getDbpPeerIndex(), 0);
    formatter.add("Firmware Revision  : " + peerConfig.getPeerFirmwareRevison(), 0);
    DiameterBasePeerVendorTable[] vendorTable = peerConfig.getDbpPeerVendorTable();
    if (vendorTable != null && vendorTable.length > 0) {
      formatter.addNewLine();
      formatter.add("--Supported Vendors--", 3);
      formatter.add(getPeerSupportedVendorSummary(vendorTable));
    } else {
      formatter.add("--No Vendors registered--", 3);
    } 
    return formatter.getFormattedValues();
  }
  
  public String getAllDCCPeerConfigSummary() {
    StringBuilder summary = new StringBuilder();
    for (Map.Entry<String, DiameterPeerConfig> entry : this.peerConfigMap.entrySet())
      summary.append(getDCCPeerConfig(entry.getKey(), entry.getValue())); 
    return summary.toString();
  }
  
  public String getDCCPeerConfig(String hostIdentity) {
    return getDCCPeerConfig(hostIdentity, this.peerConfigMap.get(hostIdentity));
  }
  
  public void addDiameterPeer(DiameterPeer peer) {
    if (peer != null && peer.getHostIdentity() != null) {
      this.mibIndexRecorder.recordIndexFor(peer.getPeerData());
      this.peerConfigMap.put(peer.getHostIdentity(), peer.getPeerConfig());
      setChanged();
      notifyObservers(peer.getHostIdentity());
    } 
  }
  
  public Map<String, DiameterPeerConfig> getPeerConfigMap() {
    return this.peerConfigMap;
  }
  
  public void reload(Collection<DiameterPeer> peerList) {
    if (peerList != null)
      for (DiameterPeer peer : peerList) {
        if (peer == null || Strings.isNullOrBlank(peer.getHostIdentity()))
          continue; 
        if (!this.peerConfigMap.containsKey(peer.getHostIdentity()))
          addDiameterPeer(peer); 
      }  
  }
  
  public DiameterPeerConfig getPeerConfig(String hostIdentity) {
    return this.peerConfigMap.get(hostIdentity);
  }
  
  public DiameterPeerState getPeerState(String hostIdentity) {
    return DiameterPeerState.fromStateOrdinal(((DiameterPeerConfig)this.peerConfigMap.get(hostIdentity)).getPeerState());
  }
}
