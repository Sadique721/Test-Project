package com.diameter.commons;

public class RedirectAgent extends DiameterAgent {
  private static final String MODULE = "REDIRECT-AGNT";
  
  private SessionReleaseIndiactor sessionReleaseIndiactor;
  
  public RedirectAgent(RouterContext diameterRouterContext) {
    super(diameterRouterContext);
    this.sessionReleaseIndiactor = SessionReleaseIndicatorFactory.getDefaultSessionReleaseIndiactor();
  }
  
  public void routeRequest(DiameterRequest diameterRequest, DiameterSession diameterSession, RoutingEntry routingEntry) throws RoutingFailedException {
    String sessionId = diameterSession.getSessionId();
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("REDIRECT-AGNT", "Redirecting Diameter Request with Session-Id=" + sessionId); 
    PeerSelector peerSelector = routingEntry.getPeerSelector();
    if (peerSelector == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("REDIRECT-AGNT", "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + " for Session-ID=" + sessionId + ", Reason: No Redirect Peer Group found."); 
      throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER, RoutingActions.REDIRECT, "Redirect Peer not found");
    } 
    String redirectHost = peerSelector.selectNextPeer(diameterRequest);
    String secondaryRedirectHost = peerSelector.selectSecondaryPeer(diameterRequest, new String[] { redirectHost });
    if (redirectHost == null) {
      if (secondaryRedirectHost == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("REDIRECT-AGNT", "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + " for Session-ID=" + sessionId + ", Reason: No redirection host found."); 
        throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER, RoutingActions.REDIRECT, "Redirect Peer not found");
      } 
      redirectHost = secondaryRedirectHost;
      secondaryRedirectHost = null;
    } 
    boolean bRedirectHostAdded = false;
    DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_REDIRECT_INDICATION);
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("REDIRECT-AGNT", "Redirection host " + redirectHost + " selected for session-ID=" + sessionId); 
    IDiameterAVP redirectHostAVP = getRedirectionHostAVP(redirectHost);
    if (redirectHostAVP != null) {
      answer.addAvp(redirectHostAVP);
      bRedirectHostAdded = true;
    } 
    if (secondaryRedirectHost != null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("REDIRECT-AGNT", "Redirection host " + secondaryRedirectHost + " selected for session-ID=" + sessionId); 
      IDiameterAVP secRedirectHostAVP = getRedirectionHostAVP(secondaryRedirectHost);
      if (redirectHostAVP != null) {
        answer.addAvp(secRedirectHostAVP);
        bRedirectHostAdded = true;
      } 
    } 
    if (!bRedirectHostAdded) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("REDIRECT-AGNT", "Sending " + ResultCode.DIAMETER_UNABLE_TO_COMPLY + " for Session-ID=" + sessionId + ", Reason: No redirection host could be resolved."); 
      throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_COMPLY, RoutingActions.REDIRECT, "No redirection host could be resolved for Session-ID=" + sessionId);
    } 
    String translationMapp = routingEntry.getTranslationMapping();
    if (translationMapp != null && translationMapp.trim().length() > 0)
      answer = buildAnswer(translationMapp, answer, diameterSession, diameterRequest); 
    try {
      sendAnswer((Session)diameterSession, diameterRequest, answer, diameterRequest.getRequestingHost(), routingEntry.getRoutingAction());
    } catch (CommunicationException e) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("REDIRECT-AGNT", "Dropping Diameter Answer with Session-ID=" + diameterSession
            .getSessionId() + ", Reason: " + e.getMessage()); 
      LogManager.getLogger().trace("REDIRECT-AGNT", (Throwable)e);
      this.routerContext.updateDiameterStatsPacketDroppedStatistics((DiameterPacket)answer, diameterRequest
          .getRequestingHost(), diameterRequest
          .getPeerData().getRealmName(), routingEntry
          .getRoutingAction());
    } 
    if (this.sessionReleaseIndiactor.isEligible((DiameterPacket)answer))
      diameterSession.release(); 
  }
  
  private IDiameterAVP getRedirectionHostAVP(String redirectHostName) {
    PeerData redirectPeerData = this.routerContext.getPeerData(redirectHostName);
    if (redirectPeerData == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("REDIRECT-AGNT", "Peer configuration for Redirect host " + redirectHostName + " not found."); 
      return null;
    } 
    String redirectHostVal = redirectPeerData.getURI();
    if (redirectHostVal == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("REDIRECT-AGNT", "DiameterURI not found for Peer " + redirectHostName); 
      return null;
    } 
    IDiameterAVP redirectHostAVP = DiameterDictionary.getInstance().getKnownAttribute("0:292");
    if (redirectHostAVP != null) {
      redirectHostAVP.setStringValue(redirectHostVal);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("REDIRECT-AGNT", "0:292 AVP not found in Diameter Dictionary");
    } 
    return redirectHostAVP;
  }
  
  private DiameterAnswer buildAnswer(String translatorName, DiameterAnswer diameterAnswer, DiameterSession diameterSession, DiameterRequest originRequest) throws RoutingFailedException {
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("REDIRECT-AGNT", "Translating packet before sending Redirect Indication using translation policy: " + translatorName); 
    TranslatorParamsImpl translatorParamsImpl = new TranslatorParamsImpl(diameterAnswer, diameterAnswer, originRequest, null);
    translatorParamsImpl.setParam("DIAMETER_SESSION", diameterSession);
    try {
      TranslationAgent.getInstance().translate(translatorName, (TranslatorParams)translatorParamsImpl, diameterAnswer.isRequest());
      diameterAnswer = (DiameterAnswer)translatorParamsImpl.getParam("TO_PACKET");
    } catch (TranslationFailedException e) {
      LogManager.getLogger().trace("REDIRECT-AGNT", (Throwable)e);
      throw new RoutingFailedException(RoutingActions.REDIRECT, 
          DiameterErrorMessageConstants.translationFailed(translatorName));
    } 
    return diameterAnswer;
  }
}