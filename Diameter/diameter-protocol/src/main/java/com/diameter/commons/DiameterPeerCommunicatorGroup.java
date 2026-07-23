package com.diameter.commons;

public interface DiameterPeerCommunicatorGroup extends ESCommunicatorGroup<DiameterPeerCommunicator> {
  boolean assignGroup(DiameterPacket paramDiameterPacket);
  
  String getNextPeer();
  
  String getSecondaryPeer(DiameterPeerCommunicator... paramVarArgs);
}
