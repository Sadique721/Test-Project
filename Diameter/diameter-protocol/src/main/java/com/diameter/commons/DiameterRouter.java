package com.diameter.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiameterRouter {
  private static final String MODULE = "DIA-RTR";
  
  private RouterContext routerContext;
  
  private IDiameterStackContext stackContext;
  
  private List<RoutingEntry> routingEntryList;
  
  private Map<String, RoutingEntry> routingEntryMap;
  
  private List<RoutingEntryData> routingEntryDataList;
  
  private ProxyAgent proxyAgent;
  
  private RedirectAgent redirectAgent;
  
  private RelayAgent relayAgent;
  
  private VirtualAgent virtualAgent;
  
  private VirtualInputStream virtualInputStream;
  
  private final String virtualPeerName = "VirtualRoutingPeer";
  
  private Map<String, List<IDiameterAVP>> translationDummyMappings;
  
  private Set<ApplicationEnum> supportedRemoteApplications;
    
  private String routingTableName;
  
  private ITranslationAgent translationAgent;
  
  private IDiameterSessionManager diameterSessionManager = null;
  
  public DiameterRouter(IDiameterStackContext stackContext, List<RoutingEntryData> routingEntryDataList) {
    this(stackContext, routingEntryDataList, (ITranslationAgent)TranslationAgent.getInstance());
  }
  
  public DiameterRouter(IDiameterStackContext stackContext, List<RoutingEntryData> routingEntryDataList, ITranslationAgent translationAgent) {
    this.stackContext = stackContext;
    this.routingEntryList = new ArrayList<>();
    this.routingEntryMap = new HashMap<>();
    this.routingEntryDataList = routingEntryDataList;
    this.translationDummyMappings = new HashMap<>();
    this.translationAgent = translationAgent;
    createRouterContext();
  }
  
  public void init() {
    initRoutingEntries();
    initAgents();
    registerVirtualRoutingPeer();
  }
  
  private void createRouterContext() {
    this.routerContext = new RouterContext() {
        public PeerData getPeerData(String hostIdentity) {
          return DiameterRouter.this.getPeerData(hostIdentity);
        }
        
        public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
          return DiameterRouter.this.getPeerCommunicator(hostIdentity);
        }
        
        public String getVirtualRoutingPeerName() {
          return "VirtualRoutingPeer";
        }
        
        public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity, String realmName, RoutingActions routeAction) {
          DiameterRouter.this.stackContext.updateUnknownH2HDropStatistics(answer, hostIdentity, realmName, routeAction);
        }
        
        public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, String hostIdentity, String realmName, RoutingActions routeAction) {
          DiameterRouter.this.stackContext.updateDiameterStatsPacketDroppedStatistics(packet, hostIdentity, realmName, routeAction);
        }
        
        public void updateRealmInputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
          DiameterRouter.this.stackContext.updateRealmInputStatistics(packet, realmName, routeAction);
        }
        
        public void updateRealmOutputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
          DiameterRouter.this.stackContext.updateRealmOutputStatistics(packet, realmName, routeAction);
        }
        
        public void postRequestRouting(DiameterRequest originRequest, DiameterRequest destinationRequest, String originPeerId, String destPeerId, String routingEntryName) {
          DiameterRouter.this.postRequestRouting(originRequest, destinationRequest, originPeerId, destPeerId, routingEntryName);
        }
        
        public void preAnswerRouting(DiameterRequest originRequest, DiameterRequest destinationRequest, DiameterAnswer originAnswer, String originPeerId, String routingEntryName) {
          DiameterRouter.this.preAnswerRouting(originRequest, destinationRequest, originAnswer, originPeerId, routingEntryName);
        }
        
        public void postAnswerRouting(DiameterRequest originRequest, DiameterRequest destinationRequest, DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, String originPeerId, String destPeerId, String routingEntryName) {
          DiameterRouter.this.postAnswerRouting(originRequest, destinationRequest, originAnswer, destinationAnswer, originPeerId, destPeerId, routingEntryName);
        }
        
        public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException {
          return DiameterRouter.this.stackContext.getDiameterCDRDriver(name);
        }
        
        public void updateRealmTimeoutRequestStatistics(DiameterRequest destinationRequest, String realmName, RoutingActions routingAction) {
          DiameterRouter.this.stackContext.updateRealmTimeoutRequestStatistics(destinationRequest, realmName, routingAction);
        }
        
        public RoutingEntry getRoutingEntry(String routingEntryName) {
          return (RoutingEntry)DiameterRouter.this.routingEntryMap.get(routingEntryName);
        }
      };
  }
  
  private void registerVirtualRoutingPeer() {
    PeerDataImpl peerData = new PeerDataImpl();
    peerData.setPeerName("VirtualRoutingPeer");
    peerData.setHostIdentity("virtual.routing.diameter.peer");
    peerData.setRemoteIPAddress("localhost");
    peerData.setInitiateConnectionDuration(Integer.valueOf(0));
    peerData.setWatchdogInterval(0);
    VirtualOutputStream outpurStream = new VirtualOutputStream() {
        public void send(Packet diaPacket) {
          DiameterRequest diameterRequest = (DiameterRequest)diaPacket;
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("DIA-RTR", "Request received by VirtualRoutingPeer" + diameterRequest.toString()); 
          DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
          String translatorName = (String)diameterRequest.getParameter("SELECTED_TRANSLATION_POLICY");
          if (translatorName != null) {
            List<IDiameterAVP> dummyResponseAVPs = DiameterRouter.this.translationDummyMappings.get(translatorName);
            if (dummyResponseAVPs == null)
              dummyResponseAVPs = DiameterRouter.this.formDummyAnswerAVPs(translatorName); 
            if (dummyResponseAVPs != null)
              diameterAnswer.addAvps(dummyResponseAVPs); 
          } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
            LogManager.getLogger().info("DIA-RTR", "Translation mapping not provided for Dummy Answer with Session-ID: " + diameterRequest
                .getAVPValue("0:263"));
          } 
          diameterRequest.setParameter("DUMMY_MAPPING", null);
          DiameterRouter.this.sendVirtualAnswer(diameterAnswer);
        }
      };
    try {
      this.virtualInputStream = this.stackContext.registerVirtualPeer(peerData, outpurStream).getInputStream();
    } catch (ElementRegistrationFailedException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIA-RTR", "Registration of Diameter Routing virtual peer failed. Reason: " + e.getMessage()); 
      LogManager.ignoreTrace(e);
    } 
  }
  
  private void sendVirtualAnswer(DiameterAnswer diameterAnswer) {
    if (this.virtualInputStream != null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIA-RTR", "Sending Answer to Diameter stack " + diameterAnswer.toString()); 
      this.virtualInputStream.received((Packet)diameterAnswer);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("DIA-RTR", "Diameter Virtual Routing Inputstream unavailable");
    } 
  }
  
  private List<IDiameterAVP> formDummyAnswerAVPs(String transMapName) {
    Map<String, String> dummyMappings = this.translationAgent.getDummyResponseMap(transMapName);
    if (dummyMappings == null || dummyMappings.isEmpty()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DIA-RTR", "No Dummy Mappings defined for translation policy: " + transMapName); 
      return null;
    } 
    List<IDiameterAVP> dummyAVPs = new ArrayList<>();
    for (Map.Entry<String, String> mapping : dummyMappings.entrySet()) {
      IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(mapping.getKey());
      if (diameterAVP != null) {
        diameterAVP.setStringValue(mapping.getValue());
        dummyAVPs.add(diameterAVP);
        continue;
      } 
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DIA-RTR", "Dummy attribute: " + (String)mapping.getKey() + "defined for translation policy: " + transMapName + " is not available in Dictionary"); 
    } 
    return dummyAVPs;
  }
  
  private void initAgents() {
    this.proxyAgent = new ProxyAgent(this.routerContext, this.translationAgent, this.diameterSessionManager);
    this.relayAgent = new RelayAgent(this.routerContext, this.diameterSessionManager);
    this.redirectAgent = new RedirectAgent(this.routerContext);
    this.virtualAgent = new VirtualAgent(this.routerContext, this.diameterSessionManager);
  }
  
  private void initRoutingEntries() {
    this.routingTableName = Parameter.getInstance().getRoutingTableName();
    if (LogManager.getLogger().isInfoLogLevel())
      LogManager.getLogger().info("DIA-RTR", "Routing Entry initialization started for Routing Table: " + this.routingTableName); 
    ArrayList<RoutingEntry> routingEntryList = new ArrayList<>();
    HashMap<String, RoutingEntry> routingEntryMap = new HashMap<>();
    for (RoutingEntryData routingEntryData : this.routingEntryDataList) {
      if (routingEntryData == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
          LogManager.getLogger().error("DIA-RTR", "Routing Entry Initialization failed. Reason: Routing Entry Data not Found."); 
        continue;
      } 
      try {
        RoutingEntry routingEntry = new RoutingEntry(routingEntryData, this.routerContext, this.translationAgent);
        routingEntry.init();
        routingEntryList.add(routingEntry);
        routingEntryMap.put(routingEntryData.getRoutingName(), routingEntry);
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIA-RTR", "Routing Entry: " + routingEntry.getRoutingEntryName() + " initialized."); 
      } catch (InitializationFailedException e) {
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("DIA-RTR", "Routing Entry: " + routingEntryData.getRoutingName() + " will not be considered, as it failed to initalize. Reason: " + e
              .getMessage()); 
        LogManager.getLogger().trace("DIA-RTR", (Throwable)e);
      } 
    } 
    this.routingEntryList = routingEntryList;
    this.routingEntryMap = routingEntryMap;
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("DIA-RTR", "All Routing Entry initialization completed."); 
  }
  
  public Set<ApplicationEnum> getSupportedRemoteApplications() {
    if (this.supportedRemoteApplications == null) {
      Set<ApplicationEnum> remoteApplications = new HashSet<>();
      for (int i = 0; i < this.routingEntryList.size(); i++) {
        ApplicationEnum[] apps = ((RoutingEntry)this.routingEntryList.get(i)).getSupportedApplications();
        if (apps != null)
          for (int j = 0; j < apps.length; j++)
            remoteApplications.add(apps[j]);  
      } 
      this.supportedRemoteApplications = remoteApplications;
    } 
    return this.supportedRemoteApplications;
  }
  
  public RoutingActions processDiameterRequest(DiameterRequest diameterRequest, DiameterSession diameterSession) throws RoutingFailedException {
    if (diameterRequest.isServerInitiated())
      return processServerInitiatedRequest(diameterRequest, diameterSession); 
    return processClientInitiatedDiameterRequest(diameterRequest, diameterSession);
  }
  
  private RoutingActions processClientInitiatedDiameterRequest(DiameterRequest diameterRequest, DiameterSession diameterSession) throws RoutingFailedException {
    String sessionID = diameterRequest.getAVPValue("0:263");
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DIA-RTR", "Diameter Packet received for Session-Id=" + sessionID); 
    RoutingActions action = RoutingActions.PROXY;
    action = processRequest(diameterRequest, diameterSession);
    return action;
  }
  
  private RoutingActions processRequest(DiameterRequest diameterRequest, DiameterSession diameterSession) throws RoutingFailedException {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DIA-RTR", "Processing Diameter Request for Session-Id=" + diameterSession
          .getSessionId()); 
    
    RoutingActions action = RoutingActions.PROXY;
    RoutingEntry routingEntry = selectRoutingEntry(diameterRequest);
    if (routingEntry != null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DIA-RTR", "Routing Entry " + routingEntry.getRoutingEntryName() + " Selected for Session-ID=" + diameterSession
            .getSessionId()); 
      action = submitRequestToAgent(routingEntry, diameterSession, diameterRequest);
    } else if (isEligibleForLocalProcess(diameterRequest)) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DIA-RTR", "Locally Processing Diameter Request for Session-Id=" + diameterSession
            .getSessionId()); 
      action = RoutingActions.LOCAL;
    } else {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIA-RTR", "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + ", Reason: Routing entry not found for Diameter Request with Session-ID=" + diameterSession
            
            .getSessionId()); 
      throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER, RoutingActions.LOCAL, "Route not found");
    } 
    return action;
  }
  
  private RoutingEntry selectRoutingEntry(DiameterRequest diameterRequest) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DIA-RTR", "Selecting Routing Entry for Diameter Request with Session-ID=" + diameterRequest
          .getAVPValue("0:263")); 
    for (int i = 0; i < this.routingEntryList.size(); i++) {
      if (((RoutingEntry)this.routingEntryList.get(i)).isApplicable((DiameterPacket)diameterRequest))
        return this.routingEntryList.get(i); 
    } 
    return null;
  }
  
  private RoutingActions submitRequestToAgent(RoutingEntry routingEntry, DiameterSession diameterSession, DiameterRequest diameterRequest) throws RoutingFailedException {
    RoutingActions action = routingEntry.getRoutingAction();
    if (!routingEntry.isRoutingEntryExecutable())
      throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_COMPLY, action, diameterRequest, DiameterErrorMessageConstants.routingFailed(routingEntry.getRoutingEntryName())); 
    if (action == RoutingActions.LOCAL) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DIA-RTR", "Locally Processing Diameter Request for Session-Id=" + diameterSession
            .getSessionId()); 
      return RoutingActions.LOCAL;
    } 
    PeerData originPeer = this.stackContext.getPeerData(diameterRequest.getRequestingHost());
    if (originPeer != null)
      this.stackContext.updateRealmInputStatistics((DiameterPacket)diameterRequest, originPeer
          .getRealmName(), routingEntry
          .getRoutingAction()); 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DIA-RTR", "Submitting Diameter Request to Diameter Agent for Session-Id=" + diameterSession
          .getSessionId()); 
    switch (action) {
      case PROXY:
        this.proxyAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
        return action;
      case REDIRECT:
        this.redirectAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
        return action;
      case RELAY:
        this.relayAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
        return action;
      case VIRTUAL:
        this.virtualAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
        return action;
    } 
    LogManager.getLogger().error("DIA-RTR", "Invalid routing action selected for Session-ID=" + diameterRequest.getAVPValue("0:263"));
    return action;
  }
  
  private boolean isEligibleForLocalProcess(DiameterRequest diameterRequest) {
    /*if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DIA-RTR", "Checking for Local processing Eligibility of Diameter Request with Session-Id=" + diameterRequest
          .getAVPValue("0:263")); 
    String destRealm = diameterRequest.getAVPValue("0:283");
    if (destRealm != null && 
      !destRealm.equalsIgnoreCase(Parameter.getInstance().getOwnDiameterRealm()))
      return false; 
    String destHost = diameterRequest.getAVPValue("0:293");
    if (destHost != null && 
      !destHost.equalsIgnoreCase(Parameter.getInstance().getOwnDiameterIdentity()))
      return false; */
    return true;
  }
  
  public PeerData getPeerData(String hostIdentity) {
    return this.stackContext.getPeerData(hostIdentity);
  }
  
  public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
    PeerData peerdata = this.stackContext.getPeerData(hostIdentity);
    if (peerdata == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DIA-RTR", "Peer " + hostIdentity + " is not available."); 
      return null;
    } 
    DiameterPeerCommunicator peerCommunicator = this.stackContext.getPeerCommunicator(hostIdentity);
    return peerCommunicator;
  }
  
  
  private void postRequestRouting(DiameterRequest originRequest, DiameterRequest destinationRequest, String originPeerId, String destPeerId, String routingEntryName) {
     
  }
  
  private void preAnswerRouting(DiameterRequest originRequest, DiameterRequest destinationRequest, DiameterAnswer originAnswer, String originPeerId, String routingEntryName) {
    
  }
  
  private void postAnswerRouting(DiameterRequest originRequest, DiameterRequest destinationRequest, DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, String originPeerId, String destPeerId, String routingEntryName) {
    
  }
  
  public void registerDiameterSessionManager(IDiameterSessionManager diameterSessionManager) {
    this.diameterSessionManager = diameterSessionManager;
  }
  
  public void reInit(List<RoutingEntryData> routingEntryDataList) {
    this.routingEntryDataList = routingEntryDataList;
    initRoutingEntries();
  }
  
  public void registerPriorityRoutingEntry(RoutingEntryData routingEntryData) {
    try {
      RoutingEntry routingEntry = new RoutingEntry(routingEntryData, this.routerContext, this.translationAgent);
      routingEntry.init();
      this.routingEntryDataList.add(0, routingEntryData);
      this.routingEntryList.add(0, routingEntry);
      if (LogManager.getLogger().isDebugLogLevel())
        LogManager.getLogger().debug("DIA-RTR", "Routing Entry: " + routingEntry.getRoutingEntryName() + " initialized."); 
    } catch (Exception e) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("DIA-RTR", "Error occured while initializing Routing Entry: " + routingEntryData.getRoutingName() + ", it will not be added, Reason: " + e
            .getMessage()); 
      LogManager.getLogger().trace("DIA-RTR", e);
    } 
  }
  
  private RoutingActions processServerInitiatedRequest(DiameterRequest diameterRequest, DiameterSession session) throws RoutingFailedException {
    String sessionID = diameterRequest.getAVPValue("0:263");
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("DIA-RTR", "Processing Server Initiated Request for Session-Id=" + sessionID); 
    String routingEntryName = (String)session.getParameter("ROUTING_ENTRY");
    RoutingEntry routingEntry = this.routerContext.getRoutingEntry(routingEntryName);
    if (routingEntry == null) {
      if (LogManager.getLogger().isInfoLogLevel())
        LogManager.getLogger().info("DIA-RTR", "Locally Processing Server Initiated Request with Session-ID=" + sessionID + ". Reason, Routing Session not found."); 
      return RoutingActions.LOCAL;
    } 
    
    if (LogManager.getLogger().isInfoLogLevel())
      LogManager.getLogger().info("DIA-RTR", "Routing Entry " + routingEntry.getRoutingEntryName() + " Selected for Session-ID=" + diameterRequest
          .getSessionID()); 
    return submitServerInitiatedRequestToAgent(routingEntry, session, diameterRequest);
  }
  
  private RoutingActions submitServerInitiatedRequestToAgent(RoutingEntry routingEntry, DiameterSession routingSession, DiameterRequest diameterRequest) throws RoutingFailedException {
    RoutingActions action = routingEntry.getRoutingAction();
    if (action == RoutingActions.LOCAL) {
      if (LogManager.getLogger().isInfoLogLevel())
        LogManager.getLogger().info("DIA-RTR", "Locally Processing Diameter Request for Session-Id=" + routingSession
            .getSessionId()); 
      return RoutingActions.LOCAL;
    } 
    PeerData originPeer = this.stackContext.getPeerData(diameterRequest.getRequestingHost());
    if (originPeer != null)
      this.stackContext.updateRealmInputStatistics((DiameterPacket)diameterRequest, originPeer
          .getRealmName(), routingEntry
          .getRoutingAction()); 
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("DIA-RTR", "Submitting Diameter Request to Diameter Agent for Session-Id=" + routingSession
          .getSessionId()); 
    switch (action) {
      case PROXY:
        this.proxyAgent.routeServerInitiatedRequest(diameterRequest, routingSession);
        return action;
      case RELAY:
        this.relayAgent.routeServerInitiatedRequest(diameterRequest, routingSession);
        return action;
      case VIRTUAL:
        this.virtualAgent.routeServerInitiatedRequest(diameterRequest, routingSession);
        return action;
    } 
    LogManager.getLogger().error("DIA-RTR", "Invalid routing action selected for Session-ID=" + diameterRequest.getAVPValue("0:263"));
    return action;
  }
}
