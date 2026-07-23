package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public class ChainPeerGroupSelector implements PeerCommunicatorGroupSelector {
  private List<PeerCommunicatorGroupSelector> peerCommunicatorGroupSelectors = new ArrayList<>();
  
  private List<String> peers = new ArrayList<>();
  
  public void add(PeerCommunicatorGroupSelector peerCommunicatorGroupSelector) {
    if (peerCommunicatorGroupSelector != null)
      this.peerCommunicatorGroupSelectors.add(peerCommunicatorGroupSelector); 
  }
  
  public DiameterPeerCommunicatorGroup select(DiameterRequest diameterRequest) {
    for (int i = 0; i < this.peerCommunicatorGroupSelectors.size(); i++) {
      DiameterPeerCommunicatorGroup peerGroup = ((PeerCommunicatorGroupSelector)this.peerCommunicatorGroupSelectors.get(i)).select(diameterRequest);
      if (peerGroup != null)
        return peerGroup; 
    } 
    return null;
  }
  
  public void init(boolean addPeerListner) throws InitializationFailedException {
    for (int i = 0; i < this.peerCommunicatorGroupSelectors.size(); i++) {
      ((PeerCommunicatorGroupSelector)this.peerCommunicatorGroupSelectors.get(i)).init(addPeerListner);
      this.peers.addAll(((PeerCommunicatorGroupSelector)this.peerCommunicatorGroupSelectors.get(i)).peers());
    } 
  }
  
  public List<String> peers() {
    return this.peers;
  }
}
