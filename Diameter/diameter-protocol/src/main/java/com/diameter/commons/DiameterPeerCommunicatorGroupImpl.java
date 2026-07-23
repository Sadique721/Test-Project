package com.diameter.commons;

public class DiameterPeerCommunicatorGroupImpl extends ESCommunicatorGroupImpl<DiameterPeerCommunicator> implements DiameterPeerCommunicatorGroup {
  private LogicalExpression ruleSet;
  
  public DiameterPeerCommunicatorGroupImpl(LogicalExpression ruleSet, LoadBalancerType loadBalancerType) {
    super(loadBalancerType);
    this.ruleSet = ruleSet;
  }
  
  public DiameterPeerCommunicatorGroupImpl(LogicalExpression ruleSet, LoadBalancerType loadBalancerType, boolean checkAlive) {
    super(loadBalancerType, checkAlive);
    this.ruleSet = ruleSet;
  }
  
  public boolean assignGroup(DiameterPacket diameterPacket) {
    DiameterAVPValueProvider diameterAVPValueProvider = new DiameterAVPValueProvider(diameterPacket);
    if (this.ruleSet != null)
      return this.ruleSet.evaluate((ValueProvider)diameterAVPValueProvider); 
    return true;
  }
  
  public String getNextPeer() {
    DiameterPeerCommunicator communicator = (DiameterPeerCommunicator)getCommunicator();
    if (communicator != null)
      return communicator.getName(); 
    return null;
  }
  
  public String getSecondaryPeer(DiameterPeerCommunicator... ignoreCommunicatorList) {
    DiameterPeerCommunicator secondaryCommunicator = (DiameterPeerCommunicator)getSecondaryCommunicator((DiameterPeerCommunicator[])ignoreCommunicatorList);
    if (secondaryCommunicator != null)
      return secondaryCommunicator.getName(); 
    return null;
  }
}
