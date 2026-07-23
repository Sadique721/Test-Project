package com.diameter.commons;

import javax.annotation.Nonnull;

public class PeerSelector {
  private RouterContext diameterRouterContext;
  
  private PeerCommunicatorGroupSelector peerCommunicatorGroupSelector;
  
  public PeerSelector(@Nonnull PeerCommunicatorGroupSelector peerCommunicatorGroupSelector, @Nonnull RouterContext diameterRouterContext) {
    this.peerCommunicatorGroupSelector = peerCommunicatorGroupSelector;
    this.diameterRouterContext = diameterRouterContext;
  }
  
  public boolean isKnown(String hostIdentity) {
    PeerData peerData = this.diameterRouterContext.getPeerData(hostIdentity);
    if (peerData != null)
      return this.peerCommunicatorGroupSelector.peers().contains(peerData.getPeerName()); 
    return true;
  }
  
  private DiameterPeerCommunicatorGroup selectCommunicatorGroup(DiameterRequest diameterRequest) {
    return this.peerCommunicatorGroupSelector.select(diameterRequest);
  }
  
  public String selectNextPeer(DiameterRequest diameterRequest) {
    DiameterPeerCommunicatorGroup communicatorGroup = selectCommunicatorGroup(diameterRequest);
    if (communicatorGroup != null)
      return communicatorGroup.getNextPeer(); 
    return null;
  }
  
  public String selectSecondaryPeer(DiameterRequest diameterRequest, String... ignorePeers) {
    DiameterPeerCommunicatorGroup peerCommGroup = selectCommunicatorGroup(diameterRequest);
    if (peerCommGroup != null) {
      DiameterPeerCommunicator[] tempIgnoreCommList = new DiameterPeerCommunicator[ignorePeers.length];
      int size = 0;
      for (int i = 0; i < ignorePeers.length; i++) {
        if (ignorePeers[i] != null) {
          DiameterPeerCommunicator peerCommunicator = this.diameterRouterContext.getPeerCommunicator(ignorePeers[i]);
          if (peerCommunicator != null) {
            tempIgnoreCommList[i] = peerCommunicator;
            size++;
          } 
        } 
      } 
      DiameterPeerCommunicator[] ignoreCommunicatorList = new DiameterPeerCommunicator[size];
      System.arraycopy(tempIgnoreCommList, 0, ignoreCommunicatorList, 0, size);
      return peerCommGroup.getSecondaryPeer(ignoreCommunicatorList);
    } 
    return null;
  }
}
