package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RedirectFailureAction implements RoutingFailureAction {
  private static final String MODULE = "REDIRECT-FLR-ACT";
  
  private static final String SECONDARY_PEER = "0.0.0.0";
  
  private static final String NEXT_PEER = "255.255.255.255";
  
  private String failureArgs;
  
  private boolean attachedRedirection;
  
  private RouterContext routerContext;
  
  private PeerSelector peerSelector;
  
  private List<IDiameterAVP> answerAVPs;
  
  private List<String> redirectHosts;
  
  private List<String> warnings;
  
  public RedirectFailureAction(RouterContext diameterRouterContext, String failureArgs, boolean attacheRedirection, PeerSelector peerSelector) {
    this.failureArgs = failureArgs;
    this.attachedRedirection = attacheRedirection;
    this.routerContext = diameterRouterContext;
    this.answerAVPs = new ArrayList<>();
    this.peerSelector = peerSelector;
    this.warnings = new ArrayList<>();
  }
  
  public void init() {
    if (this.failureArgs == null || this.failureArgs.trim().length() == 0) {
      this.warnings.add("No redirection AVPs found for " + DiameterFailureConstants.REDIRECT + " Failure Action");
      return;
    } 
    StringTokenizer st = new StringTokenizer(this.failureArgs, ",");
    while (st.hasMoreTokens()) {
      String nextTokenString = st.nextToken();
      StringTokenizer avpIdValuePair = new StringTokenizer(nextTokenString, "=");
      if (avpIdValuePair.countTokens() != 2) {
        this.warnings.add("Invalid AVP: " + nextTokenString + " for " + DiameterFailureConstants.REDIRECT + " Failure Action, it should be in key value format i.e. <AVP_ID>=<VALUE>");
        continue;
      } 
      String avpId = avpIdValuePair.nextToken().trim();
      String avpValue = avpIdValuePair.nextToken().trim();
      if (avpId.length() == 0 || avpValue.length() == 0) {
        this.warnings.add("Invalid AVP: " + nextTokenString + " for " + DiameterFailureConstants.REDIRECT + " Failure Action");
        continue;
      } 
      IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(avpId);
      if (avp == null) {
        this.warnings.add("Invalid AVP: " + nextTokenString + " for " + DiameterFailureConstants.REDIRECT + " Failure Action");
        continue;
      } 
      if (avp.getAVPCode() == 292) {
        if (this.redirectHosts == null)
          this.redirectHosts = new ArrayList<>(); 
        this.redirectHosts.add(avpValue);
        continue;
      } 
      avp.setStringValue(avpValue);
      this.answerAVPs.add(avp);
    } 
    if (this.redirectHosts == null)
      this.warnings.add("No Redirect-Host AVPs could be parsed for " + DiameterFailureConstants.REDIRECT + "  Failure Action"); 
  }
  
  public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession, DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity, String originPeerName) {
    String sessionId = failureAnswer.getAVPValue("0:263");
    int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("REDIRECT-FLR-ACT", "Performing " + DiameterFailureConstants.REDIRECT + " Failure Action with Failure Argument " + this.failureArgs + " for Session-ID=" + sessionId + " HbH-ID=" + hopByHopKey); 
    remoteRequest.addFailedPeer(originPeerName);
    boolean bRedirectHostAdded = false;
    List<String> ignoreRedirectHosts = remoteRequest.getFailedPeerList();
    if (ignoreRedirectHosts == null)
      ignoreRedirectHosts = new ArrayList<>(1); 
    List<IDiameterAVP> redirectHostAVPs = new ArrayList<>(this.answerAVPs.size());
    for (int i = 0; i < this.redirectHosts.size(); i++) {
      String redirectHostValue = this.redirectHosts.get(i);
      redirectHostValue = getRedirectHostName(redirectHostValue, originRequest, ignoreRedirectHosts);
      if (redirectHostValue != null) {
        ignoreRedirectHosts.add(redirectHostValue);
        IDiameterAVP redirectHostAVP = getRedirectionHostAVP(redirectHostValue);
        if (redirectHostAVP != null) {
          redirectHostAVPs.add(redirectHostAVP);
          bRedirectHostAdded = true;
        } 
      } 
    } 
    if (bRedirectHostAdded) {
      DiameterAnswer answer = new DiameterAnswer(originRequest, ResultCode.DIAMETER_REDIRECT_INDICATION);
      for (int j = 0; j < this.answerAVPs.size(); j++) {
        try {
          answer.addAvp((IDiameterAVP)((IDiameterAVP)this.answerAVPs.get(j)).clone());
        } catch (CloneNotSupportedException e) {
          if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
            LogManager.getLogger().trace("REDIRECT-FLR-ACT", e); 
        } 
      } 
      answer.addAvps(redirectHostAVPs);
      return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, (DiameterPacket)answer);
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("REDIRECT-FLR-ACT", "No redirection AVPs could be parsed while failure in routing for session-ID=" + sessionId + ", Passing through Answer"); 
    return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, (DiameterPacket)failureAnswer);
  }
  
  private String getRedirectHostName(String redirectHostValue, DiameterRequest originRequest, List<String> ignorePeers) {
    String redirectHost = null;
    if ("255.255.255.255".equals(redirectHostValue)) {
      redirectHost = this.peerSelector.selectNextPeer(originRequest);
      if (redirectHost != null && !ignorePeers.contains(redirectHost))
        return redirectHost; 
      return this.peerSelector.selectSecondaryPeer(originRequest, ignorePeers
          .<String>toArray(new String[ignorePeers.size()]));
    } 
    if ("0.0.0.0".equals(redirectHostValue))
      return this.peerSelector.selectSecondaryPeer(originRequest, ignorePeers
          .<String>toArray(new String[ignorePeers.size()])); 
    DiameterPeerCommunicator redirectPeerCommunicator = this.routerContext.getPeerCommunicator(redirectHostValue);
    if (redirectPeerCommunicator == null)
      return null; 
    if (!this.attachedRedirection || redirectPeerCommunicator.isAlive())
      redirectHost = redirectPeerCommunicator.getName(); 
    if (!ignorePeers.contains(redirectHost))
      return redirectHost; 
    return null;
  }
  
  private IDiameterAVP getRedirectionHostAVP(String redirectHostName) {
    PeerData redirectPeerData = this.routerContext.getPeerData(redirectHostName);
    if (redirectPeerData == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("REDIRECT-FLR-ACT", "Peer configuration for redirect host " + redirectHostName + " not found."); 
      return null;
    } 
    String redirectHostVal = redirectPeerData.getURI();
    if (redirectHostVal == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("REDIRECT-FLR-ACT", "DiameterURI not found for Peer " + redirectHostName); 
      return null;
    } 
    IDiameterAVP redirectHostAVP = DiameterDictionary.getInstance().getKnownAttribute("0:292");
    redirectHostAVP.setStringValue(redirectHostVal);
    return redirectHostAVP;
  }
  
  public List<String> getWarnings() {
    return this.warnings;
  }
}
