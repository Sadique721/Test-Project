package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public class RuleBasedPeerGroupSelector implements PeerCommunicatorGroupSelector {
  private static final String MODULE = "RULE-BSD-PEER-GRP-SELECTOR";
  
  private List<DiameterPeerCommunicatorGroup> communicatorGroups;
  
  private RouterContext diameterRouterContext;
  
  private List<String> peers;
  
  private List<PeerGroupImpl> peerGroupList;
  
  public RuleBasedPeerGroupSelector(List<PeerGroupImpl> peerGroupList, RouterContext diameterRouterContext) {
    this.peerGroupList = peerGroupList;
    this.communicatorGroups = new ArrayList<>();
    this.diameterRouterContext = diameterRouterContext;
    this.peers = new ArrayList<>();
  }
  
  public void init(boolean addlistener) throws InitializationFailedException {
    if (this.peerGroupList == null) {
      if (LogManager.getLogger().isDebugLogLevel())
        LogManager.getLogger().debug("RULE-BSD-PEER-GRP-SELECTOR", "No Peer Groups Found."); 
      return;
    } 
    for (PeerGroup peerGroup : this.peerGroupList) {
      String peerGroupAdvancedCondition = peerGroup.getAdvancedConditionStr();
      LogicalExpression logicalExpForPeergroup = null;
      if (peerGroupAdvancedCondition != null && peerGroupAdvancedCondition.trim().length() > 0) {
    	  throw new InitializationFailedException("peerGroupAdvancedCondition not supported");
      } 
      DiameterPeerCommunicatorGroupImpl diameterPeerCommunicatorGroupImpl = new DiameterPeerCommunicatorGroupImpl(logicalExpForPeergroup, LoadBalancerType.ROUND_ROBIN, addlistener);
      List<PeerInfoImpl> peerInfos = peerGroup.getPeerList();
      for (PeerInfo peerInfo : peerInfos) {
        DiameterPeerCommunicator peerCommunicator = this.diameterRouterContext.getPeerCommunicator(peerInfo.getPeerName());
        if (peerCommunicator != null) {
          diameterPeerCommunicatorGroupImpl.addCommunicator((DiameterPeerCommunicator)peerCommunicator, peerInfo.getLoadFactor());
          this.peers.add(peerCommunicator.getName());
        } 
      } 
      this.communicatorGroups.add(diameterPeerCommunicatorGroupImpl);
    } 
  }
  
  public DiameterPeerCommunicatorGroup select(DiameterRequest diameterRequest) {
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RULE-BSD-PEER-GRP-SELECTOR", "Fetching Rule-based Peer Group"); 
    for (int i = 0; i < this.communicatorGroups.size(); i++) {
      if (((DiameterPeerCommunicatorGroup)this.communicatorGroups.get(i)).assignGroup((DiameterPacket)diameterRequest))
        return this.communicatorGroups.get(i); 
    } 
    return null;
  }
  
  public List<String> peers() {
    return this.peers;
  }
}
