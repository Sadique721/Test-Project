package com.diameter.commons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DiameterPeerCommunicator extends ESCommunicator {
  void sendClientInitiatedRequest(@Nonnull DiameterSession paramDiameterSession, @Nonnull DiameterRequest paramDiameterRequest, @Nonnull ResponseListener paramResponseListener) throws CommunicationException;
  
  void sendServerInitiatedRequest(@Nonnull DiameterSession paramDiameterSession, @Nonnull DiameterRequest paramDiameterRequest, @Nonnull ResponseListener paramResponseListener) throws CommunicationException;
  
  void sendAnswer(DiameterRequest paramDiameterRequest, DiameterAnswer paramDiameterAnswer) throws CommunicationException;
  
  @Nullable
  String getHostIdentity();
}
