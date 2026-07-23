package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public class FailoverFailureAction implements RoutingFailureAction {
  private static final String MODULE = "FAILOVR-FLR-ACT";
  
  private static final int SECONDARY_PEER = 0;
  
  private static final int NEXT_PEER = 1;
  
  private static final int SPECIFIC_PEER = 2;
  
  private String failureArgs;
  
  private long transactionTimeout = 3000L;
  
  private RouterContext routerContext;
  
  private PeerSelector peerSelector;
  
  private int peerCriteria;
  
  private List<String> warnings;
  
  public FailoverFailureAction(RouterContext routerContext, String failureArgs, long transactionTimeout, PeerSelector peerSelector) {
    this.failureArgs = failureArgs;
    this.transactionTimeout = transactionTimeout;
    this.routerContext = routerContext;
    this.peerSelector = peerSelector;
    this.warnings = new ArrayList<>();
  }
  
  public void init() {
    if (this.failureArgs == null || this.failureArgs.trim().length() == 0) {
      this.warnings.add("No Diameter Peer found for " + DiameterFailureConstants.FAILOVER + " Failure Action");
      return;
    } 
    this.failureArgs = this.failureArgs.trim();
    if ("0.0.0.0".equals(this.failureArgs)) {
      this.peerCriteria = 0;
    } else if ("255.255.255.255".equals(this.failureArgs)) {
      this.peerCriteria = 1;
    } else {
      DiameterPeerCommunicator peerComm = this.routerContext.getPeerCommunicator(this.failureArgs);
      if (peerComm == null && 
        LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("FAILOVR-FLR-ACT", "Diameter Peer: " + this.failureArgs + " is not registered for " + DiameterFailureConstants.FAILOVER + " Failure Action"); 
      this.peerCriteria = 2;
    } 
  }
  
  public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession, DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity, String originPeerName) {
    String sessionId = failureAnswer.getAVPValue("0:263");
    int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("FAILOVR-FLR-ACT", "Performing " + DiameterFailureConstants.FAILOVER + " Failure Action with Failure Argument " + this.failureArgs + " for Session-ID=" + sessionId + " HbH-ID=" + hopByHopKey); 
    FailureActionResult failureActionResult = null;
    long endToEndTime = System.currentTimeMillis() - originRequest.creationTimeMillis();
    if (endToEndTime > this.transactionTimeout) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("FAILOVR-FLR-ACT", "Transaction timeout has exceeded for Session-ID=" + sessionId + ", Sending DIAMETER_UNABLE_TO_DELIVER"); 
      DiameterAnswer answer = new DiameterAnswer(originRequest, ResultCode.DIAMETER_UNABLE_TO_DELIVER);
      failureActionResult = new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, (DiameterPacket)answer);
      return failureActionResult;
    } 
    remoteRequest.addFailedPeer(this.routerContext.getPeerData(remotePeerHostIdentity).getPeerName());
    List<String> failedPeerList = remoteRequest.getFailedPeerList();
    if (failedPeerList == null)
      failedPeerList = new ArrayList<>(0); 
    String destPeerName = getNextFailoverPeer(originRequest, failedPeerList);
    if (destPeerName == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("FAILOVR-FLR-ACT", "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + ", Reason: All peers in the Peer Group are Exhausted for Session-ID=" + sessionId); 
      DiameterAnswer answer = new DiameterAnswer(originRequest, ResultCode.DIAMETER_UNABLE_TO_DELIVER);
      failureActionResult = new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, (DiameterPacket)answer);
      return failureActionResult;
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("FAILOVR-FLR-ACT", "Sending request to: " + destPeerName); 
    PeerData peerData = this.routerContext.getPeerData(destPeerName);
    String destHost = remoteRequest.getAVPValue("0:293");
    if (destHost != null && destHost.equals(remotePeerHostIdentity))
      remoteRequest.getAVP("0:293").setStringValue(peerData.getHostIdentity()); 
    failureActionResult = new FailureActionResult(FailureActionResultCodes.SEND_REQUEST_TO_PEER, (DiameterPacket)remoteRequest, destPeerName);
    return failureActionResult;
  }
  
  private String getNextFailoverPeer(DiameterRequest originRequest, List<String> failedPeerList) {
    DiameterPeerCommunicator peerCommunicator;
    String peerName = null;
    switch (this.peerCriteria) {
      case 0:
        return this.peerSelector.selectSecondaryPeer(originRequest, failedPeerList
            .<String>toArray(new String[failedPeerList.size()]));
      case 1:
        peerName = this.peerSelector.selectNextPeer(originRequest);
        if (peerName != null && !failedPeerList.contains(peerName))
          return peerName; 
        return this.peerSelector.selectSecondaryPeer(originRequest, failedPeerList
            .<String>toArray(new String[failedPeerList.size()]));
      case 2:
        peerCommunicator = this.routerContext.getPeerCommunicator(this.failureArgs);
        if (peerCommunicator != null && peerCommunicator.isAlive()) {
          peerName = peerCommunicator.getName();
          if (!failedPeerList.contains(peerName))
            return peerName; 
        } 
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("FAILOVR-FLR-ACT", "Configured Host " + this.failureArgs + " is not Available."); 
        break;
    } 
    return null;
  }
  
  public List<String> getWarnings() {
    return this.warnings;
  }
}
