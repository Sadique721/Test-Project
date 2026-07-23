package com.diameter.commons;

import java.util.Collection;

public interface PeerProvider {
  Collection<DiameterPeer> getPeerList();
  
  DiameterPeer getPeer(String paramString);
  
  DiameterPeer getPeerByName(String paramString);
  
  PeerData getPeerData(String paramString);
}
