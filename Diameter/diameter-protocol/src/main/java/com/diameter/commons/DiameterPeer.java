package com.diameter.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;

import com.diameter.stack.Stack;

public class DiameterPeer implements IPeerListener {
  private static final String MODULE = "PEER";
  
  public static final int DUPLICATE_CONNECTION_ALLOWED_INTERVAL = 3000;
  
  private static ThreadLocal<SimpleDateFormat> simpleDateFormatPool = new ThreadLocal<SimpleDateFormat>() {
      protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss S");
      }
    };
  
  private List<String> hostIPAddresses = null;
  
  private int intVendorId = 0;
  
  private String strProductName = null;
  
  private int intFirmwareRevision;
  
  private long lExpirationTime;
  
  private Set<InbandSecurityId> localInbandSecurityIds;
  
  private Set<InbandSecurityId> remoteInbandSecurityIds;
  
  private List<String> supportedVendors;
  
  private DiameterPeerStateMachine peerStateMachine;
  
  private int iSessionIdHigherValue;
  
  private int iSessionIdLowerValue;
  
  private AtomicBoolean isAlive;
  
  private int loadBalanceCount = 1;
  
  private int loadBalnceCounter;
  
  private boolean initConnection = true;
  
  private boolean localPeer = false;
  
  private volatile PeerData peerData;
  
  private DiameterPCBStateMachine pcbStateMachine;
  
  private IDiameterStackContext stackContext;
  
  private NetworkConnectionHandler connectionHandler;
  
  @Nonnull
  private RetransmissionHandler retransmissionHandler;
  
  private List<DiameterPeerStatusListener> peerStatusListeners;
  
  private AtomicLong tranlationSeq;
  
  private ScheduledThreadPoolExecutor scheduleThreadPoolExecutor;
  
  private DiameterPeerConfig peerConfig;
  
  private PeerApplicationProvider peerApplicationProvider;
  
  private long lastConnectionAttemptedTimestamp;
  
  private AtomicLong messageTxCount;
  
  private long maxResponseTimeMs = 100L;
  
  @Nonnull
  private TimeSource timesource;
  
  public DiameterPeer(PeerData peerData, IDiameterStackContext stackContext, DiameterRouter diameterRouter, SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler appMessageHandler, ExplicitRoutingHandler explicitRoutingHandler, DuplicateDetectionHandler duplicateMessageHandler) {
    this(peerData, stackContext, diameterRouter, sessionFactoryManager, appMessageHandler, explicitRoutingHandler, duplicateMessageHandler, TimeSource.systemTimeSource(), new PeerApplicationProvider(stackContext, peerData));
  }
  
  @VisibleForTesting
  DiameterPeer(PeerData peerData, IDiameterStackContext stackContext, DiameterRouter diameterRouter, SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler appMessageHandler, ExplicitRoutingHandler explicitRoutingHandler, DuplicateDetectionHandler duplicateMessageHandler, final TimeSource timeSource, PeerApplicationProvider peerApplicationProvider) {
    readMaxResponseTimeProperties();
    this.stackContext = stackContext;
    this.peerData = peerData;
    this.peerStateMachine = new DiameterPeerStateMachine(this, stackContext, diameterRouter, sessionFactoryManager, appMessageHandler, (IStateEnum)DiameterPeerState.Closed, explicitRoutingHandler, new OverloadHandler(), duplicateMessageHandler, timeSource) {
        protected String getPeerName() {
          return DiameterPeer.this.getPeerName();
        }
        
        public void addAdditionalAVPs(List<IDiameterAVP> additionalAvps, DiameterPacket diameterPacket) {
          DiameterPeer.this.addAdditionalAVPs(additionalAvps, diameterPacket);
        }
        
        protected void setPeerInitConnection(boolean peerInitConnection) {
          DiameterPeer.this.setInitConnection(peerInitConnection);
        }
        
        protected void triggerDWR() {
          DiameterPeer.this.pcbStateMachine.onTimerElapsed();
        }
        
        public void sendRequest(DiameterRequest request, ResponseListener listener) throws IOException {
          if (CommandCode.DEVICE_WATCHDOG.code != request.getCommandCode())
            DiameterPeer.this.retransmissionHandler.addPacket(request, listener); 
          try {
            DiameterPeer.this.writeToStream((DiameterPacket)request);
          } catch (IOException ex) {
            DiameterPeer.this.retransmissionHandler.removePacket(request.getHop_by_hopIdentifier());
            throw ex;
          } 
        }
        
        public void sendAnswer(DiameterAnswer answer) throws IOException {
          if (answer.getRequestReceivedTime() > 0L) {
            long requestProcessingTime = timeSource.currentTimeInMillis() - answer.getRequestReceivedTime();
            if (requestProcessingTime > 100L) {
              if (LogManager.getLogger().isWarnLogLevel())
                LogManager.getLogger().warn("PEER", (new StringBuilder(200))
                    .append("Peer: ")
                    .append(getPeerName())
                    .append(", Diameter Request Processing Time: ")
                    .append(requestProcessingTime)
                    .append("ms for Session-ID=")
                    .append(answer.getAVPValue("0:263"))
                    .append(", Packet-Type: ")
                    .append(answer.getCommandCode())
                    .append(", HbH-ID=")
                    .append(answer.getHop_by_hopIdentifier())
                    .append(", E2E-ID=")
                    .append(answer.getEnd_to_endIdentifier())
                    .toString()); 
              Stack.generateAlert(StackAlertSeverity.INFO, DiameterStackAlerts.DIAMETER_HIGH_RESPONSE_TIME, "PEER", (new StringBuilder(200))
                  
                  .append("Diameter High Response Time : ")
                  .append(requestProcessingTime)
                  .append("ms to Peer: ")
                  .append(getPeerName())
                  .append(", for HbH-ID=")
                  .append(answer.getHop_by_hopIdentifier())
                  .append(", E2E-ID=")
                  .append(answer.getEnd_to_endIdentifier()).toString(), (int)requestProcessingTime, DiameterPeer.this
                  .getHostIdentity());
            } else if (LogManager.getLogger().isInfoLogLevel()) {
              LogManager.getLogger().info("PEER", (new StringBuilder(200))
                  .append("Peer: ")
                  .append(getPeerName())
                  .append(", Diameter Request Processing Time: ")
                  .append(requestProcessingTime)
                  .append("ms")
                  .append(" for Session-ID=")
                  .append(answer.getAVPValue("0:263"))
                  .append(", Packet-Type: ")
                  .append(answer.getCommandCode()).toString());
            } 
          } else if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug("PEER", "Peer: " + 
                
                getPeerName() + " Diameter Request Processing Time can not be calculated." + 
                " Reason: Request received time not available for sessionID=" + 
                answer
                .getAVPValue("0:263") + ", Packet-Type: " + 
                answer
                .getCommandCode());
          } 
          DiameterPeer.this.writeToStream((DiameterPacket)answer);
        }
        
        public void sendBasePacket(DiameterPacket basePacket) throws IOException {
          if (basePacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode())
            if (basePacket.isRequest()) {
              if (LogManager.getLogger().isInfoLogLevel())
                LogManager.getLogger().info("PEER", "Sending DWR to " + getPeerName() + " " + DiameterPeer.this
                    .getHostIPAddresses()); 
            } else if (LogManager.getLogger().isInfoLogLevel()) {
              LogManager.getLogger().info("PEER", "Sending DWA to " + getPeerName() + " " + DiameterPeer.this
                  .getHostIPAddresses());
            }  
          DiameterPeer.this.writeToStream(basePacket);
        }
      };
    this.tranlationSeq = new AtomicLong(1L);
    this.hostIPAddresses = new ArrayList<>();
    this.localInbandSecurityIds = new TreeSet<>();
    this.remoteInbandSecurityIds = new TreeSet<>();
    this.supportedVendors = new ArrayList<>();
    this.iSessionIdHigherValue = (int)(new Date()).getTime();
    this.iSessionIdLowerValue = 0;
    this.pcbStateMachine = new DiameterPCBStateMachine(peerData.getWatchdogInterval(), peerData.getInitiateConnectionDuration(), stackContext);
    this.isAlive = new AtomicBoolean(false);
    this.peerStatusListeners = new ArrayList<>();
    this.peerApplicationProvider = peerApplicationProvider;
    this.scheduleThreadPoolExecutor = new ScheduledThreadPoolExecutor(5, (ThreadFactory)new EliteThreadFactory("DIA-STACK", peerData.getPeerName() + "-PEER-SCH", 10));
    this.scheduleThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    this.retransmissionHandler = new RetransmissionHandler();
    this.messageTxCount = new AtomicLong();
    this.timesource = timeSource;
    setLocalSecurity();
  }
  
  private void readMaxResponseTimeProperties() {
    String property = System.getProperty("max.response.time");
    if (Objects.nonNull(property))
      this.maxResponseTimeMs = Numbers.parseLong(property, this.maxResponseTimeMs); 
  }
  
  public void init() throws InitializationFailedException {
    this.peerConfig = createPeerConfig();
    this.peerApplicationProvider.init();
  }
  
  private void setLocalSecurity() {
    if (this.peerData.getSecurityStandard() == SecurityStandard.NONE) {
      this.localInbandSecurityIds.add(InbandSecurityId.NO_INBAND_SECURITY);
    } else if (this.peerData.getSecurityStandard() == SecurityStandard.RFC_3588_TLS) {
      this.localInbandSecurityIds.add(InbandSecurityId.TLS);
    } else if (this.peerData.getSecurityStandard() == SecurityStandard.RFC_3588_DYNAMIC) {
      this.localInbandSecurityIds.add(InbandSecurityId.TLS);
      this.localInbandSecurityIds.add(InbandSecurityId.NO_INBAND_SECURITY);
    } 
  }
  
  public void attemptConnection() {
    if (isInitiateConnection()) {
      if (getRemoteInetAddress() == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("PEER", "Unabled to attempt connection with Peer: " + this.peerData.getPeerName() + ". Reason: Invalid RemoteInetAddress"); 
        return;
      } 
      try {
        IStateTransitionData stateTransitionData = getStateTransitionData();
        stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, DiameterPeerEvent.Start);
        this.peerStateMachine.onStateTransitionTrigger(stateTransitionData);
      } catch (UnhandledTransitionException e) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("PEER", "Connection Initialization failed with Peer: " + this.peerData.getPeerName() + ". Reason: " + e.getMessage()); 
      } 
    } 
  }
  
  public boolean isInitiateConnection() {
    return (this.initConnection && this.peerData.getInitiateConnectionDuration() > 0);
  }
  
  private void setInitConnection(boolean initConnection) {
    this.initConnection = initConnection;
  }
  
  public void addSupportedVendor(long intVendorId) {
    this.supportedVendors.add(String.valueOf(intVendorId));
  }
  
  public List<String> getSupportedVendors() {
    return this.supportedVendors;
  }
  
  public boolean isValidSupporedVendor(int intVendorId) {
    return this.supportedVendors.contains(String.valueOf(intVendorId));
  }
  
  public void setHostIPAddress(List<String> hostIPAddress) {
    if (hostIPAddress != null && !hostIPAddress.isEmpty())
      this.hostIPAddresses = hostIPAddress; 
  }
  
  public List<String> getHostIPAddresses() {
    return this.hostIPAddresses;
  }
  
  public boolean isValidHostIPAddress(String hostAddress) {
    return this.hostIPAddresses.contains(hostAddress);
  }
  
  public void setVendorId(int intVendorId) {
    this.intVendorId = intVendorId;
  }
  
  public int getVendorId() {
    return this.intVendorId;
  }
  
  public void setProductName(String strProductName) {
    this.strProductName = strProductName;
  }
  
  public String getProductName() {
    return this.strProductName;
  }
  
  public void setFirmwareRevision(int intFirmwareRevision) {
    this.intFirmwareRevision = intFirmwareRevision;
  }
  
  public int getFirmwareRevision() {
    return this.intFirmwareRevision;
  }
  
  public String getRealmName() {
    return this.peerData.getRealmName();
  }
  
  public EliteSSLContextExt createEliteSSLContext() {
    EliteSSLContextExt eliteSSLContext = null;
    try {
      EliteSSLContextFactory sslContextFactory = this.stackContext.getEliteSSLContextFactory();
      if (sslContextFactory == null) {
        LogManager.getLogger().warn("PEER", "Could not create sslContext for Peer: " + this.peerData.getPeerName() + ". Reason: SSLContext factory not provide");
        return null;
      } 
      eliteSSLContext = sslContextFactory.createSSLContext(this.peerData.getSSLParameter());
      eliteSSLContext.getTrustManager().setCertificateSubjectCnChecker(new DiameterCertificateSubjectCnChecker(!this.peerData.getSSLParameter().isValidateSubjectCN(), this.peerData));
    } catch (Exception ex) {
      LogManager.getLogger().warn("PEER", "TLS connection not possible for diameterPeer: " + this.peerData.getPeerName() + ". Reason: " + ex.getMessage());
      LogManager.getLogger().trace("PEER", ex);
    } 
    return eliteSSLContext;
  }
  
  public void addRemoteSecurityId(InbandSecurityId inbandSecurityId) {
    this.remoteInbandSecurityIds.add(inbandSecurityId);
  }
  
  public Set<InbandSecurityId> getCommonSecurityIds() {
    Set<InbandSecurityId> commonInbandSecurityIds = new TreeSet<>();
    if (this.localInbandSecurityIds.contains(InbandSecurityId.TLS) && this.remoteInbandSecurityIds.contains(InbandSecurityId.TLS)) {
      commonInbandSecurityIds.add(InbandSecurityId.TLS);
    } else if (this.localInbandSecurityIds.contains(InbandSecurityId.NO_INBAND_SECURITY) && this.remoteInbandSecurityIds.contains(InbandSecurityId.NO_INBAND_SECURITY)) {
      commonInbandSecurityIds.add(InbandSecurityId.NO_INBAND_SECURITY);
    } 
    return commonInbandSecurityIds;
  }
  
  public String getNextSessionId() {
    String strSessionId = Parameter.getInstance().getOwnDiameterIdentity() + ";" + this.iSessionIdHigherValue + ";" + this.iSessionIdLowerValue;
    if (this.iSessionIdLowerValue == Integer.MAX_VALUE) {
      this.iSessionIdLowerValue = 0;
      this.iSessionIdHigherValue++;
    } else {
      this.iSessionIdLowerValue++;
    } 
    return strSessionId;
  }
  
  public String getNextSessionId(String strOptionalValue) {
    String strSessionId = Parameter.getInstance().getOwnDiameterIdentity() + ";" + this.iSessionIdHigherValue + ";" + this.iSessionIdLowerValue;
    if (this.iSessionIdLowerValue == Integer.MAX_VALUE) {
      this.iSessionIdLowerValue = 0;
      this.iSessionIdHigherValue++;
    } else {
      this.iSessionIdLowerValue++;
    } 
    return strSessionId + ";" + strOptionalValue;
  }
  
  public long getExpirationTime() {
    return this.lExpirationTime;
  }
  
  public void setExpirationTime(long expirationTime) {
    this.lExpirationTime = expirationTime * 1000L;
  }
  
  public String getHostIdentity() {
    return this.peerData.getHostIdentity();
  }
  
  public String getPeerName() {
    return this.peerData.getPeerName();
  }
  
  public boolean isSessionCleanUpOnCER() {
    return this.peerData.isSessionCleanUpOnCER();
  }
  
  public boolean isSessionCleanUpOnDPR() {
    return this.peerData.isSessionCleanUpOnDPR();
  }
  
  public IStateEnum currentState() {
    if (this.peerStateMachine != null)
      return this.peerStateMachine.currentState(); 
    return null;
  }
  
  public DiameterPeerStateMachine getPeerStateMachine() {
    return this.peerStateMachine;
  }
  
  public int getPCBState() {
    return this.pcbStateMachine.getCurrentState();
  }
  
  public int getPeerState() {
    return this.peerStateMachine.getCurrentState();
  }
  
  public long getPeerStateChangedDuration() {
    return this.peerStateMachine.getStateDuration();
  }
  
  public void setPeerStateMachine(DiameterPeerStateMachine peerStateMachine) {
    this.peerStateMachine = peerStateMachine;
  }
  
  protected void writeToStream(DiameterPacket packet) throws IOException {
    boolean destHostReplaced = false;
    boolean destRealmReplaced = false;
    IDiameterAVP destHost = packet.getAVP("0:293");
    IDiameterAVP destRealm = packet.getAVP("0:283");
    if (destHost != null && "*".equals(destHost.getStringValue())) {
      destHost.setStringValue(this.peerData.getHostIdentity());
      destHostReplaced = true;
    } 
    if (destRealm != null && "*".equals(destRealm.getStringValue())) {
      destRealm.setStringValue(this.peerData.getRealmName());
      destRealmReplaced = true;
    } 
    this.stackContext.finalPreResponseProcess(packet);
    long currentTimeMillis = this.timesource.currentTimeInMillis();
    if (LogManager.getLogger().isInfoLogLevel()) {
      LogManager.getLogger().info("PEER", "Sending Packet to: " + getPeerName() + " " + 
          getHostIPAddresses() + packet.toString());
    } 
    packet.setSendTime(currentTimeMillis);
    try {
      this.connectionHandler.send((Packet)packet);
      this.stackContext.updateOutputStatistics(packet, getHostIdentity());
    } finally {
      if (destHostReplaced)
        destHost.setStringValue("*"); 
      if (destRealmReplaced)
        destRealm.setStringValue("*"); 
    } 
  }
  
  public void closeConnection(ConnectionEvents event) {
    try {
      if (event == ConnectionEvents.TIMER_EXPIRED)
        handleEvent((IEventEnum)DiameterPeerEvent.Timeout, event); 
    } catch (Exception ex) {
      LogManager.getLogger().error("PEER", "Error in processiong timeout event for peer = " + getPeerName());
      LogManager.getLogger().trace("PEER", ex);
    } 
    if (isPeerConnected())
      if (event == ConnectionEvents.FORCE_CLOSE) {
        this.connectionHandler.terminateConnection();
      } else {
        this.connectionHandler.closeConnection(event);
      }  
    this.peerApplicationProvider.clear();
    this.remoteInbandSecurityIds.clear();
    this.supportedVendors.clear();
    this.hostIPAddresses.clear();
  }
  
  public boolean isPeerConnected() {
    return (this.connectionHandler != null && this.connectionHandler.isConnected());
  }
  
  public void setConnectionListener(NetworkConnectionHandler connectionHandler) {
    if (connectionHandler == null || !connectionHandler.isConnected()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", "Connection-Handler not used for Peer: " + getPeerName() + ", Reason: Connection-Handler is null"); 
      return;
    } 
    if (this.connectionHandler != null && this.connectionHandler.isConnected()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", "Connection-Handler(" + connectionHandler
            .getSourceIpAddress() + ":" + connectionHandler.getSourcePort() + ") not used for Peer: " + 
            getPeerName() + ", Reason: Connection-Handler (" + this.connectionHandler
            
            .getSourceIpAddress() + ":" + this.connectionHandler.getSourcePort() + ") already exists"); 
      return;
    } 
    this.connectionHandler = connectionHandler;
    recordMessageTxCountAndConnectionTime();
    DiameterNWConnectionEventListener connectionEventListener = new DiameterNWConnectionEventListener(this);
    this.connectionHandler.addNetworkConnectionEventListener((NetworkConnectionEventListener)connectionEventListener);
  }
  
  public Set<ApplicationEnum> getCommonApplications() {
    return this.peerApplicationProvider.getCommonApplications();
  }
  
  public Set<ApplicationEnum> getRemoteApplications() {
    return this.peerApplicationProvider.getRemoteApplications();
  }
  
  public Set<ApplicationEnum> getApplications() {
    return this.peerApplicationProvider.getApplications();
  }
  
  private class RetransmissionHandler {
    private ConcurrentHashMap<Integer, DiameterRequestWrapper> requestMap = new ConcurrentHashMap<>(1024, 0.75F, DiameterPeer.this.stackContext.getMaxWorkerThreads());
    
    public void addPacket(DiameterRequest diameterRequest, ResponseListener listener) {
      long timeoutTimeMillis = DiameterPeer.this.timesource.currentTimeInMillis() + DiameterPeer.this.peerData.getRequestTimeout();
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER", "RequestTimeout will be on " + ((SimpleDateFormat)DiameterPeer.simpleDateFormatPool.get()).format(Long.valueOf(timeoutTimeMillis)) + " for diameter request with H2HId: " + diameterRequest
            .getHop_by_hopIdentifier() + " to peer: " + DiameterPeer.this
            .peerData.getHostIdentity()); 
      DiameterRequestWrapper diameterRequestWrapper = new DiameterRequestWrapper(diameterRequest, listener);
      this.requestMap.put(Integer.valueOf(diameterRequest.getHop_by_hopIdentifier()), diameterRequestWrapper);
      TimeoutTask timeoutTask = new TimeoutTask(DiameterPeer.this.peerData.getRequestTimeout(), diameterRequest.getHop_by_hopIdentifier(), diameterRequest.getAVPValue("0:263"));
      ScheduledFuture<?> scheduledFuture = DiameterPeer.this.scheduleThreadPoolExecutor.schedule(timeoutTask, timeoutTask.getInitialDelay(), timeoutTask.getTimeUnit());
      diameterRequestWrapper.setScheduleTask(scheduledFuture);
    }
    
    public DiameterRequestWrapper removePacket(int hopByHopIdentifier) {
      DiameterRequestWrapper diameterRequestWrapper = this.requestMap.remove(Integer.valueOf(hopByHopIdentifier));
      if (diameterRequestWrapper == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("PEER", "Diameter request with H2HId: " + hopByHopIdentifier + " for peer: " + DiameterPeer.this
              .peerData.getHostIdentity() + "  already removed from pending requests"); 
        return null;
      } 
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER", "Diameter request with H2HId: " + hopByHopIdentifier + " for  peer: " + DiameterPeer.this
            .peerData.getHostIdentity() + " removed from pending requests"); 
      if (diameterRequestWrapper.getScheduleTask() != null)
        diameterRequestWrapper.getScheduleTask().cancel(true); 
      return diameterRequestWrapper;
    }
    
    private class DiameterRequestWrapper {
      private DiameterRequest diameterRequest;
      
      private ResponseListener listener;
      
      private int retryCount;
      
      private ScheduledFuture<?> scheduledTask;
      
      public DiameterRequestWrapper(DiameterRequest diameterRequest, ResponseListener listener) {
        this.diameterRequest = diameterRequest;
        this.listener = listener;
        this.retryCount = 0;
      }
      
      public ResponseListener getListener() {
        return this.listener;
      }
      
      public DiameterRequest getDiameterRequest() {
        return this.diameterRequest;
      }
      
      public int getRetryCount() {
        return this.retryCount;
      }
      
      public void increamentRetryCount() {
        this.retryCount++;
      }
      
      public ScheduledFuture<?> getScheduleTask() {
        return this.scheduledTask;
      }
      
      public void setScheduleTask(ScheduledFuture<?> task) {
        this.scheduledTask = task;
      }
    }
    
    private class TimeoutTask implements Runnable {
      private long timeoutInterval;
      
      private int hopByHopId;
      
      private String sessionID;
      
      public TimeoutTask(long retryInterval, int hopByHopId, String sessionID) {
        this.timeoutInterval = retryInterval;
        this.hopByHopId = hopByHopId;
        this.sessionID = sessionID;
      }
      
      public long getInitialDelay() {
        return this.timeoutInterval;
      }
      
      public TimeUnit getTimeUnit() {
        return TimeUnit.MILLISECONDS;
      }
      
      public void run() {
        try {
          DiameterPeer.RetransmissionHandler.DiameterRequestWrapper diameterRequestWrapper = (DiameterPeer.RetransmissionHandler.DiameterRequestWrapper)DiameterPeer.RetransmissionHandler.this.requestMap.get(Integer.valueOf(this.hopByHopId));
          if (diameterRequestWrapper == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
              LogManager.getLogger().debug("PEER", "Skipping execution of  Timeout Task for diameter reqeust with H2HId: " + this.hopByHopId + ", sessionID: " + this.sessionID + " for peer: " + DiameterPeer.this
                  .peerData.getHostIdentity() + ". Reason: diameter request removed from pending requests"); 
            return;
          } 
          DiameterRequest diameterRequest = diameterRequestWrapper.getDiameterRequest();
          DiameterPeer.this.stackContext.addMDC((DiameterPacket)diameterRequest);
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("PEER", "No response from server " + DiameterPeer.this.getHostIdentity() + " for Session-Id= " + this.sessionID + ", App-Id=" + diameterRequest
                
                .getApplicationID() + ", Packet-Type=" + diameterRequest
                .getCommandCode() + ", H2H-ID: " + this.hopByHopId + ", E2E-ID: " + diameterRequest
                
                .getEnd_to_endIdentifier()); 
          if (diameterRequestWrapper.getRetryCount() >= DiameterPeer.this.peerData.getRetryCount()) {
            handleTimeoutRequest(diameterRequestWrapper);
            return;
          } 
          diameterRequestWrapper.increamentRetryCount();
          if (DiameterPeer.this.getPeerState() != DiameterPeerState.I_Open.stateOrdinal() && DiameterPeer.this.getPeerState() != DiameterPeerState.R_Open.stateOrdinal()) {
            LogManager.getLogger().warn("PEER", "Unable to retrasmite diameter request with H2HId: " + this.hopByHopId + ", sessionID: " + this.sessionID + " to peer: " + DiameterPeer.this
                .getHostIdentity() + ". Reason: peer is not in OPEN state");
            DiameterPeer.this.stackContext.updateTimeoutRequestStatistics(diameterRequest, DiameterPeer.this.getHostIdentity());
            diameterRequestWrapper.setScheduleTask(DiameterPeer.this.scheduleThreadPoolExecutor.schedule(this, getInitialDelay(), getTimeUnit()));
            return;
          } 
          retry(diameterRequestWrapper);
          diameterRequestWrapper.setScheduleTask(DiameterPeer.this.scheduleThreadPoolExecutor.schedule(this, getInitialDelay(), getTimeUnit()));
        } finally {
          DiameterPeer.this.stackContext.clearMDC();
        } 
      }
      
      private void retry(DiameterPeer.RetransmissionHandler.DiameterRequestWrapper diameterRequestWrapper) {
        DiameterRequest diameterRequest = diameterRequestWrapper.getDiameterRequest();
        try {
          diameterRequest.setReTransmittedBit();
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("PEER", "Retransmitting diameter request with H2HId: " + this.hopByHopId + ", sessionID: " + this.sessionID + " to peer: " + DiameterPeer.this
                .getHostIdentity() + ". Attempt: " + diameterRequestWrapper.getRetryCount() + ". Remaining Attempts: " + (DiameterPeer.this
                .peerData.getRetryCount() - diameterRequestWrapper.getRetryCount())); 
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("PEER", "Next request timeout will be on " + ((SimpleDateFormat)DiameterPeer.simpleDateFormatPool.get()).format(Long.valueOf(DiameterPeer.this.timesource.currentTimeInMillis() + DiameterPeer.this.peerData.getRequestTimeout())) + " for diameter request with H2HId: " + diameterRequest
                .getHop_by_hopIdentifier() + " to peer: " + DiameterPeer.this.peerData.getHostIdentity()); 
          DiameterPeer.this.stackContext.updateTimeoutRequestStatistics(diameterRequest, DiameterPeer.this.getHostIdentity());
          DiameterPeer.this.writeToStream((DiameterPacket)diameterRequest);
        } catch (Exception ex) {
          LogManager.getLogger().error("PEER", "Error in retrasmitting diameter request with H2HId: " + this.hopByHopId + ", sessionID: " + this.sessionID + " to Peer: " + DiameterPeer.this
              .getHostIdentity() + ". Reason: " + ex.getMessage());
          LogManager.getLogger().trace("PEER", ex);
        } 
      }
      
      private void handleTimeoutRequest(DiameterPeer.RetransmissionHandler.DiameterRequestWrapper diameterRequestWrapper) {
        DiameterRequest diameterRequest = diameterRequestWrapper.getDiameterRequest();
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("PEER", "Timeout occured for diameter request with H2HId: " + this.hopByHopId + ", sessionID: " + this.sessionID + " for peer: " + DiameterPeer.this
              .peerData.getHostIdentity()); 
        ResponseListener listener = diameterRequestWrapper.getListener();
        if (DiameterPeer.RetransmissionHandler.this.requestMap.remove(Integer.valueOf(this.hopByHopId)) == null)
          return; 
        DiameterPeer.this.stackContext.updateTimeoutRequestStatistics(diameterRequest, DiameterPeer.this.getHostIdentity());
        if (listener != null) {
          try {
            listener.requestTimedout(DiameterPeer.this.getHostIdentity(), (DiameterSession)DiameterPeer.this.stackContext.getOrCreateSession(this.sessionID, diameterRequest.getApplicationID()));
          } catch (Exception ex) {
            LogManager.getLogger().error("PEER", "Error in providing diameter request with H2HId: " + this.hopByHopId + ", sessionID: " + this.sessionID + " for Peer: " + DiameterPeer.this
                .getHostIdentity() + " to timeoutlistener. Reason: " + ex.getMessage());
            LogManager.getLogger().trace("PEER", ex);
          } 
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("PEER", "Drop diameter request with H2HId: " + this.hopByHopId + ", sessionID: " + this.sessionID + " for peer: " + DiameterPeer.this
              .peerData.getHostIdentity() + ". Reason: timeoutlistener not found");
        } 
      }
    }
  }
  
  private class DiameterRequestWrapper {
    private DiameterRequest diameterRequest;
    
    private ResponseListener listener;
    
    private int retryCount;
    
    private ScheduledFuture<?> scheduledTask;
    
    public DiameterRequestWrapper(DiameterRequest diameterRequest, ResponseListener listener) {
      this.diameterRequest = diameterRequest;
      this.listener = listener;
      this.retryCount = 0;
    }
    
    public ResponseListener getListener() {
      return this.listener;
    }
    
    public DiameterRequest getDiameterRequest() {
      return this.diameterRequest;
    }
    
    public int getRetryCount() {
      return this.retryCount;
    }
    
    public void increamentRetryCount() {
      this.retryCount++;
    }
    
    public ScheduledFuture<?> getScheduleTask() {
      return this.scheduledTask;
    }
    
    public void setScheduleTask(ScheduledFuture<?> task) {
      this.scheduledTask = task;
    }
  }
  
  public int getCommunicationPort() {
    return this.peerData.getRemotePort();
  }
  
  public boolean isAlive() {
    return this.isAlive.get();
  }
  
  public int getLoadBalanceCount() {
    return this.loadBalanceCount;
  }
  
  public void setLoadBalanceCount(int loadBalanceCount) {
    this.loadBalanceCount = loadBalanceCount;
    setLoadBalnceCounter(loadBalanceCount);
  }
  
  public int getLoadBalnceCounter() {
    return this.loadBalnceCounter;
  }
  
  public void setLoadBalnceCounter(int loadBalnceCounter) {
    this.loadBalnceCounter = loadBalnceCounter;
  }
  
  public boolean isLocalPeer() {
    return this.localPeer;
  }
  
  public void setLocalPeer(boolean localPeer) {
    this.localPeer = localPeer;
  }
  
  public long getWatchdogInterval() {
    return this.peerData.getWatchdogInterval();
  }
  
  public long getTimeoutConnectionAttempts() {
    return this.peerStateMachine.getTimeoutConnectionAttempts();
  }
  
  public int getPeerTimeout() {
    if (this.pcbStateMachine != null)
      return this.pcbStateMachine.getTimeout(); 
    return 3000;
  }
  
  public void processReceivedDiameterPacket(Packet packet, NetworkConnectionHandler connectionHandler) throws UnhandledTransitionException {
    long currentTime = this.timesource.currentTimeInMillis();
    DiameterPacket diameterPacket = (DiameterPacket)packet;
    IStateTransitionData stateTransitionData = getStateTransitionData();
    stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET, diameterPacket);
    stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.CONNECTION, connectionHandler);
    this.stackContext.updateInputStatistics(diameterPacket, getHostIdentity());
    long idealTime = currentTime - diameterPacket.creationTimeMillis();
    if (!DiameterUtility.isBaseProtocolPacket(diameterPacket.getCommandCode())) {
      if (!isAlive() || !isPeerConnected()) {
        LogManager.getLogger().warn("PEER", "Dropping Packet with HbH-ID=" + diameterPacket
            .getHop_by_hopIdentifier() + "EtE-ID=" + diameterPacket
            .getEnd_to_endIdentifier() + ", Session-ID=" + diameterPacket
            .getAVPValue("0:263") + " Reason, Peer: " + 
            getHostIdentity() + " is Down.");
        this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
        return;
      } 
      if (diameterPacket.isResponse()) {
        convertFromRedirectHostFormat((IStackContext)this.stackContext, diameterPacket, getPeerData().getRedirectHostAVPFormat());
        RetransmissionHandler.DiameterRequestWrapper requestWrapper = this.retransmissionHandler.removePacket(diameterPacket.getHop_by_hopIdentifier());
        if (requestWrapper == null) {
          this.stackContext.updateUnknownH2HDropStatistics((DiameterAnswer)diameterPacket, getHostIdentity());
          LogManager.getLogger().warn("PEER", "Dropping response. Reason: Request was timeout OR Possibly duplicate response for HbH: " + diameterPacket
              .getHop_by_hopIdentifier() + ", sessionID: " + diameterPacket
              .getAVPValue("0:263") + " from peer " + getHostIdentity());
          return;
        } 
        DiameterRequest diameterRequest = requestWrapper.getDiameterRequest();
        stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.DIAMETER_PACKET_TO_SEND, diameterRequest);
        stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.RESPONSE_LISTENER, requestWrapper.listener);
        long requestProcessingTime = this.timesource.currentTimeInMillis() - diameterRequest.getSendTime();
        if (requestProcessingTime > this.maxResponseTimeMs) {
          LogManager.getLogger().warn("PEER", (new StringBuilder(200))
              .append("Diameter High Response Time: ")
              .append(requestProcessingTime)
              .append("ms from Peer: ")
              .append(getPeerName())
              .append(", for Session-ID=")
              .append(diameterPacket.getAVPValue("0:263"))
              .append(", Packet-Type: ")
              .append(diameterPacket.getCommandCode())
              .append(", HbH-ID=")
              .append(diameterPacket.getHop_by_hopIdentifier())
              .append(", E2E-ID=")
              .append(diameterPacket.getEnd_to_endIdentifier()).toString());
          Stack.generateAlert(StackAlertSeverity.INFO, DiameterStackAlerts.DIAMETER_PEER_HIGH_RESPONSE_TIME, "PEER", (new StringBuilder(200))
              
              .append("Diameter High Response Time: ")
              .append(requestProcessingTime)
              .append("ms from Peer: ")
              .append(getPeerName())
              .append(", for HbH-ID=")
              .append(diameterPacket.getHop_by_hopIdentifier())
              .append(", E2E-ID=")
              .append(diameterPacket.getEnd_to_endIdentifier())
              .toString(), (int)requestProcessingTime, getHostIdentity());
        } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
          LogManager.getLogger().info("PEER", "Diameter Response Time: " + requestProcessingTime + "ms from Peer: " + 
              getPeerName() + ", for Session-ID=" + diameterPacket
              .getAVPValue("0:263") + ", Packet-Type: " + diameterPacket
              .getCommandCode());
        } 
      } else {
        DiameterRequest request = (DiameterRequest)diameterPacket;
        request.setPeerData(this.peerData);
        if (idealTime >= this.peerData.getRequestTimeout()) {
          if (this.stackContext.getActionOnOverload() == OverloadAction.DROP) {
            LogManager.getLogger().warn("PEER", (new StringBuilder(200))
                .append("Dropping request,")
                .append(" Package-Type:")
                .append(diameterPacket.getCommandCode())
                .append(". Reason: Request Timeout in queues for HbH: ")
                .append(diameterPacket.getHop_by_hopIdentifier())
                .append(", sessionID: ")
                .append(diameterPacket.getAVPValue("0:263"))
                .append(" from peer ")
                .append(getHostIdentity())
                .append(" and Overload Action is drop")
                .toString());
            this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
            return;
          } 
          int resultCodeOnOverload = this.stackContext.getOverloadResultCode();
          IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
          resultCode.setInteger(resultCodeOnOverload);
          DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket);
          diameterAnswer.addAvp(resultCode);
          try {
            LogManager.getLogger().warn("PEER", (new StringBuilder(200))
                .append("Sending ")
                .append(resultCodeOnOverload)
                .append(" response. Reason: Request Timeout in queues for HbH: ")
                .append(diameterPacket.getHop_by_hopIdentifier())
                .append(", sessionID: ")
                .append(diameterPacket.getAVPValue("0:263"))
                .append(" from peer ")
                .append(getHostIdentity())
                .toString());
            writeToStream((DiameterPacket)diameterAnswer);
          } catch (Exception e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
              LogManager.getLogger().warn("PEER", e.getMessage()); 
            LogManager.getLogger().trace("PEER", e);
            this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
          } 
          return;
        } 
        if (isLoopDetected((DiameterRequest)diameterPacket)) {
          handleLoopedRequest(diameterPacket);
          return;
        } 
      } 
    } 
    diameterPacket.setQueueTime(idealTime);
    if (isSameConnection(connectionHandler))
      try {
        this.pcbStateMachine.onReceive(stateTransitionData);
      } catch (Exception e) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("PEER", e.getMessage()); 
        LogManager.getLogger().trace("PEER", e);
      }  
    try {
      handleStateTransition(stateTransitionData);
    } catch (UnhandledTransitionException e) {
      LogManager.getLogger().trace("PEER", (Throwable)e);
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", e.getMessage()); 
      if (diameterPacket.isRequest()) {
        DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
        try {
          writeToStream((DiameterPacket)diameterAnswer);
          this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
        } catch (IOException io) {
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("PEER", "Unable to send" + CommandCode.fromCode(diameterAnswer.getCommandCode()) + " to " + getHostIdentity() + ". Reason: " + io.getMessage()); 
          LogManager.getLogger().trace("PEER", io);
          this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
        } 
      } 
    } catch (Exception e) {
      LogManager.getLogger().error("PEER", "Peer:" + getPeerName() + " Unknown Error: " + e.getMessage() + ". Sending DIAMETER_UNABLE_TO_COMPLY");
      LogManager.getLogger().trace("PEER", e);
      if (diameterPacket.isRequest()) {
        DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
        try {
          writeToStream((DiameterPacket)diameterAnswer);
          this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
        } catch (IOException io) {
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("PEER", io.getMessage()); 
          LogManager.getLogger().trace("PEER", io);
          this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
        } 
      } 
    } 
  }
  
  private void handleLoopedRequest(DiameterPacket diameterPacket) {
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("PEER", "Reason: Loop detected for Peer: " + getPeerName() + " for Diameter Request with Session-ID=" + diameterPacket
          
          .getAVPValue("0:263") + ", Sending: " + ResultCode.DIAMETER_LOOP_DETECTED); 
    try {
      DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_LOOP_DETECTED);
      writeToStream((DiameterPacket)diameterAnswer);
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", e.getMessage()); 
      LogManager.getLogger().trace("PEER", e);
      this.stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
    } 
  }
  
  private boolean isLoopDetected(DiameterRequest diameterRequest) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PEER", "Checking Diameter Loop for Diameter Request with Session-Id=" + diameterRequest
          .getAVPValue("0:263")); 
    List<IDiameterAVP> routeRecords = diameterRequest.getAVPList("0:282");
    if (routeRecords != null && !routeRecords.isEmpty())
      for (IDiameterAVP routeRecord : routeRecords) {
        if (Parameter.getInstance().getOwnDiameterIdentity().equals(routeRecord.getStringValue()))
          return true; 
      }  
    return false;
  }
  
  private IStateTransitionData getStateTransitionData() {
    return new IStateTransitionData() {
        Map<IStateTransitionDataCode, Object> data = new HashMap<>();
        
        public Object getData(IStateTransitionDataCode key) {
          return this.data.get(key);
        }
        
        public void addObject(IStateTransitionDataCode key, Object value) {
          this.data.put(key, value);
        }
      };
  }
  
  public void handleStateTransition(IStateTransitionData stateTransitionData) throws UnhandledTransitionException {
    try {
      this.peerStateMachine.onStateTransitionTrigger(stateTransitionData);
    } catch (UnhandledTransitionException e) {
      throw e;
    } 
  }
  
  public void handleEvent(IEventEnum eventEnum, ConnectionEvents event, Map<PeerDataCode, String> eventParam) throws UnhandledTransitionException {
    try {
      IStateTransitionData stateTransitionData = getStateTransitionData();
      stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, eventEnum);
      if (eventParam != null)
        for (Map.Entry<PeerDataCode, String> paramEntry : eventParam.entrySet())
          stateTransitionData.addObject((IStateTransitionDataCode)paramEntry.getKey(), paramEntry.getValue());  
      handleStateTransition(stateTransitionData);
    } catch (UnhandledTransitionException e) {
      throw e;
    } 
  }
  
  public void handleEvent(IEventEnum eventEnum, ConnectionEvents event) throws UnhandledTransitionException {
    if (event == ConnectionEvents.HANDSHAKE_FAIL && 
      isInitiateConnection()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", "Initiate Connection with Peer :" + this.peerData.getHostIdentity() + ", IP : " + this.peerData
            .getRemoteIPAddress() + " disabled, Reason: Handshake Failed."); 
      setInitConnection(false);
    } 
    handleEvent(eventEnum, event, null);
  }
  
  public String getRealm() {
    return this.peerData.getRealmName();
  }
  
  private class DiameterPCBStateMachine extends PCBStateMachine {
    private volatile IStateTransitionData sendDwr;
    
    public DiameterPCBStateMachine(long watchDogTimerMs, int isInitiateConnection, IDiameterStackContext stackContext) {
      super(watchDogTimerMs, isInitiateConnection, (IStackContext)stackContext);
    }
    
    public void start() {
      super.start();
      createDWR();
    }
    
    private void createDWR() {
      DiameterRequest dwr = new DiameterRequest();
      dwr.setCommandCode(CommandCode.DEVICE_WATCHDOG.code);
      DiameterPeer.this.addAdditionalAVPs(DiameterPeer.this.peerData.getAdditionalDWRAvps(), (DiameterPacket)dwr);
      IStateTransitionData sendDwr = DiameterPeer.this.getStateTransitionData();
      sendDwr.addObject((IStateTransitionDataCode)PeerDataCode.DIAMETER_PACKET_TO_SEND, dwr);
      sendDwr.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, DiameterPeerEvent.SendMessage);
      this.sendDwr = sendDwr;
    }
    
    public void attemptOpen() {
      if (isInitiateConnection()) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("PEER", "Trying to attempt connection open for peer: " + getPeerName()); 
        DiameterPeer.this.attemptConnection();
      } 
    }
    
    public void closeConnection(ConnectionEvents event) {
      DiameterPeer.this.closeConnection(event);
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", "Connection closed with peer:" + getPeerName()); 
    }
    
    public void sendDPR() {
      DiameterPeer.this.handleEvent((IEventEnum)DiameterPeerEvent.Stop, ConnectionEvents.CONNECTION_DPR);
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", "PCB event for connection close detected, DPR sent to peer: " + getPeerName()); 
    }
    
    public void onConnectionUp() {
      super.onConnectionUp();
      DiameterPeer.this.markOpen();
    }
    
    public void sendWatchdog() {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER", "Generating SendMessage DWR to peer:" + getPeerName()); 
      DiameterPacket dwr = (DiameterPacket)this.sendDwr.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_PACKET_TO_SEND);
      dwr.setHop_by_hopIdentifier(HopByHopPool.get());
      dwr.setEnd_to_endIdentifier(EndToEndPool.get());
      DiameterPeer.this.handleStateTransition(this.sendDwr);
    }
    
    public void failback() {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", "Failback is called for peer:" + getPeerName()); 
      DiameterPeer.this.markOpen();
    }
    
    public void failover() {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER", "Failover is called for peer:" + getPeerName()); 
      DiameterPeer.this.markClosed();
    }
    
    public void throwaway() {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER", "Throwaway is called for peer:" + getPeerName()); 
    }
    
    protected String getPeerName() {
      return DiameterPeer.this.getPeerName();
    }
    
    protected boolean isInitiateConnection() {
      return DiameterPeer.this.isInitiateConnection();
    }
  }
  
  public void onConnectionDown() {
    this.pcbStateMachine.onConnectionDown();
  }
  
  public void onConnectionUp() {
    if (this.peerData.getSecurityStandard() == SecurityStandard.RFC_3588_TLS || (this.peerData
      .getSecurityStandard() == SecurityStandard.RFC_3588_DYNAMIC && this.remoteInbandSecurityIds.contains(InbandSecurityId.TLS)))
      try {
        this.connectionHandler.secureConnection((PeerConnectionData)this.peerData, createEliteSSLContext());
        setInitConnection(true);
      } catch (HandShakeFailException e) {
        LogManager.getLogger().error("PEER", "Error in creating TLS Socket. Reason: " + e.getMessage());
        LogManager.getLogger().trace("PEER", (Throwable)e);
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("PEER", "Initiate Connection with Peer :" + this.peerData.getHostIdentity() + ", IP : " + this.peerData
              .getRemoteIPAddress() + " disabled, Reason: Handshake Failed."); 
        setInitConnection(false);
        closeConnection(ConnectionEvents.HANDSHAKE_FAIL);
        return;
      }  
    this.pcbStateMachine.onConnectionUp();
  }
  
  public boolean stop() {
    if (this.peerStateMachine.getCurrentState() == DiameterPeerState.I_Open.ordinal() || this.peerStateMachine.getCurrentState() == DiameterPeerState.R_Open.ordinal())
      try {
        this.peerStateMachine.stop();
      } catch (Exception ex) {
        LogManager.getLogger().error("PEER", "Error while stopping peer state machine. Reason: " + ex.getMessage());
        LogManager.getLogger().trace("PEER", ex);
      }  
    if (this.scheduleThreadPoolExecutor != null)
      try {
        this.scheduleThreadPoolExecutor.shutdown();
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("PEER", "Waiting for DiameterPeer: " + this.peerData.getPeerName() + " level scheduled async task executor to complete execution"); 
        if (!this.scheduleThreadPoolExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("PEER", "Shutting down DiameterPeer: " + this.peerData.getPeerName() + " level scheduled async task executor forcefully. Reason: Async task taking more than 2 second to complete"); 
          this.scheduleThreadPoolExecutor.shutdownNow();
        } 
      } catch (Exception ex) {
        LogManager.ignoreTrace(ex);
        try {
          this.scheduleThreadPoolExecutor.shutdownNow();
        } catch (Exception e) {
          LogManager.ignoreTrace(e);
        } 
      }  
    return true;
  }
  
  public boolean isSameConnection(NetworkConnectionHandler connectionHandler) {
    return (connectionHandler != null && connectionHandler.equals(this.connectionHandler));
  }
  
  public void sendDiameterAnswer(DiameterAnswer diameterAnswer) throws UnhandledTransitionException {
    if (!isAlive() || !isPeerConnected())
      throw new UnhandledTransitionException("Diameter Peer: " + getHostIdentity() + " is Closed."); 
    IDiameterAVP originHost = diameterAnswer.getAVP("0:264");
    IDiameterAVP originRealm = diameterAnswer.getAVP("0:296");
    if (originHost != null && "*".equals(originHost.getStringValue()))
      originHost.setStringValue(Parameter.getInstance().getOwnDiameterIdentity()); 
    if (originRealm != null && "*".equals(originRealm.getStringValue()))
      originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm()); 
    convertToRedirectHostFormat((IStackContext)this.stackContext, (DiameterPacket)diameterAnswer, this.peerData.getRedirectHostAVPFormat());
    IStateTransitionData stateTransitionData = createStateTransitionData();
    stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.DIAMETER_PACKET_TO_SEND, diameterAnswer);
    stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, DiameterPeerEvent.SendMessage);
    handleStateTransition(stateTransitionData);
  }
  
  public void sendDiameterRequest(DiameterRequest diameterRequest, @Nonnull ResponseListener listener) throws UnhandledTransitionException {
    if (!isAlive() || !isPeerConnected())
      throw new UnhandledTransitionException("Diameter Peer: " + getHostIdentity() + " is Closed."); 
    IDiameterAVP originHost = diameterRequest.getAVP("0:264");
    IDiameterAVP originRealm = diameterRequest.getAVP("0:296");
    if (originHost != null && "*".equals(originHost.getStringValue()))
      originHost.setStringValue(Parameter.getInstance().getOwnDiameterIdentity()); 
    if (originRealm != null && "*".equals(originRealm.getStringValue()))
      originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm()); 
    convertToRedirectHostFormat((IStackContext)this.stackContext, (DiameterPacket)diameterRequest, this.peerData.getRedirectHostAVPFormat());
    IStateTransitionData stateTransitionData = createStateTransitionData();
    stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.DIAMETER_PACKET_TO_SEND, diameterRequest);
    stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, DiameterPeerEvent.SendMessage);
    stateTransitionData.addObject((IStateTransitionDataCode)PeerDataCode.RESPONSE_LISTENER, listener);
    handleStateTransition(stateTransitionData);
  }
  
  private IStateTransitionData createStateTransitionData() {
    return new IStateTransitionData() {
        Map<IStateTransitionDataCode, Object> data = new HashMap<>();
        
        public Object getData(IStateTransitionDataCode code) {
          return this.data.get(code);
        }
        
        public void addObject(IStateTransitionDataCode code, Object value) {
          this.data.put(code, value);
        }
      };
  }
  
  public List<IDiameterAVP> getAdditionalCERAvps() {
    return this.peerData.getAdditionalCERAvps();
  }
  
  public List<IDiameterAVP> getAdditionalDPRAvps() {
    return this.peerData.getAdditionalDPRAvps();
  }
  
  public InetAddress getLocalInetAddress() {
    return this.peerData.getLocalInetAddress();
  }
  
  public InetAddress getRemoteInetAddress() {
    return this.peerData.getRemoteInetAddress();
  }
  
  public DiameterPeerState registerStatusListener(DiameterPeerStatusListener listener) {
    if (listener != null)
      this.peerStatusListeners.add(listener); 
    return DiameterPeerState.fromStateOrdinal(this.peerStateMachine.currentState().stateOrdinal());
  }
  
  private void markOpen() {
    this.isAlive.set(true);
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("PEER", "Marking Peer: " + getPeerName() + " - " + getHostIdentity() + " Open"); 
    for (DiameterPeerStatusListener listener : this.peerStatusListeners)
      listener.markOpen(); 
  }
  
  private void markClosed() {
    this.isAlive.set(false);
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("PEER", "Marking Peer: " + getPeerName() + " - " + getHostIdentity() + " Closed"); 
    for (DiameterPeerStatusListener listener : this.peerStatusListeners)
      listener.markClosed(); 
  }
  
  public int getLocalPort() {
    return this.peerData.getLocalPort();
  }
  
  public TransportProtocols getTransportProtocol() {
    return this.peerData.getTransportProtocol();
  }
  
  public long getNextSequence() {
    return this.tranlationSeq.getAndAdd(1L);
  }
  
  public void setRemoteIpAddress(String remoteIp) {
    this.peerData.setRemoteIPAddress(remoteIp);
  }
  
  public void setRemoteInetAddress(InetAddress remoteInetAddress) {
    this.peerData.setRemoteInetAddress(remoteInetAddress);
  }
  
  public void setRemotePort(int remotePort) {
    this.peerData.setRemotePort(remotePort);
  }
  
  public void setHostIdentity(String hostIdentity) {
    this.peerData.setHostIdentity(hostIdentity);
  }
  
  public String getLocalBoundAddress() {
    return this.connectionHandler.getLocalAddress();
  }
  
  public boolean isSendDPRonCloseEvent() {
    return this.peerData.isSendDPRonCloseEvent();
  }
  
  private void addAdditionalAVPs(List<IDiameterAVP> additionalAVPs, DiameterPacket diameterPacket) {
    if (additionalAVPs != null) {
      int noOfDPRAvps = additionalAVPs.size();
      for (int i = 0; i < noOfDPRAvps; i++) {
        IDiameterAVP additionalAVP = additionalAVPs.get(i);
        if (additionalAVP.getAVPId().equals("0:283") || additionalAVP
          .getAVPId().equals("0:293") || additionalAVP
          .getAVPId().equals("0:296") || additionalAVP
          .getAVPId().equals("0:264")) {
          IDiameterAVP avp = diameterPacket.getAVP(additionalAVP.getAVPId());
          if (avp != null) {
            avp.setStringValue(additionalAVP.getStringValue());
          } else {
            diameterPacket.addAvp(additionalAVP);
          } 
        } else {
          diameterPacket.addAvp(additionalAVP);
        } 
      } 
    } 
  }
  
  public List<IDiameterAVP> getAdditionalDWRAvps() {
    return this.peerData.getAdditionalDWRAvps();
  }
  
  public PeerData getPeerData() {
    return this.peerData;
  }
  
  public void addRemoteApplication(DiameterPacket diameterPacket) {
    this.peerApplicationProvider.addRemoteApplication(diameterPacket);
  }
  
  public boolean addSecurityAVP(DiameterPacket diameterPacket) {
    if (this.peerData.getSecurityStandard() == SecurityStandard.RFC_6733)
      return true; 
    for (InbandSecurityId securityId : this.localInbandSecurityIds) {
      IDiameterAVP inbandSecurityAVP = DiameterDictionary.getInstance().getKnownAttribute("0:299");
      if (inbandSecurityAVP == null)
        return false; 
      inbandSecurityAVP.setInteger(securityId.getCode());
      diameterPacket.addAvp(inbandSecurityAVP);
    } 
    return true;
  }
  
  private void convertFromRedirectHostFormat(IStackContext stackContext, DiameterPacket packet, RedirectHostAVPFormat fromRedirectHostAVPFormat) {
    if (packet.isRequest())
      return; 
    if (fromRedirectHostAVPFormat == RedirectHostAVPFormat.DIAMETERURI)
      return; 
    List<IDiameterAVP> redirectHostAVPs = packet.getAVPList("0:292");
    if (redirectHostAVPs == null)
      return; 
    for (IDiameterAVP redirectHostAVP : redirectHostAVPs) {
      String redirectHostAVPVal = redirectHostAVP.getStringValue();
      PeerData redirectHostData = null;
      redirectHostData = stackContext.getPeerData(redirectHostAVPVal);
      if (redirectHostData != null) {
        redirectHostAVPVal = redirectHostData.getURI();
        redirectHostAVP.setStringValue(redirectHostAVPVal);
        continue;
      } 
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("PEER", "Peer Data could not be fetched for Redirect Host: " + redirectHostAVPVal + ". Sending Redirect-Host AVP value : " + redirectHostAVPVal); 
    } 
  }
  
  private void convertToRedirectHostFormat(IStackContext stackContext, DiameterPacket packet, RedirectHostAVPFormat toRedirectHostAVPFormat) {
    if (packet.isRequest())
      return; 
    if (RedirectHostAVPFormat.DIAMETERURI == toRedirectHostAVPFormat)
      return; 
    List<IDiameterAVP> redirectHostAVPs = packet.getAVPList("0:292");
    if (redirectHostAVPs == null)
      return; 
    for (IDiameterAVP redirectHostAVP : redirectHostAVPs) {
      String redirectHostAVPVal = redirectHostAVP.getStringValue();
      PeerData redirectHostData = null;
      URIData uriData = DiameterURIParser.parse(redirectHostAVPVal);
      if (uriData == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("PEER", "Unable to parse DiameterURI . Sending Redirect-Host AVP value : " + redirectHostAVPVal); 
        continue;
      } 
      switch (toRedirectHostAVPFormat) {
        case HOSTIDENTITY:
          redirectHostAVPVal = uriData.getHost();
          break;
        case IP:
          redirectHostData = stackContext.getPeerData(uriData.getHost());
          if (redirectHostData != null)
            redirectHostAVPVal = redirectHostData.getRemoteIPAddress(); 
          break;
        default:
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("PEER", "Unable to form " + toRedirectHostAVPFormat + " for Redirect-Host value: " + redirectHostAVPVal); 
          break;
      } 
      redirectHostAVP.setStringValue(redirectHostAVPVal);
    } 
  }
  
  private DiameterPeerConfig createPeerConfig() {
    return new DiameterPeerConfig() {
        public String getPeerId() {
          return DiameterPeer.this.getHostIdentity();
        }
        
        public int getPeerFirmwareRevison() {
          return DiameterPeer.this.getFirmwareRevision();
        }
        
        public StorageTypes getPeerStorageType() {
          return StorageTypes.NON_VOLATILE;
        }
        
        public RowStatus getPeerRowStatus() {
          RowStatus status = RowStatus.NOT_READY;
          if (DiameterPeer.this.getPeerStateMachine().currentState() == DiameterPeerState.I_Open || DiameterPeer.this
            .getPeerStateMachine().currentState().stateOrdinal() == DiameterPeerState.R_Open.stateOrdinal())
            status = RowStatus.ACTIVE; 
          return status;
        }
        
        public int getDbpPeerPortListen() {
          return DiameterPeer.this.getCommunicationPort();
        }
        
        public TransportProtocols getDbpPeerTransportProtocol() {
          TransportProtocols transportProtocol = TransportProtocols.TCP;
          TransportProtocols protocol = DiameterPeer.this.getTransportProtocol();
          if (protocol != null)
            transportProtocol = protocol; 
          return transportProtocol;
        }
        
        public SecurityProtocol getDbpPeerSecurity() {
          SecurityProtocol security = SecurityProtocol.NONE;
          NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
          if (connectionHandler != null)
            security = connectionHandler.getSecurityProtocol(); 
          return security;
        }
        
        public int getDbpPeerPortConnect() {
          int connectPort = 0;
          NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
          if (connectionHandler != null)
            connectPort = connectionHandler.getLocalPort(); 
          return connectPort;
        }
        
        public Set<ApplicationEnum> getDbpAppAdvFromPeer() {
          return DiameterPeer.this.getRemoteApplications();
        }
        
        public DiameterBasePeerVendorTable[] getDbpPeerVendorTable() {
          List<DiameterBasePeerVendorTable> vendorTables = new ArrayList<>();
          List<String> supportedStrVendorList = DiameterPeer.this.getSupportedVendors();
          int vendorListIndex = 0;
          if (supportedStrVendorList != null)
            for (int j = 0; j < supportedStrVendorList.size(); j++) {
              if (supportedStrVendorList.get(j) != null) {
                vendorListIndex++;
                vendorTables.add(new DiameterBasePeerVendorTable(vendorListIndex, supportedStrVendorList
                      .get(j), StorageTypes.NON_VOLATILE, RowStatus.ACTIVE));
              } 
            }  
          return vendorTables.<DiameterBasePeerVendorTable>toArray(new DiameterBasePeerVendorTable[vendorTables.size()]);
        }
        
        public DiameterBasePeerIpAddressTable[] getPeerIpAddressIndex() {
          List<DiameterBasePeerIpAddressTable> dbPeerIpList = new ArrayList<>();
          NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
          if (connectionHandler != null) {
            String remoteIP = connectionHandler.getSourceIpAddress();
            if (remoteIP.length() > 0)
              dbPeerIpList.add(new DiameterBasePeerIpAddressTable(0, IpAddressTypes.IPV4, remoteIP)); 
          } 
          return dbPeerIpList.<DiameterBasePeerIpAddressTable>toArray(new DiameterBasePeerIpAddressTable[dbPeerIpList.size()]);
        }
        
        public String getPeerIpAddresses() {
          NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
          int port = DiameterPeer.this.getCommunicationPort();
          String ip = "";
          if (connectionHandler != null) {
            ip = connectionHandler.getSourceIpAddress();
            if (ip.trim().length() == 0)
              ip = DiameterPeer.this.getPeerData().getRemoteIPAddress(); 
          } else {
            ip = DiameterPeer.this.getPeerData().getRemoteIPAddress();
            if (Strings.isNullOrBlank(ip))
              ip = DiameterPeer.this.getHostIdentity(); 
          } 
          return ip + "-" + port;
        }
        
        public long getDbpPerPeerStatsTimeoutConnAtmpts() {
          return DiameterPeer.this.getTimeoutConnectionAttempts();
        }
        
        public int getPCBState() {
          return DiameterPeer.this.getPCBState();
        }
        
        public long getDbpPerPeerInfoStateDuration() {
          return DiameterPeer.this.getPeerStateChangedDuration();
        }
        
        public int getPeerState() {
          return DiameterPeer.this.getPeerState();
        }
        
        public long getPeerWatchDogInterval() {
          return DiameterPeer.this.getWatchdogInterval();
        }
        
        public long getDbpPeerIndex() {
          return DiameterPeer.this.peerData.getPeerIndex();
        }
        
        public Set<ApplicationEnum> getDbpAppAdvToPeer() {
          return DiameterPeer.this.getApplications();
        }
        
        public boolean isConnectionInitiationEnabled() {
          return DiameterPeer.this.isInitiateConnection();
        }
        
        public String getPeerLocalIpAddresses() {
          return DiameterPeer.this.getPeerData().getLocalIPAddress() + "-" + DiameterPeer.this.getLocalPort();
        }
      };
  }
  
  public class OverloadHandler {
    public void handle(DiameterRequest diameterRequest) {
      if (DiameterPeer.this.stackContext.getActionOnOverload() == OverloadAction.DROP) {
        LogManager.getLogger().warn("PEER", "Dropping request, Package-Type:" + diameterRequest
            .getCommandCode() + ", HbH-ID: " + diameterRequest
            .getHop_by_hopIdentifier() + ", Session-ID: " + diameterRequest
            .getAVPValue("0:263") + " from peer " + DiameterPeer.this
            .getHostIdentity() + ". Reason: Overload Action is DROP");
        DiameterPeer.this.stackContext.updateDiameterStatsPacketDroppedStatistics((DiameterPacket)diameterRequest, DiameterPeer.this.getHostIdentity());
        return;
      } 
      DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
      IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
      int resultCodeOnOverload = DiameterPeer.this.stackContext.getOverloadResultCode();
      resultCode.setInteger(resultCodeOnOverload);
      diameterAnswer.addAvp(resultCode);
      if (resultCode.getInteger() != ResultCode.DIAMETER_SUCCESS.code)
        DiameterUtility.addOrReplaceAvp("0:281", (DiameterPacket)diameterAnswer, "TPS Exceeded"); 
      try {
        LogManager.getLogger().warn("PEER", "Sending " + resultCodeOnOverload + " response for HbH: " + diameterRequest
            .getHop_by_hopIdentifier() + ", Session-ID: " + diameterRequest
            .getAVPValue("0:263") + " from peer " + DiameterPeer.this
            .getHostIdentity() + ". Reason: Overload Action is REJECT");
        DiameterPeer.this.writeToStream((DiameterPacket)diameterAnswer);
      } catch (Exception e) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("PEER", e.getMessage()); 
        LogManager.getLogger().trace("PEER", e);
      } 
    }
  }
  
  public DiameterPeerConfig getPeerConfig() {
    return this.peerConfig;
  }
  
  public void reloadDiameterPeer() {
    this.pcbStateMachine.createDWR();
    this.peerStateMachine.reload();
    readMaxResponseTimeProperties();
  }
  
  public long getPeerLastConnectionRequestTime() {
    return this.lastConnectionAttemptedTimestamp;
  }
  
  public String getLocalIpAddress() {
    return this.peerData.getLocalIPAddress();
  }
  
  public void recordMessageTxCountAndConnectionTime() {
    this.lastConnectionAttemptedTimestamp = this.timesource.currentTimeInMillis();
    this.messageTxCount.set(getCurrentMessageTxCount());
  }
  
  private long getCurrentMessageTxCount() {
    GroupedStatistics peerStatistics = (this.stackContext.getDiameterStatisticsProvider() == null) ? null : (GroupedStatistics)this.stackContext.getDiameterStatisticsProvider().getPeerStatsMap().get(getHostIdentity());
    if (peerStatistics == null)
      return 0L; 
    return peerStatistics.getTotalRequestOutCount() + peerStatistics.getTotalAnswerOutCount();
  }
  
  public boolean isTrafficObservedOnCurrentConnection() {
    return (getCurrentMessageTxCount() > this.messageTxCount.get());
  }
  
  public void start() {
    this.pcbStateMachine.start();
  }
}
