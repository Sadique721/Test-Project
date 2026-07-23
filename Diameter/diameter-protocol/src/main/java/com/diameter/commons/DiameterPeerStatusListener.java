package com.diameter.commons;

public interface DiameterPeerStatusListener {
  void markOpen();
  
  void markClosed();
}
