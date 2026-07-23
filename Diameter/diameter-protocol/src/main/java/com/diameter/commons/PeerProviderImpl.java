package com.diameter.commons;

import java.util.Collection;

public class PeerProviderImpl implements PeerProvider {
  DiameterPeersTable diameterPeersTable;
  
  public PeerProviderImpl(DiameterPeersTable diameterPeersTable) {
    this.diameterPeersTable = diameterPeersTable;
  }
  
  public Collection<DiameterPeer> getPeerList() {
    return this.diameterPeersTable.getPeerList();
  }
  
  public DiameterPeer getPeer(String strPeerHostIdentity) {
    return this.diameterPeersTable.getPeer(strPeerHostIdentity);
  }
  
  public DiameterPeer getPeerByName(String peerName) {
    return this.diameterPeersTable.getPeerByName(peerName);
  }
  
  public PeerData getPeerData(String strPeerHostIdentity) {
    return this.diameterPeersTable.getPeerData(strPeerHostIdentity);
  }
}
