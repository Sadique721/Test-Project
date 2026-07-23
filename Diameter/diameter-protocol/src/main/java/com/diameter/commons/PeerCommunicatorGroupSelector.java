package com.diameter.commons;

import java.util.List;

import javax.annotation.Nonnull;

public interface PeerCommunicatorGroupSelector {
  void init(boolean paramBoolean) throws InitializationFailedException;
  
  @Nonnull
  DiameterPeerCommunicatorGroup select(DiameterRequest paramDiameterRequest);
  
  @Nonnull
  List<String> peers();
}
