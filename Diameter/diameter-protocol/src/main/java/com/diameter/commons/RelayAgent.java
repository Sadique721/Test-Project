package com.diameter.commons;

import java.util.List;

public class RelayAgent extends DiameterAgent {
  private static final String MODULE = "RELAY-AGNT";
  
  protected SessionReleaseIndiactor sessionReleaseIndiactor;
  
  private IDiameterSessionManager diameterSessionManager;
  
  public RelayAgent(RouterContext routerContext, IDiameterSessionManager diameterSessionManager) {
    super(routerContext);
    this.sessionReleaseIndiactor = (SessionReleaseIndiactor)new AppDefaultSessionReleaseIndicator();
    this.diameterSessionManager = diameterSessionManager;
  }
  
  public void routeRequest(DiameterRequest originRequest, DiameterSession diameterSession, RoutingEntry routingEntry) throws RoutingFailedException {
    String destPeerName, sessionId = diameterSession.getSessionId();
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RELAY-AGNT", "Routing initiated for Diameter Request with Session-Id = " + sessionId); 
    diameterSession.setParameter("0:264", originRequest.getRequestingHost());
    diameterSession.setParameter("ROUTING_ENTRY", routingEntry.getRoutingEntryName());
    locateSession(originRequest);
    DiameterRequest destinationRequest = buildRequest(diameterSession, originRequest, routingEntry);
    if (destinationRequest.getAVPValue("0:263") == null) {
      IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("0:263");
      diameterAVP.setStringValue(sessionId);
      destinationRequest.addAvp(diameterAVP);
    } 
    if (destinationRequest.getParameter("DUMMY_MAPPING") != null) {
      destPeerName = this.routerContext.getVirtualRoutingPeerName();
    } else {
      destPeerName = selectDestinationPeer(originRequest, routingEntry, diameterSession);
    } 
    if (destPeerName == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("RELAY-AGNT", "Doing failover Sending " + ResultCode.DIAMETER_PEER_NOT_FOUND + ", Reason: Destination Peer not found for Session-Id=" + sessionId); 
      DiameterAnswer diameterAnswer = new DiameterAnswer(originRequest, ResultCode.DIAMETER_PEER_NOT_FOUND);
      routeAnswer(diameterSession, originRequest, destinationRequest, diameterAnswer, routingEntry, (String)null);
    } else {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("RELAY-AGNT", "Peer: " + destPeerName + " selected for Session-ID=" + sessionId); 
      diameterSession.setParameter("0:293", destPeerName);
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("RELAY-AGNT", "Routing Session updated for Session-ID=" + sessionId); 
      addRouteRecoredAVP(originRequest, destinationRequest);
      postRequestProcessing(destinationRequest);
      finalRequestProcessing(diameterSession, originRequest, destinationRequest, destPeerName, routingEntry);
      try {
        sendClientInitiatedRequest(diameterSession, destinationRequest, new RelayAgentResponseListener(routingEntry, originRequest, destinationRequest), destPeerName, routingEntry
            .getRoutingAction());
      } catch (CommunicationException e) {
        LogManager.getLogger().trace("RELAY-AGNT", (Throwable)e);
        throw new RoutingFailedException(ResultCode.DIAMETER_PEER_NOT_FOUND, routingEntry.getRoutingAction(), e.getMessage());
      } 
    } 
  }
  
  protected void postRequestProcessing(DiameterRequest destinationRequest) {}
  
  protected String selectDestinationPeer(DiameterRequest originRequest, RoutingEntry routingEntry, DiameterSession routingSession) {
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RELAY-AGNT", "Selecting Destination Peer for Session-Id=" + originRequest
          .getAVPValue("0:263")); 
    DiameterPeerCommunicator destPeerComm = null;
    if (routingEntry.isStatefulRoutingEnabled()) {
      String str = (String)routingSession.getParameter("0:293");
      if (str != null) {
        destPeerComm = this.routerContext.getPeerCommunicator(str);
        if (destPeerComm.isAlive()) {
          if (LogManager.getLogger().isDebugLogLevel())
            LogManager.getLogger().debug("RELAY-AGNT", "Session's Destination host: " + str + " selected for Session-ID = " + originRequest
                
                .getAVPValue("0:263")); 
          return destPeerComm.getName();
        } 
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("RELAY-AGNT", "Peer: " + str + " for Session-ID = " + originRequest
              .getAVPValue("0:263") + " is Down."); 
        return null;
      } 
    } 
    String destHostId = originRequest.getAVPValue("0:293");
    if (destHostId != null) {
      destPeerComm = this.routerContext.getPeerCommunicator(destHostId);
      if (destPeerComm != null) {
        if (destPeerComm.isAlive()) {
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("RELAY-AGNT", "Packet's Destination host: " + destHostId + " selected for Session-ID=" + originRequest
                
                .getAVPValue("0:263")); 
          return destPeerComm.getName();
        } 
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("RELAY-AGNT", "Peer: " + destHostId + " for Session-ID=" + originRequest
              .getAVPValue("0:263") + " is Down."); 
        return null;
      } 
    } 
    PeerSelector peerSelector = routingEntry.getPeerSelector();
    if (peerSelector != null)
      return peerSelector.selectNextPeer(originRequest); 
    return null;
  }
  
  private class RelayAgentResponseListener implements ResponseListener {
    private RoutingEntry routingEntry;
    
    private DiameterRequest originRequest;
    
    private DiameterRequest destinationRequest;
    
    public RelayAgentResponseListener(RoutingEntry routingEntry, DiameterRequest originRequest, DiameterRequest destinationRequest) {
      this.routingEntry = routingEntry;
      this.originRequest = originRequest;
      this.destinationRequest = destinationRequest;
    }
    
    public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession diameterSession) {
      RelayAgent.this.routerContext.updateRealmInputStatistics((DiameterPacket)diameterAnswer, RelayAgent.this.routerContext
          .getPeerData(hostIdentity).getRealmName(), this.routingEntry
          .getRoutingAction());
      RelayAgent.this.routeAnswer(diameterSession, this.originRequest, this.destinationRequest, diameterAnswer, this.routingEntry, hostIdentity);
    }
    
    public void requestTimedout(String hostIdentity, DiameterSession diameterSession) {
      if (LogManager.getLogger().isDebugLogLevel())
        LogManager.getLogger().debug("RELAY-AGNT", "Request timeout response received from peer: " + hostIdentity); 
      DiameterAnswer diameterAnswer = new DiameterAnswer(this.destinationRequest, ResultCode.DIAMETER_REQUEST_TIMEOUT);
      DiameterUtility.addOrReplaceAvp("0:281", (DiameterPacket)diameterAnswer, "Request Timeout");
      RelayAgent.this.routerContext.updateRealmTimeoutRequestStatistics(this.destinationRequest, RelayAgent.this.routerContext
          .getPeerData(hostIdentity).getRealmName(), this.routingEntry
          .getRoutingAction());
      RelayAgent.this.routeAnswer(diameterSession, this.originRequest, this.destinationRequest, diameterAnswer, this.routingEntry, hostIdentity);
    }
  }
  
  protected DiameterRequest buildRequest(DiameterSession diameterSession, DiameterRequest originRequest, RoutingEntry routingEntry) throws RoutingFailedException {
    try {
      DiameterRequest destReq = (DiameterRequest)originRequest.clone();
      destReq.setHop_by_hopIdentifier(HopByHopPool.get());
      destReq.touch();
      return destReq;
    } catch (CloneNotSupportedException e) {
      LogManager.getLogger().trace("RELAY-AGNT", e);
      throw new AssertionError(e);
    } 
  }
  
  private void addRouteRecoredAVP(DiameterRequest originalRequest, DiameterRequest destinationRequest) {
    String requesterHostId = originalRequest.getRequestingHost();
    if (requesterHostId != null && requesterHostId.trim().length() > 0 && 
      !requesterHostId.equalsIgnoreCase(Parameter.getInstance().getOwnDiameterIdentity())) {
      IDiameterAVP routeRecord = DiameterDictionary.getInstance().getAttribute("0:282");
      routeRecord.setStringValue(requesterHostId);
      destinationRequest.addAvp(routeRecord);
      if (LogManager.getLogger().isDebugLogLevel())
        LogManager.getLogger().debug("RELAY-AGNT", "Added Route-Record-AVP 0:282 with value: " + requesterHostId + " in remote request."); 
    } 
  }
  
  private void finalRequestProcessing(DiameterSession routingSession, DiameterRequest originRequest, DiameterRequest destinationRequest, String remoteHost, RoutingEntry routingEntry) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("RELAY-AGNT", "Final Request Processing for Session-Id=" + originRequest
          .getAVPValue("0:263")); 
    this.routerContext.postRequestRouting(originRequest, destinationRequest, originRequest
        .getRequestingHost(), remoteHost, routingEntry
        .getRoutingEntryName());
    if (routingEntry.isStatefulRoutingEnabled())
      routingSession.setParameter("0:293", remoteHost); 
  }
  
  private void finalServerInitiatedRequestProcessing(DiameterSession session, DiameterRequest originRequest, DiameterRequest destinationRequest, RoutingEntry routingEntry) {
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RELAY-AGNT", "Final Request Processing for Session-Id=" + originRequest
          .getAVPValue("0:263")); 
    destinationRequest.setHop_by_hopIdentifier(HopByHopPool.get());
    this.routerContext.postRequestRouting(originRequest, destinationRequest, originRequest
        
        .getRequestingHost(), (String)session
        .getParameter("0:264"), routingEntry
        .getRoutingEntryName());
  }
  
  private void routeAnswer(DiameterSession session, DiameterRequest originalRequest, DiameterRequest translatedRequest, DiameterAnswer remoteAnswer, RoutingEntry routingEntry, String hostIdentity) {
    String sessionId = session.getSessionId();
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("RELAY-AGNT", "Routing initiated for Diameter Answer with Session-ID=" + sessionId); 
    this.routerContext.preAnswerRouting(originalRequest, translatedRequest, remoteAnswer, hostIdentity, routingEntry
        .getRoutingEntryName());
    IDiameterAVP resultCodeAVP = remoteAnswer.getAVP("0:268");
    int resultCode = 0;
    if (resultCodeAVP != null)
      resultCode = (int)resultCodeAVP.getInteger(); 
    if (!(ResultCodeCategory.getResultCodeCategory(resultCode)).isFailureCategory || routingEntry
      .getFailureAction(resultCode) == null) {
      DiameterAnswer translatedAnswer = buildAnswer(originalRequest, remoteAnswer, routingEntry, session, translatedRequest);
      sendAnswerInternal(originalRequest, translatedRequest, remoteAnswer, routingEntry, session, translatedAnswer, hostIdentity);
    } else {
      RoutingFailureAction failureAction = routingEntry.getFailureAction(resultCode);
      FailureActionResult failureActionResult = failureAction.process(remoteAnswer, session, originalRequest, translatedRequest, hostIdentity, originalRequest.getRequestingHost());
      switch (failureActionResult.getAction()) {
        case DROP:
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("RELAY-AGNT", "Dropping Diameter Answer with HbH-ID=" + remoteAnswer.getHop_by_hopIdentifier()); 
          releaseSession(originalRequest, new DiameterAnswer(originalRequest), session);
          this.routerContext.updateDiameterStatsPacketDroppedStatistics((DiameterPacket)remoteAnswer, originalRequest
              
              .getRequestingHost(), originalRequest
              .getPeerData().getRealmName(), routingEntry
              .getRoutingAction());
          return;
        case SEND_REQUEST_TO_PEER:
          actionOnSendRequestToPeer(remoteAnswer, session, failureActionResult, translatedRequest, originalRequest, routingEntry, hostIdentity);
          break;
        case SEND_ANSWER_TO_ORIGINATOR:
          sendAnswerInternal(originalRequest, translatedRequest, remoteAnswer, routingEntry, session, failureActionResult.getDiameterPacket().getAsDiameterAnswer(), hostIdentity);
          break;
      } 
    } 
  }
  
  private void sendAnswerInternal(DiameterRequest originRequest, DiameterRequest remoteRequest, DiameterAnswer remoteAnswer, RoutingEntry routingEntry, DiameterSession diameterSession, DiameterAnswer finalPacket, String remoteHost) {
    postAnswerProcessing(finalPacket);
    boolean releaseSession = this.sessionReleaseIndiactor.isEligible((DiameterPacket)finalPacket);
    if (!releaseSession)
      updateOrSaveSession(originRequest, finalPacket); 
    finalAnswerProcessing(originRequest, remoteAnswer, finalPacket.getAsDiameterAnswer(), diameterSession, remoteRequest, remoteHost, routingEntry);
    try {
      sendAnswer((Session)diameterSession, originRequest, finalPacket, originRequest.getRequestingHost(), routingEntry.getRoutingAction());
    } catch (CommunicationException e) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("RELAY-AGNT", "Dropping Diameter Answer with Session-ID=" + diameterSession
            .getSessionId() + ", Reason: " + e.getMessage()); 
      LogManager.getLogger().trace("RELAY-AGNT", (Throwable)e);
      this.routerContext.updateDiameterStatsPacketDroppedStatistics((DiameterPacket)finalPacket, originRequest
          .getRequestingHost(), originRequest
          .getPeerData().getRealmName(), routingEntry
          .getRoutingAction());
    } 
    if (releaseSession)
      releaseSession(originRequest, finalPacket, diameterSession); 
  }
  
  private void actionOnSendRequestToPeer(DiameterAnswer remoteAnswer, DiameterSession diameterSession, FailureActionResult failureActionResult, DiameterRequest destinationRequest, DiameterRequest originalRequest, RoutingEntry routingEntry, String remoteHost) {
    DiameterPacket finalPacket = failureActionResult.getDiameterPacket();
    String destPeerName = failureActionResult.getPeerName();
    diameterSession.setParameter("0:293", destPeerName);
    postRequestProcessing(destinationRequest);
    finalRequestProcessing(diameterSession, originalRequest, (DiameterRequest)finalPacket, destPeerName, routingEntry);
    try {
      sendClientInitiatedRequest(diameterSession, finalPacket.getAsDiameterRequest(), new RelayAgentResponseListener(routingEntry, originalRequest, destinationRequest), destPeerName, routingEntry
          
          .getRoutingAction());
    } catch (CommunicationException e) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("RELAY-AGNT", "Not performing Failover for Diameter Answer with Session-ID=" + diameterSession
            .getSessionId() + ", Reason: " + e.getMessage()); 
      LogManager.getLogger().trace("RELAY-AGNT", (Throwable)e);
      sendAnswerInternal(originalRequest, destinationRequest, remoteAnswer, routingEntry, diameterSession, remoteAnswer, remoteHost);
    } 
  }
  
  protected void postAnswerProcessing(DiameterAnswer finalPacket) {}
  
  private boolean isFollowRedirection(int resultCode, DiameterAnswer diameterAnswer, DiameterSession routingSession) {
    if (resultCode != ResultCode.DIAMETER_REDIRECT_INDICATION.code)
      return false; 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("RELAY-AGNT", ResultCode.DIAMETER_REDIRECT_INDICATION + " received. Checking Redirect behaviour for Session-ID=" + diameterAnswer
          
          .getAVPValue("0:263")); 
    String hostIdentity = diameterAnswer.getAVPValue("0:264");
    PeerData peerData = this.routerContext.getPeerData(hostIdentity);
    if (peerData == null) {
      String destinationHost = (String)routingSession.getParameter("0:293");
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("RELAY-AGNT", "Peer configuration for Peer: " + hostIdentity + " not found, Obtaining Peer Configuration for Peer: " + destinationHost); 
      peerData = this.routerContext.getPeerData(destinationHost);
    } 
    if (peerData == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("RELAY-AGNT", "Peer configuration for Peer: " + hostIdentity + " not found, Not following Redirect indication"); 
      return false;
    } 
    if (!peerData.isFollowRedirection()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("RELAY-AGNT", "Follow Redirection for Peer: " + hostIdentity + " is disabled"); 
      return false;
    } 
    return true;
  }
  
  protected DiameterAnswer buildAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer, RoutingEntry routingEntry, DiameterSession routingSession, DiameterRequest translatedRequest) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("RELAY-AGNT", "Building Diameter Answer for Session-Id=" + diameterAnswer
          .getAVPValue("0:263")); 
    return diameterAnswer;
  }
  
  private void finalAnswerProcessing(DiameterRequest originRequest, DiameterAnswer originAnwser, DiameterAnswer diameterAnswer, DiameterSession routingSession, DiameterRequest translatedReqeuest, String remoteHost, RoutingEntry routingEntry) {
    diameterAnswer.setHop_by_hopIdentifier(originRequest.getHop_by_hopIdentifier());
    diameterAnswer.setRequestReceivedTime(originRequest.creationTimeMillis());
    IDiameterAVP sessionIdAVP = diameterAnswer.getAVP("0:263");
    String originSessionID = originRequest.getAVPValue("0:263");
    if (sessionIdAVP == null) {
      sessionIdAVP = DiameterDictionary.getInstance().getAttribute("0:263");
      sessionIdAVP.setStringValue(originSessionID);
      diameterAnswer.addAvp(sessionIdAVP);
    } else {
      sessionIdAVP.setStringValue(originSessionID);
    } 
    this.routerContext.postAnswerRouting(originRequest, translatedReqeuest, originAnwser, diameterAnswer, remoteHost, originRequest
        
        .getRequestingHost(), routingEntry
        .getRoutingEntryName());
  }
  
  private void finalServerInitiatedAnswerProcessing(DiameterRequest originRequest, DiameterAnswer originAnwser, DiameterAnswer diameterAnswer, DiameterSession routingSession, DiameterRequest destinationRequest, RoutingEntry routingEntry) {
    diameterAnswer.setHop_by_hopIdentifier(originRequest.getHop_by_hopIdentifier());
    diameterAnswer.setRequestReceivedTime(originRequest.creationTimeMillis());
    IDiameterAVP sessionIdAVP = diameterAnswer.getAVP("0:263");
    String originSessionID = originRequest.getAVPValue("0:263");
    if (sessionIdAVP == null) {
      sessionIdAVP = DiameterDictionary.getInstance().getAttribute("0:263");
      sessionIdAVP.setStringValue(originSessionID);
      diameterAnswer.addAvp(sessionIdAVP);
    } else {
      sessionIdAVP.setStringValue(originSessionID);
    } 
    this.routerContext.postAnswerRouting(originRequest, destinationRequest, originAnwser, diameterAnswer, (String)routingSession
        
        .getParameter("0:264"), originRequest
        .getRequestingHost(), routingEntry
        .getRoutingEntryName());
  }
  
  public void routeServerInitiatedRequest(final DiameterRequest originRequest, final DiameterSession session) throws RoutingFailedException {
    String destHost, sessionId = session.getSessionId();
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RELAY-AGNT", "Routing initiated for Server Initiated Request with Session-Id=" + sessionId); 
    locateSession(originRequest);
    final RoutingEntry routingEntry = this.routerContext.getRoutingEntry((String)session.getParameter("ROUTING_ENTRY"));
    final DiameterRequest destinationRequest = buildRequest((DiameterSession)null, originRequest, routingEntry);
    if (destinationRequest.getAVPValue("0:263") == null) {
      IDiameterAVP diameterAVP = DiameterUtility.createAvp("0:263", sessionId);
      destinationRequest.addAvp(diameterAVP);
    } 
    if (destinationRequest.getParameter("DUMMY_MAPPING") != null) {
      destHost = this.routerContext.getVirtualRoutingPeerName();
    } else {
      destHost = (String)session.getParameter("0:264");
      if (destHost == null)
        throw new RoutingFailedException(ResultCode.DIAMETER_PEER_NOT_FOUND, routingEntry
            .getRoutingAction(), "Destination Peer not found"); 
      DiameterPeerCommunicator destPeerComm = this.routerContext.getPeerCommunicator(destHost);
      if (!destPeerComm.isAlive()) {
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("RELAY-AGNT", "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + " for Session-Id=" + sessionId + ", Reason: Peer: " + destHost + " is Down."); 
        throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER, routingEntry
            .getRoutingAction(), "Destination Peer not found");
      } 
      if (LogManager.getLogger().isInfoLogLevel())
        LogManager.getLogger().info("RELAY-AGNT", "Session's Destination host: " + destHost + " selected for Session-ID=" + sessionId); 
    } 
    try {
      addRouteRecoredAVP(originRequest, destinationRequest);
      postRequestProcessing(destinationRequest);
      finalServerInitiatedRequestProcessing(session, originRequest, destinationRequest, routingEntry);
      sendServerInitiatedRequest(session, destinationRequest, new ResponseListener() {
            public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession diameterSession) {
              RelayAgent.this.routerContext.updateRealmInputStatistics((DiameterPacket)diameterAnswer, RelayAgent.this.routerContext
                  .getPeerData(hostIdentity).getRealmName(), routingEntry
                  .getRoutingAction());
              RelayAgent.this.routeServerInitiatedAnswer(diameterAnswer, originRequest, destinationRequest, session, routingEntry);
            }
            
            public void requestTimedout(String hostIdentity, DiameterSession diameterSession) {
              DiameterAnswer diameterAnswer = new DiameterAnswer(destinationRequest, ResultCode.DIAMETER_REQUEST_TIMEOUT);
              DiameterUtility.addOrReplaceAvp("0:281", (DiameterPacket)diameterAnswer, "Request Timeout");
              RelayAgent.this.routerContext.updateRealmTimeoutRequestStatistics(destinationRequest, RelayAgent.this.routerContext
                  .getPeerData(hostIdentity).getRealmName(), routingEntry.getRoutingAction());
              RelayAgent.this.routeServerInitiatedAnswer(diameterAnswer, originRequest, destinationRequest, session, routingEntry);
            }
          },destHost, routingEntry.getRoutingAction());
    } catch (CommunicationException e) {
      LogManager.getLogger().trace("RELAY-AGNT", (Throwable)e);
      throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_COMPLY, routingEntry
          .getRoutingAction(), e.getMessage());
    } 
  }
  
  private void routeServerInitiatedAnswer(DiameterAnswer remoteAnswer, DiameterRequest originalRequest, DiameterRequest remoteRequest, DiameterSession diameterSession, RoutingEntry routingEntry) {
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RELAY-AGNT", "Routing initiated for Diameter Answer with Session-ID=" + diameterSession.getSessionId()); 
    DiameterAnswer originAnswer = null;
    try {
      originAnswer = (DiameterAnswer)remoteAnswer.clone();
    } catch (CloneNotSupportedException e) {
      LogManager.getLogger().trace("RELAY-AGNT", e);
      originAnswer = remoteAnswer;
    } 
    this.routerContext.preAnswerRouting(originalRequest, remoteRequest, originAnswer, originalRequest
        .getRequestingHost(), routingEntry.getRoutingEntryName());
    DiameterAnswer finalAnswer = buildAnswer(originalRequest, remoteAnswer, routingEntry, diameterSession, remoteRequest);
    postAnswerProcessing(finalAnswer);
    boolean releaseSession = this.sessionReleaseIndiactor.isEligible((DiameterPacket)finalAnswer);
    if (!releaseSession)
      updateOrSaveSession(originalRequest, finalAnswer); 
    finalServerInitiatedAnswerProcessing(originalRequest, remoteAnswer, finalAnswer, diameterSession, remoteRequest, routingEntry);
    try {
      sendAnswer((Session)diameterSession, originalRequest, finalAnswer, originalRequest.getRequestingHost(), routingEntry.getRoutingAction());
    } catch (CommunicationException e) {
      LogManager.getLogger().error("RELAY-AGNT", "Unable to send diameter answer for request with HbH-ID=" + originalRequest
          .getHop_by_hopIdentifier() + "EtE-ID=" + originalRequest
          .getEnd_to_endIdentifier() + ", Reason: " + e.getMessage());
      LogManager.getLogger().trace("RELAY-AGNT", (Throwable)e);
    } 
  }
  
  protected final void locateSession(DiameterRequest diameterRequest) {
    if (this.diameterSessionManager == null)
      return; 
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RELAY-AGNT", "Session location for Diameter Packet with HbH-ID = " + diameterRequest
          .getHop_by_hopIdentifier() + " and EtE-ID = " + diameterRequest.getEnd_to_endIdentifier() + " has started"); 
    List<SessionData> locatedSessionData = this.diameterSessionManager.locate(diameterRequest, null);
    if (locatedSessionData != null && 
      LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("RELAY-AGNT", locatedSessionData.size() + " session(s) located."); 
    diameterRequest.setLocatedSessionData(locatedSessionData);
  }
  
  protected final void updateOrSaveSession(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {
    if (this.diameterSessionManager == null)
      return; 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("RELAY-AGNT", "Session updation/save for Diameter Request with HbH-ID=" + diameterRequest
          .getHop_by_hopIdentifier() + " and EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() + " has started"); 
    this.diameterSessionManager.updateOrSave(diameterRequest, diameterAnswer, diameterRequest.getLocatedSessionData());
  }
  
  protected final void releaseSession(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer, DiameterSession diameterSession) {
    if (this.diameterSessionManager != null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("RELAY-AGNT", "Session deletion for Diameter Packet with HbH-ID=" + diameterRequest
            .getHop_by_hopIdentifier() + " and EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() + " has started"); 
      this.diameterSessionManager.delete(diameterRequest, diameterAnswer);
    } 
    diameterSession.release();
  }
}
