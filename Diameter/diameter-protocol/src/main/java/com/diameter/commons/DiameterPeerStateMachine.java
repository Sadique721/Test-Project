package com.diameter.commons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.diameter.stack.Stack;

public abstract class DiameterPeerStateMachine extends StateMachine implements IPeerAtomicActionsExecutor {
  private static final String MODULE = "DIAMETER-STATE-MACHINE";
  
  protected static final char[] padding = new char[512];
  
  private AtomicLong numberOfConnectionAttempts = new AtomicLong(0L);
  
  private SessionFactoryManager sessionFactoryManager;
  
  private DiameterPeer peer;
  
  private IDiameterStackContext stackContext;
  
  private DiameterRouter diameterRouter;
  
  private DiameterAppMessageHandler appMessageHandler;
  
  private ExplicitRoutingHandler explicitRoutingHandler;
  
  private DiameterPeer.OverloadHandler overloadHandler;
  
  private int originStateId = -1;
  
  private DuplicateDetectionHandler duplicateMessageHandler;
  
  private SessionReleaseIndiactor sessionReleaseIndiactor;
  
  @Nonnull
  private final TimeSource timesource;
  
  private volatile DuplicateConnectionPolicy duplicateConnectionPolicy;
  
  protected DiameterPeerStateMachine(DiameterPeer peer, DiameterRouter diameterRouter, SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler appMessageHandler, IDiameterStackContext stackContext, ExplicitRoutingHandler explicitRoutingHandler, DiameterPeer.OverloadHandler overloadHandler, DuplicateDetectionHandler duplicateMessageHandler, @Nonnull TimeSource timesource) {
    this(peer, stackContext, diameterRouter, sessionFactoryManager, appMessageHandler, (IStateEnum)StateEnum.UNKNOWN, explicitRoutingHandler, overloadHandler, duplicateMessageHandler, timesource);
  }
  
  public DiameterPeerStateMachine(DiameterPeer peer, IDiameterStackContext stackContext, DiameterRouter diameterRouter, SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler appMessageHandler, IStateEnum stateEnum, ExplicitRoutingHandler explicitRoutingHandler, DiameterPeer.OverloadHandler overloadHandler, DuplicateDetectionHandler duplicateMessageHandler, @Nonnull TimeSource timesource) {
    super(stateEnum);
    this.peer = peer;
    this.stackContext = stackContext;
    this.diameterRouter = diameterRouter;
    this.appMessageHandler = appMessageHandler;
    this.explicitRoutingHandler = explicitRoutingHandler;
    this.overloadHandler = overloadHandler;
    this.duplicateMessageHandler = duplicateMessageHandler;
    this.timesource = timesource;
    this.sessionReleaseIndiactor = SessionReleaseIndicatorFactory.getDefaultSessionReleaseIndiactor();
    this.duplicateConnectionPolicy = createDuplicateConnectionPolicy();
    this.sessionFactoryManager = sessionFactoryManager;
  }
  
  private DuplicateConnectionPolicy createDuplicateConnectionPolicy() {
    switch (this.peer.getPeerData().getDuplicateConnectionPolicyType()) {
      case DISCARD_OLD:
        return new DiscardOldPolicy();
    } 
    return new DefaultPolicy();
  }
  
  protected StateEvent createStateEvent(IStateTransitionData transitionData) {
    StateEvent stateEvent = null;
    DiameterPeerEvent peerEvent = (DiameterPeerEvent)transitionData.getData((IStateTransitionDataCode)PeerDataCode.PEER_EVENT);
    DiameterPacket diameterPacket = (DiameterPacket)transitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    if (peerEvent != null) {
      DiameterPeerState nextState = (DiameterPeerState)this.currentState.getNextState((IEventEnum)peerEvent);
      if (nextState != null) {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)peerEvent, (IStateEnum)nextState, transitionData);
        return stateEvent;
      } 
    } else if (diameterPacket != null) {
      try {
        stateEvent = fetchCurrentState().getStateEvent(transitionData);
      } catch (Exception e) {
        LogManager.getLogger().trace("DIAMETER-STATE-MACHINE", e);
      } 
      if (stateEvent != null)
        return stateEvent; 
    } 
    if (LogManager.getLogger().isWarnLogLevel())
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Peer: " + getPeerName() + ", Event Can't decided..." + this.currentState + " : " + peerEvent); 
    return null;
  }
  
  public boolean stop() {
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("DIAMETER-STATE-MACHINE", "Peer: " + getPeerName() + ", Shutdown is called, generating Stop event."); 
    IStateTransitionData transitionData = new IStateTransitionData() {
        Map<IStateTransitionDataCode, Object> data = new HashMap<>();
        
        public Object getData(IStateTransitionDataCode key) {
          return this.data.get(key);
        }
        
        public void addObject(IStateTransitionDataCode key, Object value) {
          this.data.put(key, value);
        }
      };
    transitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, DiameterPeerEvent.Stop);
    try {
      onStateTransitionTrigger(transitionData);
    } catch (UnhandledTransitionException e) {
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Peer : " + getPeerName() + "facing an issue with stop. Reason :" + e.getMessage());
    } 
    return true;
  }
  
  public void atomicActionCleanup(StateEvent event, ConnectionEvents connEvent) {
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Received NAck for " + this.peer.getPeerName() + ", cleanup done"); 
    this.peer.closeConnection(connEvent);
  }
  
  public void atomicActionError(StateEvent event, ConnectionEvents connEvent) {
    this.peer.closeConnection(connEvent);
  }
  
  public void atomicActionIDisc(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Closing connection with the peer " + this.peer.getPeerName()); 
  }
  
  public void atomicActionProcess(StateEvent event) {
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    DiameterSession diameterSession = null;
    SessionsFactory diameterSessionFactory = this.sessionFactoryManager.getSessionFactory(diameterPacket.getApplicationID());
    if (diameterSessionFactory.hasSession(diameterPacket.getAVPValue("0:263")))
      diameterSession = (DiameterSession)diameterSessionFactory.getOrCreateSession(diameterPacket.getAVPValue("0:263")); 
    if (diameterPacket.isRequest()) {
      DiameterRequest diameterRequest = (DiameterRequest)diameterPacket;
      if (diameterSession == null) {
        if (this.stackContext.isOverLoad(diameterRequest)) {
          this.overloadHandler.handle(diameterRequest);
          return;
        } 
        diameterSession = (DiameterSession)diameterSessionFactory.getOrCreateSession(diameterPacket.getAVPValue("0:263"));
      } 
      if (this.peer.getPeerData().isReTransmissionCompliant() && this.duplicateMessageHandler
        .isDuplicate(diameterRequest)) {
        DiameterAnswer diameterAnswer = this.duplicateMessageHandler.storeIfAbsent(diameterRequest);
        this.stackContext.updateDuplicatePacketStatistics((DiameterPacket)diameterRequest, this.peer.getHostIdentity());
        if (diameterAnswer != null) {
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Diameter Answer available for Duplicate Request, Responding Duplicate Request with HbH-ID=" + diameterAnswer.getHop_by_hopIdentifier()); 
          this.peer.sendDiameterAnswer(diameterAnswer);
        } 
        return;
      } 
    } else if (diameterSession == null) {
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Session-id=" + diameterPacket.getSessionID() + " for answer not found. Creating new.");
      diameterSession = (DiameterSession)diameterSessionFactory.getOrCreateSession(diameterPacket.getAVPValue("0:263"));
    } 
    try {
      if (this.stackContext.isEREnabled())
        this.explicitRoutingHandler.handle(diameterPacket); 
      if (diameterPacket.isRequest()) {
        submitRequestToDiameterRouter(stateTransitionData, this.peer, diameterSession, diameterPacket.getAsDiameterRequest());
      } else {
        submitAnswerToDiameterRouter(stateTransitionData, this.peer, diameterPacket.getAsDiameterAnswer(), diameterSession);
      } 
      diameterSession.update(ValueProvider.NO_VALUE_PROVIDER);
    } catch (MalformedNAIException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Peer: " + getPeerName() + ", " + e.getMessage() + ", so skipping further local processing"); 
      LogManager.ignoreTrace((Exception)e);
      DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_INVALID_AVP_VALUE);
      DiameterUtility.addFailedAVP(answerPacket, diameterPacket.getAVP("0:1"));
      this.peer.sendDiameterAnswer(answerPacket);
      if (this.sessionReleaseIndiactor.isEligible((DiameterPacket)answerPacket))
        diameterSession.release(); 
    } catch (RoutingFailedException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Peer: " + getPeerName() + ", " + e.getMessage() + ", so skipping further local processing"); 
      LogManager.ignoreTrace((Exception)e);
      DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, e.getResultCode());
      if (!Strings.isNullOrBlank(e.getLocalizedMessage()))
        DiameterUtility.addOrReplaceAvp("0:281", (DiameterPacket)answerPacket, e.getLocalizedMessage()); 
      this.peer.sendDiameterAnswer(answerPacket);
      RoutingActions routingAction = e.getRoutingAction();
      if (routingAction != null)
        this.stackContext.updateRealmOutputStatistics((DiameterPacket)answerPacket, this.peer
            .getRealm(), routingAction); 
      if (this.sessionReleaseIndiactor.isEligible((DiameterPacket)answerPacket))
        diameterSession.release(); 
    } catch (ExplicitRoutingFailedException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "For Peer: " + getPeerName() + ", " + e.getMessage() + ", so skipping further local processing"); 
      LogManager.ignoreTrace((Exception)e);
      if (diameterPacket.isResponse())
        return; 
      DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, e.getResultCode());
      this.peer.sendDiameterAnswer(answerPacket);
      if (this.sessionReleaseIndiactor.isEligible((DiameterPacket)answerPacket))
        diameterSession.release(); 
    } catch (UnsupportedApplicationException e) {
      LogManager.ignoreTrace((Exception)e);
      if (diameterPacket.isResponse()) {
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Dropping Response, Reason: Application: " + e.getUnsupportedApplicationId() + " is not supported."); 
        this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, this.peer.getHostIdentity());
        return;
      } 
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Application: " + e.getUnsupportedApplicationId() + " is not supported. Sending " + ResultCode.DIAMETER_APPLICATION_UNSUPPORTED + " to Peer: " + 
            getPeerName()); 
      DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED);
      this.peer.sendDiameterAnswer(answerPacket);
      this.stackContext.updateRealmOutputStatistics((DiameterPacket)answerPacket, this.peer
          .getRealm(), RoutingActions.LOCAL);
      if (this.sessionReleaseIndiactor.isEligible((DiameterPacket)answerPacket))
        diameterSession.release(); 
    } 
  }
  
  private void submitRequestToDiameterRouter(IStateTransitionData stateTransitionData, DiameterPeer peer, DiameterSession diameterSession, DiameterRequest diameterRequest) throws MalformedNAIException, RoutingFailedException, UnsupportedApplicationException {
    RoutingActions action = this.diameterRouter.processDiameterRequest(diameterRequest, diameterSession);
    if (RoutingActions.LOCAL == action)
      if (this.stackContext.isNAIEnabled() && isNAIRequest((DiameterPacket)diameterRequest)) {
        submitRequestToDiameterRouter(stateTransitionData, peer, diameterSession, diameterRequest);
      } else {
        this.stackContext.updateRealmInputStatistics((DiameterPacket)diameterRequest, peer
            .getRealmName(), RoutingActions.LOCAL);
        this.appMessageHandler.handleReceivedRequest(diameterRequest, (Session)diameterSession);
      }  
  }
  
  private void submitAnswerToDiameterRouter(IStateTransitionData stateTransitionData, DiameterPeer peer, DiameterAnswer diameterAnswer, DiameterSession session) {
    ResponseListener responseListener = (ResponseListener)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.RESPONSE_LISTENER);
    responseListener.responseReceived(diameterAnswer, peer.getHostIdentity(), session);
  }
  
  private boolean isNAIRequest(DiameterPacket diameterPacket) throws MalformedNAIException {
    boolean isNAIRequest = false;
    IDiameterAVP userName = diameterPacket.getAVP("0:1");
    IDiameterAVP destinationRealm = diameterPacket.getAVP("0:283");
    if (userName != null) {
      String userNameStr = userName.getStringValue();
      if (DiameterUtility.isValidUserAccordingToABNF(userNameStr)) {
        if (DiameterUtility.isNAIDecorated(userNameStr)) {
          isNAIRequest = isNAIRequest(diameterPacket, isNAIRequest, userName, destinationRealm, userNameStr);
        } else {
          stripNAIDecoration(diameterPacket, userName);
        } 
      } else {
        throw new MalformedNAIException("Improper UserName " + userNameStr + " is not according to RFC 4282");
      } 
    } 
    return isNAIRequest;
  }
  
  private boolean isNAIRequest(DiameterPacket diameterPacket, boolean isNAIRequest, IDiameterAVP userName, IDiameterAVP destinationRealm, String userNameStr) throws MalformedNAIException {
    if (DiameterUtility.isValidForProxy(userNameStr)) {
      String proxyRealm = DiameterUtility.getProxyRealm(userNameStr);
      if (DiameterUtility.isValidRealmAccordingToABNF(proxyRealm)) {
        if (this.stackContext.isValidNAIRealm(proxyRealm)) {
          isNAIRequest = true;
          String transformedNAI = DiameterUtility.transformNAI(userNameStr);
          if (destinationRealm == null) {
            destinationRealm = DiameterDictionary.getInstance().getAttribute("0:283");
            diameterPacket.addAvp(destinationRealm);
          } 
          userName.setStringValue(transformedNAI);
          destinationRealm.setStringValue(proxyRealm);
          stripNAIDecoration(diameterPacket, userName);
        } 
      } else {
        throw new MalformedNAIException("Improper Proxy-Realm :" + proxyRealm + " is not according to RFC 4282");
      } 
    } 
    return isNAIRequest;
  }
  
  private void stripNAIDecoration(DiameterPacket diameterPacket, IDiameterAVP userNameAvp) {
    IDiameterAVP naiDecorationAttr = DiameterDictionary.getInstance().getKnownAttribute("21067:204");
    if (naiDecorationAttr != null) {
      String userName = userNameAvp.getStringValue();
      int indexOfOpeningCurlyBrace = userName.indexOf('{');
      int indexOfClosingCurlyBrace = userName.indexOf('}');
      if (indexOfClosingCurlyBrace != -1 && indexOfOpeningCurlyBrace == 0 && indexOfClosingCurlyBrace < userName.length() - 1 && indexOfClosingCurlyBrace - indexOfOpeningCurlyBrace > 1) {
        userName = userName.substring(indexOfClosingCurlyBrace + 1);
        naiDecorationAttr.setStringValue(userName.substring(indexOfOpeningCurlyBrace + 1, indexOfClosingCurlyBrace));
        userNameAvp.setStringValue(userName);
        diameterPacket.addInfoAvp(naiDecorationAttr);
      } 
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "NAI-Decoration AVP not found in Dictionary. Skipping stripping of NAI Decoration");
    } 
  }
  
  public ResultCode atomicActionProcessCEA(StateEvent event) {
    DiameterPacket receivedPacket = (DiameterPacket)event.getStateTransitionData().getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    int resultCode = ResultCode.DIAMETER_SUCCESS.getCode();
    IDiameterAVP resultCodeAVP = receivedPacket.getAVP("0:268");
    if (resultCodeAVP != null) {
      resultCode = (int)resultCodeAVP.getInteger();
    } else {
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Result-Code AVP is not present in CEA. Considering " + ResultCode.fromCode(resultCode));
    } 
    if (resultCode == ResultCode.DIAMETER_SUCCESS.code) {
      setPeerInitConnection(true);
    } else if (resultCode > 2999) {
      if (ResultCodeCategory.getResultCodeCategory(resultCode) == ResultCodeCategory.RC5XXX && 
        this.peer.getPeerData().isInitConnection()) {
        setPeerInitConnection(false);
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Initiate Connection with Peer: " + getPeerName() + " disabled, Reason: CEA with Result-code= " + 
              ResultCode.fromCode(resultCode) + " received."); 
      } 
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Peer: " + getPeerName() + ", CEA received with Result code " + ResultCode.fromCode(resultCode) + ". Closing connection.");
      this.peer.closeConnection(ConnectionEvents.CONNECTION_BREAK);
      Stack.generateAlert(StackAlertSeverity.CRITICAL, DiameterStackAlerts.DIAMETER_PEER_DOWN, "DIAMETER-STATE-MACHINE", "CEA received with Result-code: " + 
          
          ResultCode.fromCode(resultCode) + ", current state: " + this.currentState.toString() + " for peer: " + this.peer.getPeerName(), 0, this.peer
          .getPeerName() + "(" + ResultCode.fromCode(resultCode) + ", " + this.currentState.toString() + ")");
      return ResultCode.fromCode(resultCode);
    } 
    ArrayList<IDiameterAVP> hostAddressAvpList = receivedPacket.getAVPList("0:257");
    if (hostAddressAvpList != null) {
      List<String> hostIpAddresses = new ArrayList<>();
      for (IDiameterAVP hostAddressAvp : hostAddressAvpList)
        hostIpAddresses.add(hostAddressAvp.getStringValue()); 
      if (!Collectionz.isNullOrEmpty(hostIpAddresses))
        this.peer.setHostIPAddress(hostIpAddresses); 
    } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
      LogManager.getLogger().info("DIAMETER-STATE-MACHINE", "Host not found");
    } 
    ArrayList<IDiameterAVP> inbandSecurities = receivedPacket.getAVPList("0:299");
    if (!Collectionz.isNullOrEmpty(inbandSecurities))
      for (IDiameterAVP inbandSecurity : inbandSecurities) {
        InbandSecurityId inbandSecurityId = InbandSecurityId.fromCode((int)inbandSecurity.getInteger());
        this.peer.addRemoteSecurityId(inbandSecurityId);
      }  
    this.peer.setProductName(receivedPacket.getAVP("0:269").getStringValue());
    this.peer.setVendorId((int)receivedPacket.getAVP("0:266").getInteger());
    this.peer.addRemoteApplication(receivedPacket);
    ArrayList<IDiameterAVP> supportedVendorIds = receivedPacket.getAVPList("0:265");
    if (!Collectionz.isNullOrEmpty(supportedVendorIds))
      for (IDiameterAVP supportedVendorId : supportedVendorIds)
        this.peer.addSupportedVendor(supportedVendorId.getInteger());  
    String originHost = receivedPacket.getAVPValue("0:264");
    if (originHost != null) {
      if (!originHost.equals(this.peer.getHostIdentity()) && 
        LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Origin Host Received in CEA does not matches for peer: " + this.peer.getPeerName()); 
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Origin Host is not received in CEA for peer: " + this.peer.getPeerName());
    } 
    return ResultCode.fromCode(resultCode);
  }
  
  public ResultCode atomicActionProcessCER(StateEvent event) {
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    DiameterRequest diameterPacket = (DiameterRequest)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    NetworkConnectionHandler connectionHandler = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    ArrayList<IDiameterAVP> hostAddressAvpList = diameterPacket.getAVPList("0:257");
    List<String> hostIpAddresses = new ArrayList<>();
    if (hostAddressAvpList != null) {
      for (IDiameterAVP hostAddressAvp : hostAddressAvpList)
        hostIpAddresses.add(hostAddressAvp.getStringValue()); 
      if (!Collectionz.isNullOrEmpty(hostIpAddresses))
        this.peer.setHostIPAddress(hostIpAddresses); 
    } 
    this.peer.setProductName(diameterPacket.getAVP("0:269").getStringValue());
    this.peer.setVendorId((int)diameterPacket.getAVP("0:266").getInteger());
    if (!this.peer.isPeerConnected()) {
      this.peer.setConnectionListener(connectionHandler);
    } else {
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Rejecting new Connection with peer: " + getPeerName() + " because peer is already connected");
      connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
    } 
    if (this.peer.isSessionCleanUpOnCER()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + ".Reason: session clean up on CER is enabled"); 
      releasePeerSessions(diameterPacket);
    } else {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Session clean up on CER is disabled for peer: " + this.peer.getPeerName()); 
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Checking Origin-State-Id AVP value for Releasing sessions for peer: " + this.peer.getPeerName()); 
      IDiameterAVP origin_stateAVP = diameterPacket.getAVP("0:278");
      if (origin_stateAVP != null) {
        int originStateID = (int)origin_stateAVP.getInteger();
        if (this.originStateId == -1) {
          setOriginStateID(originStateID);
        } else if (originStateID != this.originStateId) {
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + ". Reason: Value of Origin-State-Id AVP is changed from " + getOriginStateId() + " to " + originStateID); 
          setOriginStateID(originStateID);
          releasePeerSessions(diameterPacket);
        } 
      } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
        LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + " skipped. Reason: Origin-State-Id AVP not found from CER");
      } 
    } 
    this.peer.addRemoteApplication((DiameterPacket)diameterPacket);
    if (this.peer.getCommonApplications().isEmpty())
      return ResultCode.DIAMETER_NO_COMMON_APPLICATION; 
    ArrayList<IDiameterAVP> inbandSecurities = diameterPacket.getAVPList("0:299");
    if (!Collectionz.isNullOrEmpty(inbandSecurities)) {
      for (IDiameterAVP inbandSecurity : inbandSecurities) {
        InbandSecurityId inbandSecurityId = InbandSecurityId.fromCode((int)inbandSecurity.getInteger());
        this.peer.addRemoteSecurityId(inbandSecurityId);
      } 
    } else {
      this.peer.addRemoteSecurityId(InbandSecurityId.NO_INBAND_SECURITY);
    } 
    Set<InbandSecurityId> commonSecurity = this.peer.getCommonSecurityIds();
    if (commonSecurity.isEmpty() && this.peer.getPeerData().getSecurityStandard() != SecurityStandard.RFC_6733)
      return ResultCode.DIAMETER_NO_COMMON_SECURITY; 
    if (diameterPacket.getAVP("0:267") != null)
      this.peer.setFirmwareRevision((int)diameterPacket.getAVP("0:267").getInteger()); 
    ArrayList<IDiameterAVP> supportedVendorIds = diameterPacket.getAVPList("0:265");
    if (!Collectionz.isNullOrEmpty(supportedVendorIds))
      for (IDiameterAVP supportedVendorId : supportedVendorIds)
        this.peer.addSupportedVendor(supportedVendorId.getInteger());  
    return ResultCode.DIAMETER_SUCCESS;
  }
  
  public void atomicActionProcessDWA(StateEvent event) {}
  
  public void atomicActionProcessDWR(StateEvent event) {}
  
  public boolean atomicActionRAccept(StateEvent event) {
    this.numberOfConnectionAttempts.set(0L);
    return true;
  }
  
  public void atomicActionRDisc(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Closing Connection with the peer :: " + this.peer
          .getPeerName()); 
  }
  
  public void atomicActionRReject(@Nullable NetworkConnectionHandler connection) {
    if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Peer: " + getPeerName() + ", Executing RReject Action"); 
    Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.PEER_CONNECTION_REJECTED, "DIAMETER-STATE-MACHINE", "Multiple Connection Request rejected for Peer: " + 
        getPeerName(), 0, 
        getPeerName());
    this.peer.recordMessageTxCountAndConnectionTime();
    if (connection != null) {
      connection.closeConnection(ConnectionEvents.REJECT_CONNECTION);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Peer: " + getPeerName() + ", Failed closing Connection. Reason: Connection not available ");
    } 
  }
  
  public void atomicActionRSndCEA(StateEvent event) {
    atomicActionRSndCEA(event, ResultCode.DIAMETER_SUCCESS);
  }
  
  public void atomicActionRSndCEA(StateEvent event, ResultCode resultCode) {
    sendCEA(event, resultCode);
  }
  
  private void sendCEA(StateEvent event, ResultCode resultCode) {
    try {
      IStateTransitionData stateTransitionData = event.getStateTransitionData();
      DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
      DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket);
      IDiameterAVP originStateIDAvp = diameterPacket.getAVP("0:278");
      if (originStateIDAvp != null && originStateIDAvp.getInteger() != 0L) {
        IDiameterAVP originStateIdAVP = DiameterDictionary.getInstance().getAttribute("0:278");
        originStateIdAVP.setStringValue(String.valueOf(Parameter.getInstance().getOriginStateId()));
        diameterAnswer.addAvp(originStateIdAVP);
      } 
      byte b= 0x60;
      AvpAddress avpAddress = new AvpAddress(257, 0, b, "0:257", "yes");
      
      String advertisedIp = System.getenv("DIAMETER_ADVERTISE_IP");
      if(advertisedIp == null || advertisedIp.isEmpty()) {
    	  avpAddress.setStringValue(this.peer.getLocalBoundAddress());
      }else {
    	  avpAddress.setStringValue(advertisedIp);
      }
      diameterAnswer.addAvp(avpAddress);
      IDiameterAVP vendorId = DiameterDictionary.getInstance().getAttribute("0:266");
      vendorId.setInteger(Parameter.getInstance().getVendorId());
      diameterAnswer.addAvp(vendorId);
      IDiameterAVP productName = DiameterDictionary.getInstance().getAttribute("0:269");
      productName.setStringValue(Parameter.getInstance().getProductName());
      diameterAnswer.addAvp(productName);
      Set<InbandSecurityId> securityIds = this.peer.getCommonSecurityIds();
      if (!Collectionz.isNullOrEmpty(securityIds))
        for (InbandSecurityId inbandSecurityId : securityIds) {
          IDiameterAVP inbandSecurityAVP = DiameterDictionary.getInstance().getKnownAttribute("0:299");
          if (inbandSecurityAVP == null)
            break; 
          inbandSecurityAVP.setInteger(inbandSecurityId.getCode());
          diameterAnswer.addAvp(inbandSecurityAVP);
        }  
      Set<ApplicationEnum> commonApplications = this.peer.getCommonApplications();
      
      String rxOriginHost = System.getenv("ENV_RX_ORIGIN_HOST");
      IDiameterAVP originHostAvp = diameterPacket.getAVP("0:264");
      if(originHostAvp !=null && rxOriginHost !=null) {
    	  if(rxOriginHost.contains(originHostAvp.getStringValue())) {
    		  
    		// Vendor-Specific-Application-Id (260)
    		  IDiameterAVP vendorSpecificAppId = DiameterDictionary.getInstance()
    		          .getAttribute("0:260");

    		  ArrayList<IDiameterAVP> paramArrayList = new ArrayList<>();

    		  // Vendor-Id (266)
    		  IDiameterAVP vendorIdDiameterAVP = DiameterDictionary.getInstance()
    		          .getAttribute("0:266");

    		  vendorIdDiameterAVP.setInteger(Parameter.getInstance().getVendorId());

    		  paramArrayList.add(vendorIdDiameterAVP);

    		  IDiameterAVP authApplicationId = DiameterDictionary.getInstance().getAttribute("0:258");

    		  authApplicationId.setInteger(16777236);

    		  paramArrayList.add(authApplicationId);

    		  vendorSpecificAppId.setGroupedAvp(paramArrayList); 
    		  
    		  diameterAnswer.addAvp(vendorSpecificAppId);

    		  IDiameterAVP topLevelAuthAppId = DiameterDictionary.getInstance().getAttribute("0:258");

    		  topLevelAuthAppId.setInteger(16777236);

    		  diameterAnswer.addAvp(topLevelAuthAppId);
    	  }
      }
      
      if (commonApplications != null && !commonApplications.isEmpty()) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Total " + commonApplications.size() + " common applications found for peer = " + diameterPacket
              .getAVPValue("0:264")); 
        List<IDiameterAVP> diameterAVPs = createApplicationIdAVPs(commonApplications);
        if (diameterAVPs != null && !diameterAVPs.isEmpty()) {
          diameterAnswer.addAvps(diameterAVPs);
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "No diameter AVPs created for common Application");
        } 
        List<String> tempVendorIds = DiameterDictionary.getInstance().getVendorIDs();
        tempVendorIds.remove("0");
        List<String> vendorIds = new ArrayList<>(tempVendorIds);
        if (vendorIds != null)
          vendorIds.retainAll(this.peer.getSupportedVendors()); 
        if (!Collectionz.isNullOrEmpty(vendorIds))
          for (String vendorIdStr : vendorIds) {
            IDiameterAVP supportedVendorId = DiameterDictionary.getInstance().getAttribute("0:265");
            supportedVendorId.setInteger(Integer.parseInt(vendorIdStr));
            diameterAnswer.addAvp(supportedVendorId);
          }  
      } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
        LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "No common application found for peer = " + diameterPacket
            .getAVPValue("0:264"));
      } 
      if (resultCode == ResultCode.DIAMETER_SUCCESS) {
    	  	LogManager.getLogger().info("DIAMETER-STATE-MACHINE", "ResultCode is 2001"); 
			IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
			resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS
			diameterAnswer.addAvp(resultCodeAvp);
			setPeerInitConnection(true);
      } else if (resultCode.category == ResultCodeCategory.RC3XXX) {
    	  LogManager.getLogger().info("DIAMETER-STATE-MACHINE", "ResultCode is Protocol Errors"); 
        diameterAnswer.setErrorBit();
      }else if (resultCode == ResultCode.DIAMETER_NO_COMMON_APPLICATION) {
    	  LogManager.getLogger().info("DIAMETER-STATE-MACHINE", "ResultCode is DIAMETER_NO_COMMON_APPLICATION"); 
    	  IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
          resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS
          diameterAnswer.addAvp(resultCodeAvp);
      }
      addAdditionalAVPs(this.peer.getAdditionalCERAvps(), (DiameterPacket)diameterAnswer);
      
      sendBasePacket((DiameterPacket)diameterAnswer);
    } catch (IOException ioExc) {
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Diameter Packet sending failed for Peer: " + this.peer.getPeerName() + ". Reason: " + ioExc.getMessage());
    } 
  }
  
  public void atomicActionISndCEA(StateEvent event) {
    sendCEA(event, ResultCode.DIAMETER_SUCCESS);
  }
  
  public void atomicActionSndCER(StateEvent event) {
    this.numberOfConnectionAttempts.set(0L);
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    NetworkConnectionHandler connectionListener = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (!this.peer.isPeerConnected())
      this.peer.setConnectionListener(connectionListener); 
    DiameterRequest diameterRequest = new DiameterRequest();
    diameterRequest.setCommandCode(257);
    diameterRequest.setApplicationID(0L);
    diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
    diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());
    diameterRequest.addAvp("0:257", this.peer.getLocalBoundAddress());
    diameterRequest.addAvp("0:266", String.valueOf(Parameter.getInstance().getVendorId()));
    diameterRequest.addAvp("0:269", Parameter.getInstance().getProductName());
    List<String> supportedVendors = DiameterDictionary.getInstance().getVendorIDs();
    supportedVendors.remove("0");
    diameterRequest.addAvp("0:265", supportedVendors);
    diameterRequest.addAvp("0:278", String.valueOf(Parameter.getInstance().getOriginStateId()));
    this.peer.addSecurityAVP((DiameterPacket)diameterRequest);
    Set<ApplicationEnum> localApplicationIdentifiers = this.peer.getApplications();
    List<IDiameterAVP> diameterAVPs = createApplicationIdAVPs(localApplicationIdentifiers);
    if (diameterAVPs != null && !diameterAVPs.isEmpty())
      diameterRequest.addAvps(diameterAVPs); 
    List<IDiameterAVP> cerAvps = this.peer.getAdditionalCERAvps();
    addAdditionalAVPs(cerAvps, (DiameterPacket)diameterRequest);
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Sending CER to " + this.peer.getPeerName() + diameterRequest); 
    try {
      sendBasePacket((DiameterPacket)diameterRequest);
    } catch (IOException ioExc) {
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Diameter Packet sending failed for Peer: " + this.peer.getPeerName() + ". Reason: " + ioExc.getMessage());
      LogManager.ignoreTrace(ioExc);
    } 
    if (this.peer.isSessionCleanUpOnCER()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + ".Reason: session clean up on CER is enabled"); 
      releasePeerSessions(diameterRequest);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + " Skipped.Reason: session clean up on CER is disabled");
    } 
  }
  
  public void atomicActionSndConnReq(StateEvent event) {
    this.numberOfConnectionAttempts.incrementAndGet();
    this.stackContext.getNetworkConnector(this.peer.getTransportProtocol()).openConnection((IPeerListener)this.peer);
  }
  
  public long getTimeoutConnectionAttempts() {
    return this.numberOfConnectionAttempts.get();
  }
  
  public void atomicActionRSndDPA(StateEvent event) {
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    DiameterRequest diameterRequest = (DiameterRequest)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
    IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
    resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
    diameterAnswer.addAvp(resultCode);
    addAdditionalAVPs(this.peer.getAdditionalDPRAvps(), (DiameterPacket)diameterAnswer);
    try {
      sendBasePacket((DiameterPacket)diameterAnswer);
    } catch (IOException e) {
      throw new UnhandledTransitionException(e);
    } 
    if (this.peer.isSessionCleanUpOnDPR()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + ".Reason: session clean up on DPR is enabled"); 
      releasePeerSessions(diameterRequest);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
    } 
  }
  
  public void atomicActionRSndDPR(StateEvent event, DiameterPeerEvent peerEvent) {
    DiameterRequest diameterRequest = new DiameterRequest();
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    diameterRequest.setCommandCode(CommandCode.DISCONNECT_PEER.code);
    diameterRequest.setRequestBit();
    IDiameterAVP disconnectionCauseAVP = DiameterDictionary.getInstance().getAttribute("0:273");
    if (stateTransitionData != null) {
      String reason = (String)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DISCONNECT_REASON);
      if ("MALFORMED_PACKET".equals(reason)) {
        disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
      } else if (peerEvent == DiameterPeerEvent.Stop) {
        disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
      } else {
        disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
      } 
    } else if (peerEvent == DiameterPeerEvent.Stop) {
      disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
    } else {
      disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
    } 
    ArrayList<IDiameterAVP> avpList = new ArrayList<>();
    avpList.add(disconnectionCauseAVP);
    diameterRequest.addAvps(avpList);
    diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
    diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());
    List<IDiameterAVP> additionalDPRAvps = this.peer.getAdditionalDPRAvps();
    addAdditionalAVPs(additionalDPRAvps, (DiameterPacket)diameterRequest);
    try {
      sendBasePacket((DiameterPacket)diameterRequest);
    } catch (IOException e) {
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Error in sending DPR to Peer: " + getPeerName());
    } 
    if (this.peer.isSessionCleanUpOnDPR()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + ".Reason: session clean up on DPR is enabled"); 
      releasePeerSessions(diameterRequest);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
    } 
  }
  
  public void atomicActionISndDWA(StateEvent event) {
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    DiameterRequest diameterRequest = (DiameterRequest)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
    IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
    resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
    diameterAnswer.addAvp(resultCode);
    addAdditionalAVPs(this.peer.getAdditionalDWRAvps(), (DiameterPacket)diameterAnswer);
    try {
      sendBasePacket((DiameterPacket)diameterAnswer);
    } catch (IOException e) {
      throw new UnhandledTransitionException(e);
    } 
  }
  
  public void atomicActionRSndDWA(StateEvent event) {
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    DiameterRequest diameterRequest = (DiameterRequest)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
    IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
    resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
    ArrayList<IDiameterAVP> avpList = new ArrayList<>();
    avpList.add(resultCode);
    diameterAnswer.addAvps(avpList);
    addAdditionalAVPs(this.peer.getAdditionalDWRAvps(), (DiameterPacket)diameterAnswer);
    try {
      sendBasePacket((DiameterPacket)diameterAnswer);
    } catch (IOException e) {
      throw new UnhandledTransitionException(e);
    } 
  }
  
  public void atomicActionSndMessage(StateEvent event) {
    try {
      IStateTransitionData stateTransitionData = event.getStateTransitionData();
      DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_PACKET_TO_SEND);
      if (diameterPacket.isRequest()) {
        ResponseListener listener = (ResponseListener)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.RESPONSE_LISTENER);
        sendRequest(diameterPacket.getAsDiameterRequest(), listener);
      } else {
        if (this.peer.getPeerData().isReTransmissionCompliant())
          this.duplicateMessageHandler.decorate((DiameterAnswer)diameterPacket); 
        sendAnswer(diameterPacket.getAsDiameterAnswer());
      } 
    } catch (IOException e) {
      throw new UnhandledTransitionException(e);
    } 
  }
  
  protected List<State> createStates() {
    List<State> result = new ArrayList<>();
    DiameterPeerStateMachineContext diameterPeerStateMachineContext = (DiameterPeerStateMachineContext)getStateMachineContext();
    result.add(DiameterPeerState.Closed.ordinal(), new PeerStateClosed(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.Wait_Conn_Ack.ordinal(), new PeerStateWaitConnAck(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.Wait_I_CEA.ordinal(), new PeerStateWaitICEA(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.Elect.ordinal(), new PeerStateWaitConnAckElect(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.Wait_Returns.ordinal(), new PeerStateWaitReturn(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.R_Open.ordinal(), new PeerStateROpen(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.I_Open.ordinal(), new PeerStateIOpen(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.Closing.ordinal(), new PeerStateClosing(this, diameterPeerStateMachineContext));
    result.add(DiameterPeerState.Wait_Conn_Ack_Elect.ordinal(), new PeerStateWaitConnAck(this, diameterPeerStateMachineContext));
    return result;
  }
  
  public void act() {}
  
  public void atomicActionISndDPA(StateEvent event) {
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    DiameterRequest diameterRequest = (DiameterRequest)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
    IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
    resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
    ArrayList<IDiameterAVP> avpList = new ArrayList<>();
    avpList.add(resultCode);
    diameterAnswer.addAvps(avpList);
    addAdditionalAVPs(this.peer.getAdditionalDPRAvps(), (DiameterPacket)diameterAnswer);
    try {
      sendBasePacket((DiameterPacket)diameterAnswer);
    } catch (IOException e) {
      throw new UnhandledTransitionException(e);
    } 
    if (this.peer.isSessionCleanUpOnDPR()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + ".Reason: session clean up on DPR is enabled"); 
      releasePeerSessions(diameterRequest);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
    } 
  }
  
  public void atomicActionISndDPR(StateEvent event, DiameterPeerEvent diameterPeerEvent) {
    DiameterRequest diameterRequest = new DiameterRequest();
    diameterRequest.setCommandCode(CommandCode.DISCONNECT_PEER.code);
    IDiameterAVP disconnectionCauseAVP = DiameterDictionary.getInstance().getAttribute("0:273");
    IStateTransitionData stateTransitionData = event.getStateTransitionData();
    if (stateTransitionData != null) {
      String reason = (String)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DISCONNECT_REASON);
      if ("MALFORMED_PACKET".equals(reason)) {
        disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
      } else if (diameterPeerEvent == DiameterPeerEvent.Stop) {
        disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
      } else {
        disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
      } 
    } else if (diameterPeerEvent == DiameterPeerEvent.Stop) {
      disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
    } else {
      disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
    } 
    ArrayList<IDiameterAVP> avpList = new ArrayList<>();
    avpList.add(disconnectionCauseAVP);
    diameterRequest.addAvps(avpList);
    List<IDiameterAVP> additionalDPRAvps = this.peer.getAdditionalDPRAvps();
    addAdditionalAVPs(additionalDPRAvps, (DiameterPacket)diameterRequest);
    diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
    diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());
    try {
      sendBasePacket((DiameterPacket)diameterRequest);
    } catch (IOException e) {
      LogManager.getLogger().error("DIAMETER-STATE-MACHINE", "Error in sending DPR to Peer: " + getPeerName());
    } 
    if (this.peer.isSessionCleanUpOnDPR()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + ".Reason: session clean up on DPR is enabled"); 
      releasePeerSessions(diameterRequest);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Releasing sessions for peer: " + this.peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
    } 
  }
  
  public final void atomicActionElect(StateEvent event) {
    String localHostName = Parameter.getInstance().getOwnDiameterIdentity();
    StringBuilder localHost = new StringBuilder(localHostName);
    StringBuilder remoteHost = new StringBuilder(this.peer.getHostIdentity());
    int lengthDiff = localHost.length() - remoteHost.length();
    if (lengthDiff < 0) {
      localHost.append(padding, 0, -lengthDiff);
    } else if (lengthDiff > 0) {
      remoteHost.append(padding, 0, lengthDiff);
    } 
  }
  
  class DiameterPeerStateMachineContext extends BaseStateMachineContext implements IPeerStateMachineContext {
    public IPeerListener getPeerListener() {
      return (IPeerListener)DiameterPeerStateMachine.this.peer;
    }
  }
  
  public void onConnectionUp() {
    this.peer.onConnectionUp();
  }
  
  protected String getKey() {
    return "DIAMETER-STATE-MACHINE";
  }
  
  protected IStateMachineContext createStateMachineContext() {
    return (IStateMachineContext)new DiameterPeerStateMachineContext();
  }
  
  public class DiameterPeerStateMachineListener implements IStateMachineListener {
    public void stateSwitched(IStateEnum oldState, IStateEnum newState) {
      if (newState == DiameterPeerState.R_Open || newState == DiameterPeerState.I_Open) {
        Stack.generateAlert(StackAlertSeverity.INFO, DiameterStackAlerts.DIAMETER_PEER_UP, "DIAMETER-STATE-MACHINE", DiameterPeerStateMachine.this
            
            .peer.getPeerName() + " is up, current state: " + DiameterPeerStateMachine.this.currentState.toString(), 0, DiameterPeerStateMachine.this.peer.getPeerName() + "(" + DiameterPeerStateMachine.this.currentState.toString() + ")");
      } else if (newState == DiameterPeerState.Closed) {
        Stack.generateAlert(StackAlertSeverity.CRITICAL, DiameterStackAlerts.DIAMETER_PEER_DOWN, "DIAMETER-STATE-MACHINE", DiameterPeerStateMachine.this
            
            .peer.getPeerName() + " is down, current state: " + DiameterPeerStateMachine.this.currentState.toString(), 0, DiameterPeerStateMachine.this.peer.getPeerName() + "(" + DiameterPeerStateMachine.this.currentState.toString() + ")");
      } 
    }
  }
  
  public IStateMachineListener getStateMachineListener() {
    return new DiameterPeerStateMachineListener();
  }
  
  private IStateTransitionData getStateTransitionData() {
    IStateTransitionData stateTransitionData = new IStateTransitionData() {
        Map<IStateTransitionDataCode, Object> data = new HashMap<>();
        
        public Object getData(IStateTransitionDataCode key) {
          return this.data.get(key);
        }
        
        public void addObject(IStateTransitionDataCode key, Object value) {
          this.data.put(key, value);
        }
      };
    return stateTransitionData;
  }
  
  public void startTimeoutEventTimer() {
    this.stackContext.scheduleSingleExecutionTask((SingleExecutionAsyncTask)new PeerTimeoutTask());
  }
  
  protected List<IDiameterAVP> createApplicationIdAVPs(Set<ApplicationEnum> diameterApplicationIdentifiers) {
    List<IDiameterAVP> diameterAVPs = new ArrayList<>();
    for (ApplicationEnum applicationIdentifier : diameterApplicationIdentifiers) {
      if (applicationIdentifier.getVendorId() <= 0L) {
        setAvpByApplicationType(diameterAVPs, applicationIdentifier);
        continue;
      } 
      AvpGrouped vendorSpeAppId = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute("0:260");
      if (vendorSpeAppId == null) {
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Skipping Capability Exchange for Vendor Id(" + applicationIdentifier
            .getVendorId() + "). Reason: " + "Vendor-Specific-Application-Id" + " AVP not found from Diameter Dictionary");
        continue;
      } 
      IDiameterAVP vendorId = DiameterDictionary.getInstance().getAttribute("0:266");
      if (vendorId == null) {
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Skipping Capability Exchange for Vendor Id(" + applicationIdentifier
            .getVendorId() + "). Reason: " + "0:266" + " AVP not found from Diameter Dictionary");
        continue;
      } 
      vendorId.setStringValue(String.valueOf(applicationIdentifier.getVendorId()));
      vendorSpeAppId.addSubAvp(vendorId);
      switch (applicationIdentifier.getApplicationType()) {
        case ACCT:
          checkAuthentication(diameterAVPs, vendorSpeAppId, createAcctApplicationAVP(applicationIdentifier.getApplicationId()));
        case AUTH:
          checkAuthentication(diameterAVPs, vendorSpeAppId, createAuthApplicationAVP(applicationIdentifier.getApplicationId()));
        case BOTH:
          checkAccountingAndAuth(diameterAVPs, applicationIdentifier, vendorSpeAppId);
      } 
    } 
    return diameterAVPs;
  }
  
  private void setAvpByApplicationType(List<IDiameterAVP> diameterAVPs, ApplicationEnum applicationIdentifier) {
    IDiameterAVP acctAppIdAvp;
    IDiameterAVP authAppIdAvp;
    switch (applicationIdentifier.getApplicationType()) {
      case ACCT:
        acctAppIdAvp = createAcctApplicationAVP(applicationIdentifier.getApplicationId());
        if (acctAppIdAvp != null)
          diameterAVPs.add(acctAppIdAvp); 
        break;
      case AUTH:
        authAppIdAvp = createAuthApplicationAVP(applicationIdentifier.getApplicationId());
        if (authAppIdAvp != null)
          diameterAVPs.add(authAppIdAvp); 
        break;
      case BOTH:
        setAppID(diameterAVPs, applicationIdentifier);
        break;
    } 
  }
  
  private void setAppID(List<IDiameterAVP> diameterAVPs, ApplicationEnum applicationIdentifier) {
    IDiameterAVP appIdAvp = createAuthApplicationAVP(applicationIdentifier.getApplicationId());
    if (appIdAvp != null)
      diameterAVPs.add(appIdAvp); 
    appIdAvp = createAcctApplicationAVP(applicationIdentifier.getApplicationId());
    if (appIdAvp != null)
      diameterAVPs.add(appIdAvp); 
  }
  
  private void checkAuthentication(List<IDiameterAVP> diameterAVPs, AvpGrouped vendorSpeAppId, IDiameterAVP authApplicationAVP) {
    IDiameterAVP acctIDAvp = authApplicationAVP;
    if (acctIDAvp != null)
      vendorSpeAppId.addSubAvp(acctIDAvp); 
    if (vendorSpeAppId.getGroupedAvp().size() > 1)
      diameterAVPs.add(vendorSpeAppId); 
  }
  
  private void checkAccountingAndAuth(List<IDiameterAVP> diameterAVPs, ApplicationEnum applicationIdentifier, AvpGrouped vendorSpeAppId) {
    AvpGrouped vendorSpeAppId2 = null;
    try {
      vendorSpeAppId2 = (AvpGrouped)vendorSpeAppId.clone();
    } catch (CloneNotSupportedException e) {
      LogManager.getLogger().trace("DIAMETER-STATE-MACHINE", e);
      return;
    } 
    IDiameterAVP appIDAvp = createAuthApplicationAVP(applicationIdentifier.getApplicationId());
    if (appIDAvp != null)
      vendorSpeAppId.addSubAvp(appIDAvp); 
    diameterAVPs.add(vendorSpeAppId);
    appIDAvp = createAcctApplicationAVP(applicationIdentifier.getApplicationId());
    if (appIDAvp != null)
      vendorSpeAppId2.addSubAvp(appIDAvp); 
    diameterAVPs.add(vendorSpeAppId2);
  }
  
  private IDiameterAVP createAuthApplicationAVP(long authAppIdVal) {
    if (authAppIdVal >= 0L) {
      IDiameterAVP authAppId = DiameterDictionary.getInstance().getKnownAttribute("0:258");
      if (authAppId != null) {
        authAppId.setStringValue(String.valueOf(authAppIdVal));
        return authAppId;
      } 
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Error in creating 0:258. Reason: 0:258 AVP not found from Diameter Dictionary");
    } else {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Error in creating 0:258. Reason: 0:258 must be greater then ZERO");
    } 
    return null;
  }
  
  private IDiameterAVP createAcctApplicationAVP(long acctAppIdVal) {
    if (acctAppIdVal >= 0L) {
      IDiameterAVP acctAppId = DiameterDictionary.getInstance().getKnownAttribute("0:259");
      if (acctAppId != null) {
        acctAppId.setStringValue(String.valueOf(acctAppIdVal));
        return acctAppId;
      } 
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Error in creating 0:259. Reason: 0:259 AVP not found from Diameter Dictionary");
    } else {
      LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Error in creating 0:259. Reason: 0:259 must be greater then ZERO");
    } 
    return null;
  }
  
  private class PeerTimeoutTask extends BaseSingleExecutionAsyncTask {
    private IStateEnum state = DiameterPeerStateMachine.this.currentState();
    
    public void execute(AsyncTaskContext context) {
      try {
        if (this.state == DiameterPeerStateMachine.this.currentState()) {
          IStateTransitionData stateTransitionData = DiameterPeerStateMachine.this.getStateTransitionData();
          stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, DiameterPeerEvent.Timeout);
          DiameterPeerStateMachine.this.onStateTransitionTrigger(stateTransitionData);
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "Peer: " + DiameterPeerStateMachine.this.getPeerName() + ", Old State : " + this.state + " : New State : " + DiameterPeerStateMachine.this.currentState() + " not going to execute time-out task");
        } 
      } catch (Exception e) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Peer: " + DiameterPeerStateMachine.this.getPeerName() + ", " + e.getMessage()); 
      } 
    }
    
    public long getInitialDelay() {
      return (DiameterPeerStateMachine.this.peer.getPeerTimeout() / 1000);
    }
  }
  
  public void onConnectionDown() {
    this.peer.onConnectionDown();
  }
  
  public void releasePeerSessions(DiameterRequest baseRequest) {
    long sessionCount = this.stackContext.releasePeerSessions(baseRequest);
    if (LogManager.getLogger().isWarnLogLevel())
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Removed " + sessionCount + " Sessions for HostIdenity :" + this.peer.getPeerName()); 
  }
  
  private void setOriginStateID(int originStateId) {
    this.originStateId = originStateId;
  }
  
  private int getOriginStateId() {
    return this.originStateId;
  }
  
  public final void atomicActionProcessDuplicateConnection(StateEvent stateEvent) {
    NetworkConnectionHandler duplicateConnection = (NetworkConnectionHandler)stateEvent.getStateTransitionData().getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (duplicateConnection == null) {
      LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", "Duplicate Connection handler is null for peer: " + this.peer.getPeerName());
      return;
    } 
    if (LogManager.getLogger().isInfoLogLevel())
      LogManager.getLogger().info("DIAMETER-STATE-MACHINE", "Duplicate connection request arrived for Peer: " + 
          getPeerName()); 
    try {
      this.duplicateConnectionPolicy.checkAcceptable(duplicateConnection);
      atomicActionError(stateEvent, ConnectionEvents.FORCE_CLOSE);
      ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
      if (atomicActionRAccept(stateEvent))
        resultCode = atomicActionProcessCER(stateEvent); 
      atomicActionRSndCEA(stateEvent, resultCode);
    } catch (UnhandledTransitionException ex) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("DIAMETER-STATE-MACHINE", ex.getMessage()); 
      LogManager.getLogger().trace((Throwable)ex);
      atomicActionRReject((NetworkConnectionHandler)stateEvent.getStateTransitionData().getData((IStateTransitionDataCode)PeerDataCode.CONNECTION));
    } 
  }
  
  private class DefaultPolicy implements DuplicateConnectionPolicy {
    private DefaultPolicy() {}
    
    public void checkAcceptable(NetworkConnectionHandler duplicateConnection) {
      if (!isEligibleForAcceptingDuplicateConnection())
        throw new UnhandledTransitionException("Rejecting duplicate connection request of Peer: " + DiameterPeerStateMachine.this
            .getPeerName() + ", Reason: Request arrived in less than 3000ms."); 
      if (DiameterPeerStateMachine.this.peer.getWatchdogInterval() > 0L) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("DIAMETER-STATE-MACHINE", "DWR triggered for Peer: " + DiameterPeerStateMachine.this.getPeerName()); 
        DiameterPeerStateMachine.this.triggerDWR();
        throw new UnhandledTransitionException("Rejecting duplicate connection request of Peer: " + DiameterPeerStateMachine.this
            .getPeerName());
      } 
      if (DiameterPeerStateMachine.this.peer.isTrafficObservedOnCurrentConnection())
        throw new UnhandledTransitionException("Connection request from IP-Address: " + duplicateConnection
            .getSourceIpAddress() + ":" + duplicateConnection.getSourcePort() + " is not acceptable, Reason: Traffic is observed from old Connection"); 
    }
    
    private boolean isEligibleForAcceptingDuplicateConnection() {
      return (DiameterPeerStateMachine.this.timesource.currentTimeInMillis() - DiameterPeerStateMachine.this.peer.getPeerLastConnectionRequestTime() > 3000L);
    }
  }
  
  private class DiscardOldPolicy implements DuplicateConnectionPolicy {
    private DiscardOldPolicy() {}
    
    public void checkAcceptable(NetworkConnectionHandler duplicateConnection) {
      if (DiameterPeerStateMachine.this.peer.isTrafficObservedOnCurrentConnection())
        throw new UnhandledTransitionException("Connection request from IP-Address: " + duplicateConnection
            .getSourceIpAddress() + ":" + duplicateConnection.getSourcePort() + " is not acceptable, Reason: Traffic is observed from old Connection"); 
      LogManager.getLogger().trace("DIAMETER-STATE-MACHINE", "Discard Old policy applied successfully for peer: " + DiameterPeerStateMachine.this.peer.getPeerName());
    }
  }
  
  public void reload() {
    this.duplicateConnectionPolicy = createDuplicateConnectionPolicy();
  }
  
  public abstract void sendBasePacket(DiameterPacket paramDiameterPacket) throws IOException;
  
  public abstract void sendRequest(DiameterRequest paramDiameterRequest, ResponseListener paramResponseListener) throws IOException;
  
  public abstract void sendAnswer(DiameterAnswer paramDiameterAnswer) throws IOException;
  
  public abstract void addAdditionalAVPs(List<IDiameterAVP> paramList, DiameterPacket paramDiameterPacket);
  
  protected abstract void setPeerInitConnection(boolean paramBoolean);
  
  protected abstract void triggerDWR();
  
  private static interface DuplicateConnectionPolicy {
    void checkAcceptable(NetworkConnectionHandler param1NetworkConnectionHandler);
  }
}
