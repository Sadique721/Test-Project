package com.diameter.commons;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiameterPeersTable {
  private Map<String, DiameterPeer> peerMap;
  
  private Map<String, DiameterPeer> peerMapByName;
  
  private final String MODULE = "DIAMETER-PEERS-TABLE";
  
  public DiameterPeersTable() {
    this.peerMap = new LinkedHashMap<>(1, 1.0F);
    this.peerMapByName = new LinkedHashMap<>(1, 1.0F);
  }
  
  public Collection<DiameterPeer> getPeerList() {
    if (this.peerMap != null)
      return this.peerMap.values(); 
    return null;
  }
  
  public DiameterPeer getPeer(String strPeerHostIdentity) {
    if (strPeerHostIdentity != null)
      return this.peerMap.get(strPeerHostIdentity); 
    return null;
  }
  
  public DiameterPeer getPeerByName(String peerName) {
    return this.peerMapByName.get(peerName);
  }
  
  public PeerData getPeerData(String strPeerHostIdentity) {
    DiameterPeer peer = getPeerByName(strPeerHostIdentity);
    if (peer != null)
      return peer.getPeerData(); 
    peer = getPeer(strPeerHostIdentity);
    if (peer != null)
      return peer.getPeerData(); 
    return null;
  }
  
  public int getPeerState(String strPeerHostIdentity) {
    DiameterPeer peer = this.peerMap.get(strPeerHostIdentity);
    if (peer != null)
      return peer.getPeerState(); 
    return 0;
  }
  
  public Map<String, IStateEnum> getPeersState() {
    Map<DiameterPeer, String> tempDiameterPeerMap = new HashMap<>();
    for (Map.Entry<String, DiameterPeer> entry : this.peerMap.entrySet())
      tempDiameterPeerMap.put(entry.getValue(), entry.getKey()); 
    Map<String, IStateEnum> peersStateMap = new HashMap<>();
    for (Map.Entry<DiameterPeer, String> entry : tempDiameterPeerMap.entrySet()) {
      if (((DiameterPeer)entry.getKey()).getHostIdentity() != null && 
        !((DiameterPeer)entry.getKey()).getHostIdentity().isEmpty()) {
        peersStateMap.put(((DiameterPeer)entry.getKey()).getHostIdentity(), ((DiameterPeer)entry.getKey()).currentState());
        continue;
      } 
      peersStateMap.put(entry.getValue(), ((DiameterPeer)entry.getKey()).currentState());
    } 
    return peersStateMap;
  }
  
  public void addPeer(DiameterPeer diameterPeer) {
    if (diameterPeer.getHostIdentity() != null && diameterPeer.getHostIdentity().trim().length() > 0)
      this.peerMap.put(diameterPeer.getHostIdentity(), diameterPeer); 
    if (diameterPeer.getRemoteInetAddress() != null && diameterPeer.getRemoteInetAddress().toString().trim().length() > 0 && 
      this.peerMap.put(diameterPeer.getRemoteInetAddress().getHostAddress(), diameterPeer) != null && 
      LogManager.getLogger().isLogLevel(LogLevel.TRACE))
      LogManager.getLogger().trace("DIAMETER-PEERS-TABLE", "Map Already contains Key :" + diameterPeer.getRemoteInetAddress().getHostAddress() + ", so Overriding value"); 
    if (diameterPeer.getPeerName() != null && diameterPeer.getPeerName().trim().length() > 0 && 
      this.peerMapByName.put(diameterPeer.getPeerName(), diameterPeer) != null && 
      LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DIAMETER-PEERS-TABLE", "Map already contains Key: " + diameterPeer.getPeerName() + ", so Overriding value"); 
  }
}