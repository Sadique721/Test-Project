package com.diameter.commons;

import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.diameter.stack.DiameterStack;

public interface IDiameterStackContext extends IStackContext {
  Set<ApplicationEnum> getApplicationsIdentifiersList();
  
  boolean validate();
  
  boolean isNAIEnabled();
  
  boolean isValidNAIRealm(String paramString);
  
  DiameterPeerState registerPeerStatusListener(String paramString, DiameterPeerStatusListener paramDiameterPeerStatusListener) throws StatusListenerRegistrationFailException;
  
  void addMDC(DiameterPacket paramDiameterPacket);
  
  boolean isEREnabled();
  
  int getTotalActiveSessionCount();
  
  boolean isOverLoad(DiameterRequest paramDiameterRequest);
  
  CDRDriver<DiameterPacket> getDiameterCDRDriver(String paramString) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;
  
  DiameterStatisticsProvider getDiameterStatisticsProvider();
  
  @Nonnegative
  long releasePeerSessions(@Nonnull DiameterRequest paramDiameterRequest);
  
  boolean isServerInitiatedMessage(int paramInt);
  
  void submitToWorker(DiameterStack.PacketProcess paramPacketProcess);
  
  int getMaxWorkerThreads();
  
  VirtualConnectionHandler registerVirtualPeer(PeerData paramPeerData, VirtualOutputStream paramVirtualOutputStream) throws ElementRegistrationFailedException;
  
  void clearMDC();
}
