package com.diameter.stack;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.diameter.commons.AppDefaultSessionReleaseIndicator;
import com.diameter.commons.AppListenerInitializationFaildException;
import com.diameter.commons.ApplicationEnum;
import com.diameter.commons.ApplicationIdentifier;
import com.diameter.commons.ApplicationListener;
import com.diameter.commons.AsyncTaskContext;
import com.diameter.commons.AvpGrouped;
import com.diameter.commons.AvpUserEquipmentInfoValue;
import com.diameter.commons.CDRDriver;
import com.diameter.commons.CallableSingleExecutionAsyncTask;
import com.diameter.commons.CommandCode;
import com.diameter.commons.ConnectionEvents;
import com.diameter.commons.ConnectionFactory;
import com.diameter.commons.ConnectionFactoryImpl;
import com.diameter.commons.DiameterAnswer;
import com.diameter.commons.DiameterAppMessageHandler;
import com.diameter.commons.DiameterCDRDriverFactory;
import com.diameter.commons.DiameterDictionary;
import com.diameter.commons.DiameterNetworkStatisticsProvider;
import com.diameter.commons.DiameterPacket;
import com.diameter.commons.DiameterPeer;
import com.diameter.commons.DiameterPeerCommunicator;
import com.diameter.commons.DiameterPeerCommunicatorFactory;
import com.diameter.commons.DiameterPeerEvent;
import com.diameter.commons.DiameterPeerState;
import com.diameter.commons.DiameterPeerStatusListener;
import com.diameter.commons.DiameterPeersTable;
import com.diameter.commons.DiameterRequest;
import com.diameter.commons.DiameterRouter;
import com.diameter.commons.DiameterStackAlerts;
import com.diameter.commons.DiameterStatisticListener;
import com.diameter.commons.DiameterStatisticsProvider;
import com.diameter.commons.DiameterUtility;
import com.diameter.commons.DiameterVirtualPeer;
import com.diameter.commons.DisconnectionCause;
import com.diameter.commons.DriverInitializationFailedException;
import com.diameter.commons.DriverNotFoundException;
import com.diameter.commons.DuplicateDetectionHandler;
import com.diameter.commons.ElementRegistrationFailedException;
import com.diameter.commons.EliteSSLContextFactory;
import com.diameter.commons.EliteThreadFactory;
import com.diameter.commons.EndToEndPool;
import com.diameter.commons.ExplicitRoutingHandler;
import com.diameter.commons.FairnessPolicy;
import com.diameter.commons.FileAllocatorException;
import com.diameter.commons.HopByHopPool;
import com.diameter.commons.IDiameterAVP;
import com.diameter.commons.IDiameterSessionManager;
import com.diameter.commons.IDiameterStackContext;
import com.diameter.commons.IEventEnum;
import com.diameter.commons.INetworkConnector;
import com.diameter.commons.IPeerListener;
import com.diameter.commons.ISession;
import com.diameter.commons.IStateEnum;
import com.diameter.commons.InitializationFailedException;
import com.diameter.commons.IntervalBasedTask;
import com.diameter.commons.LogLevel;
import com.diameter.commons.LogManager;
import com.diameter.commons.MIBIndexRecorder;
import com.diameter.commons.NetworkConnectionHandler;
import com.diameter.commons.OverloadAction;
import com.diameter.commons.Packet;
import com.diameter.commons.PacketValidationCode;
import com.diameter.commons.Parameter;
import com.diameter.commons.PeerData;
import com.diameter.commons.PeerProvider;
import com.diameter.commons.PeerProviderImpl;
import com.diameter.commons.ResultCode;
import com.diameter.commons.ResultCodeCategory;
import com.diameter.commons.RoutingActions;
import com.diameter.commons.RoutingEntryData;
import com.diameter.commons.Session;
import com.diameter.commons.SessionFactoryManager;
import com.diameter.commons.SessionFactoryManagerImpl;
import com.diameter.commons.SessionsFactory;
import com.diameter.commons.SingleExecutionAsyncTask;
import com.diameter.commons.StackAlertSeverity;
import com.diameter.commons.Status;
import com.diameter.commons.StatusListenerRegistrationFailException;
import com.diameter.commons.Strings;
import com.diameter.commons.TaskScheduler;
import com.diameter.commons.ThreadPoolDetails;
import com.diameter.commons.TransportProtocols;
import com.diameter.commons.TypeNotSupportedException;
import com.diameter.commons.VirtualConnectionHandler;
import com.diameter.commons.VirtualOutputStream;
import com.diameter.commons.WeightedFairBlockingQueue;

public class DiameterStack extends Stack {
  public static final String H2H = "H2H";
  
  public static final String E2E = "E2E";
  
  public static final String CC = "CC";
  
  public static final String SID = "SID";
  
  public static final String APP_ID = "AppId";
  
  private final String MODULE = "DIAMETER-STACK";
  
  private final SessionFactoryManager sessionFactoryManager;
  
  private List<RoutingEntryData> routingEntryDataList;
  
  private List<PeerData> peerDataList;
  
  private List<ApplicationListener> applicationListeners;
  
  private DiameterStackContext stackContext;
  
  private DiameterAppMessageHandler diameterAppMessageHandler;
  
  private String ownDiameterIdentity = "diameter.horizon.com";
  
  private boolean isNAIEnabled;
  
  private boolean isRealmVerificationRequired;
  
  private List<String> naiRealmNames;
  
  private String ownDiameterURI = "aaa://localhost:3868";
  
  private String ownDiameterRealm = "horizon.com";
  
  private String serverInstanceId;
  
  private String routingTableName = "";
  
  private Status currentStackStatus = Status.NOT_INITIALIZE;
  
  private DiameterStatisticListener diameterStatisticListener;
  
  private DiameterPeersTable diameterPeersTable;
  
  private PeerProvider peerProvider;
  
  private DiameterRouter diameterRouter;
  
  private AtomicInteger sessHigherBit;
  
  private AtomicInteger sessLowerBit;
  
  private boolean rfcValidation = true;
  
  private EliteSSLContextFactory eliteSSLContextFactory;
  
  private Set<ApplicationEnum> supportedApplicationIdentifiers = new HashSet<>();
  
  private ExplicitRoutingHandler explicitRoutingHandler;
  
  @Nullable
  private ScheduledExecutorService scheduledExecutorService;
  
  private OverloadAction actionOnOverload;
  
  private int resultCodeOnOverload;
  
  private MIBIndexRecorder mibIndexRecorder;
  
  @Nullable
  private IDiameterSessionManager diameterSessionManager;
  
  private static final int BASE_MESSAGE_QUEUE_SIZE = 100;
  
  private static final int BASE_MESSAGE_THREADS = 2;
  
  public static final long DEFAULT_THREAD_KEEP_ALIVE_TIME = 3600000L;
  
  private ThreadPoolExecutor subProcessThreadExecutor;
  
  private BlockingQueue<Runnable> subProcessThreadTaskQueue;
  
  private ThreadPoolExecutor baseThreadExecutor;
  
  private BlockingQueue<Runnable> baseThreadTaskQueue;
  
  private DiameterNetworkStatisticsProvider statisticsProvider;
  
  private int workerThreadPriority = 6;
  
  private int mainThreadPriority = 8;
  
  private int maxThreadPoolSize = 10;
  
  private int minThreadPoolSize = 5;
  
  private long threadKeepAliveTime = 3600000L;
  
  private int maxRequestQueueSize = 1500;
  
  @Nullable
  private FairnessPolicy<Runnable> fairnessPolicy;
  
  private long sessionCleanupInterval = 0L;
  
  private long sessionTimeOut = 0L;
  
  private DuplicateDetectionHandler duplicateMessageHandler;
  
  private DiameterCDRDriverFactory diameterCDRDriverFactory;
  
  private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
  
  private TaskScheduler taskScheduler;
  
  private DiameterPeerCommunicatorFactory diameterPeerCommunicatorFactory;
  
  public DiameterStack() {
    this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(15, (ThreadFactory)new EliteThreadFactory("DIA-STACK", "DIA-STACK-SCH", 5));
    this.taskScheduler = new TaskSchedulerImpl();
    this.stackContext = new DiameterStackContext();
    this.diameterPeersTable = new DiameterPeersTable();
    this.peerProvider = (PeerProvider)new PeerProviderImpl(this.diameterPeersTable);
    this.diameterAppMessageHandler = new DiameterAppMessageHandler(this.stackContext);
    this.mibIndexRecorder = new MIBIndexRecorder();
    this.diameterStatisticListener = new DiameterStatisticListener(this.mibIndexRecorder, this.diameterPeersTable, this.supportedApplicationIdentifiers, new TaskScheduler() {
          public Future<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
            return DiameterStack.this.scheduleSingleExecutionTask(task);
          }
          
          public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
            return DiameterStack.this.scheduleIntervalBasedTask(task);
          }
          
          public void execute(Runnable command) {
            DiameterStack.this.scheduleSingleExecutionTask(command);
          }
        });
    this.routingEntryDataList = new ArrayList<>();
    this.peerDataList = new ArrayList<>();
    this.applicationListeners = new ArrayList<>();
    this.diameterRouter = new DiameterRouter(this.stackContext, this.routingEntryDataList);
    this.naiRealmNames = new ArrayList<>();
    if (System.getProperty("diameter.rfc.validation") != null)
      this.rfcValidation = Boolean.parseBoolean(System.getProperty("diameter.rfc.validation")); 
    this.sessHigherBit = new AtomicInteger((int)(System.currentTimeMillis() & 0x7FFFFFFFL));
    this.sessLowerBit = new AtomicInteger(0);
    this.explicitRoutingHandler = new ExplicitRoutingHandler();
    this.duplicateMessageHandler = new DuplicateDetectionHandler(this.stackContext);
    this.actionOnOverload = OverloadAction.REJECT;
    this.resultCodeOnOverload = ResultCode.DIAMETER_TOO_BUSY.code;
    this.diameterPeerCommunicatorFactory = new DiameterPeerCommunicatorFactory(this.stackContext, this.peerProvider);
    this.sessionFactoryManager = (SessionFactoryManager)new SessionFactoryManagerImpl(this.stackContext);
  }
  
  public void registerEliteSSLContextFactory(EliteSSLContextFactory eliteSSLContextFactory) {
    this.eliteSSLContextFactory = eliteSSLContextFactory;
  }
  
  public void registerRoutingEntry(RoutingEntryData routingEntryData) throws ElementRegistrationFailedException {
    if (routingEntryData != null) {
      this.routingEntryDataList.add(routingEntryData);
    } else {
      throw new ElementRegistrationFailedException("Empty Routing Entry Data found");
    } 
  }
  
  public void registerPriorityRoutingEntry(RoutingEntryData routingEntryData) throws ElementRegistrationFailedException {
    if (routingEntryData != null) {
      this.diameterRouter.registerPriorityRoutingEntry(routingEntryData);
    } else {
      throw new ElementRegistrationFailedException("Empty Routing Entry Data found");
    } 
  }
  
  public void reloadRoutingEntries(List<RoutingEntryData> routingEntryDataList) throws ElementRegistrationFailedException {
    this.routingEntryDataList = routingEntryDataList;
  }
  
  public void registerRoutingEntries(List<RoutingEntryData> routingEntryDatas) throws ElementRegistrationFailedException {
    if (routingEntryDatas != null)
      for (RoutingEntryData realmData : routingEntryDatas)
        registerRoutingEntry(realmData);  
  }
  
  public VirtualConnectionHandler registerVirtualPeer(PeerData peerData, VirtualOutputStream outpurStream) throws ElementRegistrationFailedException {
    try {
      VirtualConnectionHandler virtualConnectionHandler = new VirtualConnectionHandler(this, peerData, outpurStream);
      DiameterVirtualPeer diameterVirtualPeer = new DiameterVirtualPeer(peerData, (NetworkConnectionHandler)virtualConnectionHandler, this.stackContext, this.diameterRouter, this.sessionFactoryManager, this.diameterAppMessageHandler, this.explicitRoutingHandler, this.duplicateMessageHandler);
      diameterVirtualPeer.init();
      this.diameterPeersTable.addPeer((DiameterPeer)diameterVirtualPeer);
      this.diameterStatisticListener.addDiameterPeer((DiameterPeer)diameterVirtualPeer);
      return virtualConnectionHandler;
    } catch (InitializationFailedException ex) {
      throw new ElementRegistrationFailedException(ex);
    } 
  }
  
  public void registerApplicationListener(Collection<ApplicationListener> applicationListeners) throws ElementRegistrationFailedException {
    for (ApplicationListener applicationListener : applicationListeners)
      registerApplicationListener(applicationListener); 
  }
  
  public void registerApplicationListener(ApplicationListener applicationListener) throws ElementRegistrationFailedException {
    if (applicationListener == null)
      throw new ElementRegistrationFailedException("application listener is null"); 
    LogManager.getLogger().info("DIAMETER-STACK", "Registering Application Listener for Application(s): " + 
        Arrays.<ApplicationEnum>asList(applicationListener.getApplicationEnum()));
    this.supportedApplicationIdentifiers.addAll(Arrays.asList(applicationListener.getApplicationEnum()));
    this.applicationListeners.add(applicationListener);
  }
  
  public void registerPeers(List<PeerData> peersDataList) throws ElementRegistrationFailedException {
    if (peersDataList == null)
      throw new ElementRegistrationFailedException("Provided Peers Data List is null"); 
    List<PeerData> peerList = null;
    synchronized (this.peerDataList) {
      peerList = new ArrayList<>(this.peerDataList);
    } 
    for (PeerData peerData : peersDataList) {
      if (peerData.getPeerName() == null) {
        LogManager.getLogger().error("DIAMETER-STACK", "Error while registrating peer " + peerData.getHostIdentity() + "(" + peerData
            .getRemoteIPAddress() + "). Reason: PeerName is null");
        continue;
      } 
      boolean alreadyRegistered = false;
      for (PeerData oldPeerData : peerList) {
        if (oldPeerData.getPeerName().equals(peerData.getPeerName())) {
          oldPeerData.reload(peerData);
          alreadyRegistered = true;
        } 
      } 
      if (alreadyRegistered)
        continue; 
      String remoteIPAddress = peerData.getRemoteIPAddress();
      if (DiameterUtility.isIPRange(remoteIPAddress)) {
        if (peerData.getHostIdentity() != null && !peerData.getHostIdentity().trim().isEmpty()) {
          LogManager.getLogger().error("DIAMETER-STACK", "Error while adding available addresses from range " + remoteIPAddress + ". Reason: HostIdentity not allowed for IPRange/Netmask");
          continue;
        } 
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIAMETER-STACK", "Producing available addresses from range " + remoteIPAddress + " for Peer " + peerData.getPeerName()); 
        List<InetAddress> inetAddresses = getAvailableInetAddresses(remoteIPAddress);
        if (inetAddresses == null || inetAddresses.isEmpty()) {
          LogManager.getLogger().warn("DIAMETER-STACK", "No available address found from range " + remoteIPAddress + " for Peer " + peerData.getPeerName());
          continue;
        } 
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIAMETER-STACK", "Total available addresses found = " + inetAddresses.size() + " for Peer " + peerData.getPeerName()); 
        for (InetAddress inetAddress : inetAddresses) {
          try {
            PeerData data = (PeerData)peerData.clone();
            data.setPeerIndex(-1L);
            data.setPeerName(peerData.getPeerName());
            data.setRemoteInetAddress(inetAddress);
            data.setRemoteIPAddress(inetAddress.getHostAddress());
            peerList.add(data);
          } catch (CloneNotSupportedException e) {
            LogManager.getLogger().trace("DIAMETER-STACK", e);
          } 
        } 
        continue;
      } 
      peerList.add(peerData);
    } 
    this.peerDataList = peerList;
  }
  
  public synchronized void init() {
    initDiameterPeers();
  }
  
  public synchronized boolean start() {
    if (this.currentStackStatus == Status.NOT_INITIALIZE) {
      this.currentStackStatus = Status.INITIALIZING;
      Parameter.getInstance().setHostIPAddress(getNetworkAddress());
      Parameter.getInstance().setHostListeningPort(getNetworkPort());
      Parameter.getInstance().setOwnDiameterIdentity(getDiameterStackIdentity());
      Parameter.getInstance().setOwnDiameterRealm(getDiameterStackRealm());
      Parameter.getInstance().setOwnDiameterURI(getDiameterStackURI());
      Parameter.getInstance().setRoutingTableName(getRoutingTable());
      Parameter.getInstance().setStackUpTimeStamp();
      if (!start((ConnectionFactory)new ConnectionFactoryImpl(this.peerProvider))) {
        this.currentStackStatus = Status.STOPPED;
        return false;
      } 
      startWorkerThreads();
      this.duplicateMessageHandler.init();
      initApplicationListener();
      this.diameterRouter.init();
      startDiameterPeers();
      this.supportedApplicationIdentifiers.addAll(this.diameterRouter.getSupportedRemoteApplications());
      this.diameterStatisticListener.init();
      this.currentStackStatus = Status.INITIALIZED;
      if (getSessionInterval() > 0L) {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor((ThreadFactory)new EliteThreadFactory("DIA-STACK", "DIA-STACK-SESS-CLEANUP-TH", 10));
        DiameterSessionCleanupTask diameterSessionCleanupTask = new DiameterSessionCleanupTask(getSessionTimeOut() * 1000L);
        this.scheduledExecutorService.scheduleWithFixedDelay(diameterSessionCleanupTask, getSessionInterval(), getSessionInterval(), TimeUnit.SECONDS);
      } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
        LogManager.getLogger().info("DIAMETER-STACK", "Diameter Session Cleanup is disabled. Configured value: " + getSessionInterval());
      } 
      this.currentStackStatus = Status.RUNNING;
      LogManager.getLogger().info("DIAMETER-STACK", "Diameter Stack started successfully.");
      Stack.generateAlert(StackAlertSeverity.INFO, DiameterStackAlerts.DIAMETER_STACK_UP, "DIAMETER-STACK", "Diameter Stack UP");
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("DIAMETER-STACK", "Stack already Started");
    } 
    return true;
  }
  
  private void startWorkerThreads() {
    if (getFairnessPolicy() == null) {
      this.subProcessThreadTaskQueue = new LinkedBlockingQueue<>(getMaxRequestQueueSize());
    } else {
      this.subProcessThreadTaskQueue = (BlockingQueue<Runnable>)new WeightedFairBlockingQueue(getMaxRequestQueueSize(), getFairnessPolicy());
    } 
    this.baseThreadTaskQueue = new LinkedBlockingQueue<>(100);
    RejectedExecutionHandler rejectedExecutionHandler = getRejectedExecutionHandler();
    if (rejectedExecutionHandler == null) {
      this
        .baseThreadExecutor = new ThreadPoolExecutor(2, 2, getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, this.baseThreadTaskQueue);
      this.subProcessThreadExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, this.subProcessThreadTaskQueue);
    } else {
      this
        .baseThreadExecutor = new ThreadPoolExecutor(2, 2, getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, this.baseThreadTaskQueue, rejectedExecutionHandler);
      this.subProcessThreadExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, this.subProcessThreadTaskQueue, rejectedExecutionHandler);
    } 
    this.baseThreadExecutor.setThreadFactory((ThreadFactory)new EliteThreadFactory("DIA-STACK", getThreadIdentifier() + "-BASE-POOL-THR", getMainThreadPriority()));
    this.baseThreadExecutor.prestartAllCoreThreads();
    this.subProcessThreadExecutor.setThreadFactory((ThreadFactory)new EliteThreadFactory("DIA-STACK", getThreadIdentifier() + "-POOL-THR", getWorkerThreadPriority()));
    this.subProcessThreadExecutor.prestartAllCoreThreads();
    this.statisticsProvider = new DiameterNetworkStatisticsProvider() {
        public ThreadPoolDetails getMessageThreadPoolDetails() {
          return new ThreadPoolDetails() {
              public int getQueueSize() {
                return DiameterStack.this.subProcessThreadTaskQueue.size();
              }
              
              public int getPoolSize() {
                return DiameterStack.this.subProcessThreadExecutor.getPoolSize();
              }
              
              public int getMinSize() {
                return DiameterStack.this.subProcessThreadExecutor.getCorePoolSize();
              }
              
              public int getMaxSize() {
                return DiameterStack.this.subProcessThreadExecutor.getMaximumPoolSize();
              }
              
              public int getPeakPoolSize() {
                return DiameterStack.this.subProcessThreadExecutor.getLargestPoolSize();
              }
              
              public int getActiveCount() {
                return DiameterStack.this.subProcessThreadExecutor.getActiveCount();
              }
            };
        }
        
        public ThreadPoolDetails getBaseThreadPoolDetails() {
          return new ThreadPoolDetails() {
              public int getQueueSize() {
                return DiameterStack.this.baseThreadTaskQueue.size();
              }
              
              public int getPoolSize() {
                return DiameterStack.this.baseThreadExecutor.getPoolSize();
              }
              
              public int getMinSize() {
                return DiameterStack.this.baseThreadExecutor.getCorePoolSize();
              }
              
              public int getMaxSize() {
                return DiameterStack.this.baseThreadExecutor.getMaximumPoolSize();
              }
              
              public int getPeakPoolSize() {
                return DiameterStack.this.baseThreadExecutor.getLargestPoolSize();
              }
              
              public int getActiveCount() {
                return DiameterStack.this.baseThreadExecutor.getActiveCount();
              }
            };
        }
      };
  }
  
  public int getWorkerThreadPriority() {
    return this.workerThreadPriority;
  }
  
  public void setWorkerThreadPriority(int workerThreadPriority) {
    this.workerThreadPriority = workerThreadPriority;
  }
  
  public int getMainThreadPriority() {
    return this.mainThreadPriority;
  }
  
  public void setMainThreadPriority(int mainThreadPriority) {
    this.mainThreadPriority = mainThreadPriority;
  }
  
  private String getThreadIdentifier() {
    return "DIA-STACK";
  }
  
  public int getMaxThreadPoolSize() {
    return this.maxThreadPoolSize;
  }
  
  public void setMaxThreadPoolSize(int maxThreadPoolSize) {
    this.maxThreadPoolSize = maxThreadPoolSize;
  }
  
  public int getMinThreadPoolSize() {
    return this.minThreadPoolSize;
  }
  
  public void setMinThreadPoolSize(int minThreadPoolSize) {
    this.minThreadPoolSize = minThreadPoolSize;
  }
  
  public long getThreadKeepAliveTime() {
    return this.threadKeepAliveTime;
  }
  
  public void setThreadKeepAliveTime(long threadKeepAliveTime) {
    this.threadKeepAliveTime = threadKeepAliveTime;
  }
  
  @Nullable
  protected RejectedExecutionHandler getRejectedExecutionHandler() {
    return new RejectionHandler();
  }
  
  public void addMDC(DiameterPacket packet) {
    //
  }
  
  public void clearMDC() {
    //
  }
  
  private class RejectionHandler implements RejectedExecutionHandler {
    private AppDefaultSessionReleaseIndicator appDefaultSessionReleaseIndicator = new AppDefaultSessionReleaseIndicator();
    
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      DiameterStack.PacketProcess packetProcess = null;
      try {
        packetProcess = (DiameterStack.PacketProcess)r;
      } catch (Exception ex) {
        LogManager.getLogger().trace("DIAMETER-STACK", ex);
        return;
      } 
      DiameterPacket diameterPacket = (DiameterPacket)packetProcess.getPacket();
      NetworkConnectionHandler connectionHandler = packetProcess.getConnectionHandler();
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STACK", "Fetching Peer from Source-IP-Address: " + connectionHandler.getSourceIpAddress()); 
      PeerData peerData = DiameterStack.this.getStackContext().getPeerData(connectionHandler.getSourceIpAddress());
      String hostIdentity = null;
      if (peerData != null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIAMETER-STACK", "Peer:" + peerData.getPeerName() + " found from Source-IP-Address: " + connectionHandler.getSourceIpAddress()); 
        hostIdentity = peerData.getHostIdentity();
      } else {
        LogManager.getLogger().warn("DIAMETER-STACK", "Use Origin-Host as host-Identity for updating statistics. Reason:Peer not found from Source-IP-Address:" + connectionHandler
            .getSourceIpAddress() + ", Package-Type:" + diameterPacket.getCommandCode());
        hostIdentity = connectionHandler.getHostName();
      } 
      DiameterStack.this.getStackContext().updateInputStatistics(diameterPacket, hostIdentity);
      if (diameterPacket.isResponse()) {
        if (diameterPacket.getApplicationID() == ApplicationIdentifier.BASE.getApplicationId()) {
          LogManager.getLogger().warn("DIAMETER-STACK", "Dropping rejected reponse, Packet-Type:" + diameterPacket
              .getCommandCode());
        } else {
          LogManager.getLogger().warn("DIAMETER-STACK", "Dropping rejected reponse, Session-ID = " + diameterPacket
              .getAVPValue("0:263") + " Packet-Type:" + diameterPacket
              .getCommandCode());
        } 
        DiameterStack.this.getStackContext().updateDiameterStatsPacketDroppedStatistics(diameterPacket, hostIdentity);
        return;
      } 
      int resultCodeOnOverload = DiameterStack.this.getStackContext().getOverloadResultCode();
      if (DiameterStack.this.getStackContext().getActionOnOverload() == OverloadAction.DROP) {
        LogManager.getLogger().warn("DIAMETER-STACK", "Dropping request, Package-Type:" + diameterPacket
            .getCommandCode() + ". Reason: Overload action is Drop");
        DiameterStack.this.getStackContext().updateDiameterStatsPacketDroppedStatistics(diameterPacket, hostIdentity);
        return;
      } 
      try {
        IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
        if (diameterPacket.getApplicationID() == ApplicationIdentifier.BASE.getApplicationId()) {
          if (diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.code) {
            LogManager.getLogger().info("DIAMETER-STACK", "Sending Diameter-Success for received DWR request");
            resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
          } else {
            LogManager.getLogger().warn("DIAMETER-STACK", "Sending " + resultCodeOnOverload + " for rejected request, Package-Type:" + diameterPacket
                .getCommandCode());
          } 
        } else {
          LogManager.getLogger().warn("DIAMETER-STACK", "Sending " + resultCodeOnOverload + " for rejected request, Session-ID = " + diameterPacket
              .getAVPValue("0:263") + " Package-Type:" + diameterPacket
              .getCommandCode());
          resultCode.setInteger(resultCodeOnOverload);
        } 
        DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket);
        diameterAnswer.addAvp(resultCode);
        if (resultCode.getInteger() != ResultCode.DIAMETER_SUCCESS.code)
          DiameterUtility.addOrReplaceAvp("0:281", (DiameterPacket)diameterAnswer, "System Overload"); 
        if (this.appDefaultSessionReleaseIndicator.isEligible((DiameterPacket)diameterAnswer) && 
          DiameterStack.this.getStackContext().hasSession(diameterAnswer.getSessionID(), diameterAnswer.getApplicationID()))
          DiameterStack.this.getStackContext()
            .getOrCreateSession(diameterPacket.getSessionID(), diameterPacket.getApplicationID())
            .release(); 
        connectionHandler.send((Packet)diameterAnswer);
        DiameterStack.this.getStackContext().updateOutputStatistics((DiameterPacket)diameterAnswer, hostIdentity);
      } catch (Exception exp) {
        LogManager.getLogger().error("DIAMETER-STACK", exp.getMessage());
        LogManager.getLogger().trace("DIAMETER-STACK", exp);
      } 
    }
    
    private RejectionHandler() {}
  }
  
  public int getMaxRequestQueueSize() {
    return this.maxRequestQueueSize;
  }
  
  public void setMaxRequestQueueSize(int maxRequestQueueSize) {
    this.maxRequestQueueSize = maxRequestQueueSize;
  }
  
  public FairnessPolicy<Runnable> getFairnessPolicy() {
    return this.fairnessPolicy;
  }
  
  public void setFairnessPolicy(FairnessPolicy<Runnable> fairnessPolicy) {
    this.fairnessPolicy = fairnessPolicy;
  }
  
  public void reload() {
    if (this.currentStackStatus != Status.STOPPED && this.currentStackStatus != Status.STOPPING) {
      Parameter.getInstance().setRoutingTableName(getRoutingTable());
      reloadDiameterPeers();
      this.diameterStatisticListener.reload(this.diameterPeersTable.getPeerList());
      this.diameterRouter.reInit(this.routingEntryDataList);
    } 
  }
  
  private void reloadDiameterPeers() {
    for (PeerData peerData : this.peerDataList) {
      DiameterPeer peer = null;
      if (!Strings.isNullOrBlank(peerData.getHostIdentity()))
        peer = this.diameterPeersTable.getPeer(peerData.getHostIdentity()); 
      if (peer == null && peerData.getRemoteInetAddress() != null)
        peer = this.diameterPeersTable.getPeer(peerData.getRemoteInetAddress().getHostAddress()); 
      if (peer == null) {
        try {
          DiameterPeer diameterPeer = new DiameterPeer(peerData, this.stackContext, this.diameterRouter, this.sessionFactoryManager, this.diameterAppMessageHandler, this.explicitRoutingHandler, this.duplicateMessageHandler);
          diameterPeer.init();
          this.diameterPeersTable.addPeer(diameterPeer);
        } catch (Exception e) {
          LogManager.getLogger().error("DIAMETER-STACK", "Error while creating DiameterPeer from PeerData: " + peerData.getPeerName() + ". Reason:" + e.getMessage());
          LogManager.getLogger().trace("DIAMETER-STACK", e);
        } 
        continue;
      } 
      peer.reloadDiameterPeer();
    } 
  }
  
  private void initApplicationListener() {
    if (this.applicationListeners == null || this.applicationListeners.isEmpty()) {
      LogManager.getLogger().error("DIAMETER-STACK", "Fail to initilized application listers. reason: applicationListerner list is empty");
      return;
    } 
    for (int index = 0; index < this.applicationListeners.size(); index++) {
      try {
        ((ApplicationListener)this.applicationListeners.get(index)).init();
        this.diameterAppMessageHandler.addApplicationListener(this.applicationListeners.get(index));
      } catch (AppListenerInitializationFaildException e) {
        LogManager.getLogger().error("DIAMETER-STACK", "Application(" + Arrays.toString((Object[])((ApplicationListener)this.applicationListeners.get(index)).getApplicationEnum()) + ") initialization failed, reason: " + e.getMessage());
        LogManager.getLogger().trace("DIAMETER-STACK", e);
      } 
    } 
  }
  
  private PacketValidationCode validatePacket(DiameterPacket packet) {
    if (!DiameterUtility.isBaseProtocolPacket(packet.getCommandCode())) {
      IDiameterAVP sessionIDAvp = packet.getAVP("0:263");
      if (sessionIDAvp == null || Strings.isNullOrBlank(sessionIDAvp.getStringValue())) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().error("DIAMETER-STACK", "Invalid Diameter Packet Received with E2E: " + packet.getEnd_to_endIdentifier() + ", Reason: Session-Id Avp not found or Contain blank value"); 
        return PacketValidationCode.SESSION_ID_MISSING;
      } 
    } 
    if (!this.rfcValidation) {
      if (LogManager.getLogger().isDebugLogLevel())
        LogManager.getLogger().debug("DIAMETER-STACK", "Diameter Rfc Validation is disabled"); 
      return PacketValidationCode.VALID_PACKET;
    } 
    if (packet.getAVP("0:264") == null)
      return PacketValidationCode.ORIGIN_HOST_MISSING; 
    if (packet.getAVP("0:296") == null)
      return PacketValidationCode.ORIGIN_REALM_MISSING; 
    if (DiameterUtility.isBaseProtocolPacket(packet.getCommandCode()) != (
      (packet.getApplicationID() == ApplicationIdentifier.BASE.applicationId)))
      return PacketValidationCode.INVALID_BASE_COMMAND_CODE; 
    return PacketValidationCode.VALID_PACKET;
  }
  
  private void handleReceivedInvalidMessage(DiameterPacket packet, NetworkConnectionHandler connectionHandler, PacketValidationCode invalidityReason) throws IOException {
    this.stackContext.updateDiameterStatsMalformedPacket(packet, connectionHandler.getHostName());
    if (packet.isResponse()) {
      LogManager.getLogger().error("DIAMETER-STACK", "Discarding invalid Diameter Answer. Reason: " + invalidityReason);
      return;
    } 
    DiameterAnswer answerpacket = new DiameterAnswer((DiameterRequest)packet);
    IDiameterAVP resultCode = null;
    LogManager.getLogger().warn("DIAMETER-STACK", "Invalid Diameter request received. Reason: " + invalidityReason);
    resultCode = DiameterDictionary.getInstance().getAttribute("0:268");
    resultCode.setInteger(invalidityReason.resultCode);
    answerpacket.addAvp(resultCode);
    switch (invalidityReason) {
      case ORIGIN_HOST_MISSING:
        DiameterUtility.addFailedAVP(answerpacket, 
            DiameterDictionary.getInstance()
            .getAttribute("0:264"));
        break;
      case ORIGIN_REALM_MISSING:
        DiameterUtility.addFailedAVP(answerpacket, 
            DiameterDictionary.getInstance()
            .getAttribute("0:296"));
        break;
      case SESSION_ID_MISSING:
        DiameterUtility.addFailedAVP(answerpacket, 
            DiameterDictionary.getInstance()
            .getAttribute("0:263"));
        break;
      case INVALID_BASE_COMMAND_CODE:
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("DIAMETER-STACK", "Command-Code: " + packet.getCommandCode() + " is not comaptible with Base Application-ID: " + packet
              .getApplicationID()); 
        break;
      default:
        LogManager.getLogger().error("DIAMETER-STACK", "Failed to interpret Invalidity reason " + invalidityReason + " of Diameter Request. Dropping request.");
        return;
    } 
    if (LogManager.getLogger().isInfoLogLevel())
      LogManager.getLogger().info("DIAMETER-STACK", answerpacket.toString()); 
    connectionHandler.send((Packet)answerpacket);
  }
  
  public void handleReceivedMessage(Packet packet, NetworkConnectionHandler connectionHandler) {
    try {
      DiameterPacket diameterPacket = (DiameterPacket)packet;
      if (diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode()) {
        if (diameterPacket.isRequest()) {
          if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
            LogManager.getLogger().info("DIAMETER-STACK", "Received DWR from host name: " + connectionHandler.getHostName() + " source IP: " + connectionHandler
                .getSourceIpAddress() + " origin host: " + diameterPacket.getAVPValue("0:264")); 
        } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
          LogManager.getLogger().info("DIAMETER-STACK", "Received DWA from host name: " + connectionHandler.getHostName() + " source IP: " + connectionHandler
              .getSourceIpAddress() + " origin host: " + diameterPacket.getAVPValue("0:264"));
        } 
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIAMETER-STACK", "Received Packet detail: " + diameterPacket.toString()); 
      } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
        LogManager.getLogger().info("DIAMETER-STACK", "Received Packet from host name: " + connectionHandler.getHostName() + " source IP: " + connectionHandler
            .getSourceIpAddress() + " session-Id: " + diameterPacket.getAVPValue("0:263"));
        LogManager.getLogger().info("DIAMETER-STACK", "Received Packet detail: " + diameterPacket.toString());
      } 
      
      if ((connectionHandler.getHostName() == null || connectionHandler.getHostName().trim().length() == 0)){
    	  connectionHandler.setHostName(diameterPacket.getAVPValue("0:264")); 
      }
        
      DiameterPeer diameterPeer = this.diameterPeersTable.getPeer(connectionHandler.getHostName());
      if (diameterPeer != null) {
        if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.code)
          if (diameterPeer.getRemoteInetAddress() != null) {
            if (!isLegitPeer(connectionHandler, (IPeerListener)diameterPeer)) {
              if (diameterPacket.isRequest()) {
                LogManager.getLogger().error("DIAMETER-STACK", "Recieved Connection Request from Unknown Peer: " + connectionHandler.getSourceIpAddress() + " for HostIdentity : " + diameterPeer.getHostIdentity() + ". Sending DIAMETER_UNKNOWN_PEER");
                DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNKNOWN_PEER);
                IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute("0:273");
                if (diameterAVP != null) {
                  diameterAVP.setInteger(DisconnectionCause.DO_NOT_WANT_TO_TALK_TO_YOU.code);
                  answerPacket.addAvp(diameterAVP);
                } 
                try {
                  if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info("DIAMETER-STACK", "Sending Packet: " + answerPacket.toString()); 
                  connectionHandler.send((Packet)answerPacket);
                } catch (IOException e) {
                  LogManager.getLogger().error("DIAMETER-STACK", "Error while sending answer, reason: " + e.getMessage());
                  LogManager.ignoreTrace(e);
                } 
              } else {
                LogManager.getLogger().error("DIAMETER-STACK", "Recieved Answer from Unknown Peer: " + connectionHandler.getSourceIpAddress() + " with HbH-ID: " + diameterPacket
                    .getHop_by_hopIdentifier() + ". Dropping Answer");
                sendDPRtoUnknownPeer(connectionHandler, DisconnectionCause.DO_NOT_WANT_TO_TALK_TO_YOU);
              } 
              LogManager.getLogger().warn("DIAMETER-STACK", "Closing Connection for IP Address " + connectionHandler
                  .getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
              connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
              return;
            } 
          } else {
            diameterPeer.setRemoteInetAddress(InetAddress.getByName(connectionHandler.getSourceIpAddress()));
            diameterPeer.setRemotePort(connectionHandler.getSourcePort());
          }  
        if (diameterPeer.getHostIdentity() == null || diameterPeer.getHostIdentity().trim().isEmpty()) {
          diameterPeer.setHostIdentity(diameterPacket.getAVPValue("0:264"));
          this.diameterPeersTable.addPeer(diameterPeer);
          this.diameterStatisticListener.addDiameterPeer(diameterPeer);
        } 
        PacketValidationCode validationCode = validatePacket(diameterPacket);
        if (validationCode == PacketValidationCode.VALID_PACKET) {
          addInfoAVPs(diameterPacket, connectionHandler);
          SessionsFactory diameterSessionFactory = this.sessionFactoryManager.getSessionFactory(diameterPacket.getApplicationID());
          ISession session = null;
          String sessionID = diameterPacket.getSessionID();
          if (sessionID != null)
            session = diameterSessionFactory.readOnlySession(sessionID); 
          if (diameterPacket.isRequest()) {
            diameterPacket.getAsDiameterRequest().setRequestingHost(diameterPeer.getHostIdentity());
          } else {
            diameterPacket.getAsDiameterAnswer().setAnsweringHost(diameterPeer.getHostIdentity());
          } 
          applyMonitoryLogLevel(diameterPacket);
          diameterPeer.processReceivedDiameterPacket((Packet)diameterPacket, connectionHandler);
        } else {
          handleReceivedInvalidMessage(diameterPacket, connectionHandler, validationCode);
        } 
      } else {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("DIAMETER-STACK", "Unknown Peer: " + diameterPacket.getAVPValue("0:264")); 
        if (diameterPacket.isRequest()) {
          DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNKNOWN_PEER);
          try {
            if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
              LogManager.getLogger().info("DIAMETER-STACK", "Sending Answer: " + answerPacket.toString()); 
            connectionHandler.send((Packet)answerPacket);
          } catch (IOException e) {
            LogManager.getLogger().error("DIAMETER-STACK", "Error while sending UNKNOWN_PEER answer, reason: " + e.getMessage());
          } 
        } else {
          LogManager.getLogger().error("DIAMETER-STACK", "Recieved Answer from Unknown IP Address: " + connectionHandler.getSourceIpAddress() + " with HbH-ID: " + diameterPacket
              .getHop_by_hopIdentifier() + ". Dropping Answer");
          sendDPRtoUnknownPeer(connectionHandler, DisconnectionCause.DO_NOT_WANT_TO_TALK_TO_YOU);
        } 
        LogManager.getLogger().warn("DIAMETER-STACK", "Closing Connection for IP Address " + connectionHandler
            .getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
        connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
      } 
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STACK", e.getMessage()); 
      LogManager.getLogger().trace("DIAMETER-STACK", e);
    } finally {
      DiameterPacket diameterPacket = (DiameterPacket)packet;
      try {
        if (diameterPacket.getParameter("MONITORED") != null) {
          if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug("DIAMETER-STACK", "Remove thread: " + Thread.currentThread().getName() + " from log monitor"); 
          LogManager.getLogger().removeThreadName(Thread.currentThread().getName());
        } 
      } catch (Exception ex) {
        if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
          LogManager.getLogger().error("DIAMETER-STACK", "Error in removing diameter monitor log. Reason: " + ex.getMessage()); 
        LogManager.getLogger().trace("DIAMETER-STACK", ex);
      } 
    } 
  }
  
  private boolean isLegitPeer(NetworkConnectionHandler connectionHandler, IPeerListener peerListener) {
    if (!connectionHandler.getSourceIpAddress().equals(peerListener.getRemoteInetAddress().getHostAddress())) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STACK", "Connection IPAddress: " + connectionHandler.getSourceIpAddress() + " compared to Peer Address: " + peerListener
            .getRemoteInetAddress().getHostAddress()); 
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().debug("DIAMETER-STACK", "Packet from Unknown-Ip Address received: " + connectionHandler.getSourceIpAddress()); 
      return false;
    } 
    if (peerListener.getHostIdentity() != null && peerListener.getHostIdentity().trim().length() > 0 && 
      !peerListener.getHostIdentity().equals(connectionHandler.getHostName())) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STACK", "Connection HostName: " + connectionHandler.getHostName() + " compared to Peer Host Identity: " + peerListener
            .getHostIdentity()); 
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().debug("DIAMETER-STACK", "Packet from Peer with Unknown Host-Identity received: " + connectionHandler.getHostName()); 
      return false;
    } 
    return true;
  }
  
  private void sendDPRtoUnknownPeer(NetworkConnectionHandler connectionHandler, DisconnectionCause disconnectionCause) {
    LogManager.getLogger().info("DIAMETER-STACK", "Sending DPR to Unknown IP Address: " + connectionHandler
        .getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
    DiameterRequest diameterRequest = new DiameterRequest();
    diameterRequest.setRequestingHost(Parameter.getInstance().getOwnDiameterIdentity());
    diameterRequest.setCommandCode(CommandCode.DISCONNECT_PEER.code);
    diameterRequest.setRequestBit();
    diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
    diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());
    IDiameterAVP disconnectionCauseAVP = DiameterDictionary.getInstance().getAttribute("0:273");
    if (disconnectionCauseAVP == null) {
      LogManager.getLogger().warn("DIAMETER-STACK", "Not Adding Disconnect-Cause AVP to DPR, Reason: AVP not found in dictionary");
    } else {
      disconnectionCauseAVP.setInteger(disconnectionCause.code);
      diameterRequest.addAvp(disconnectionCauseAVP);
    } 
    try {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STACK", diameterRequest.toString()); 
      connectionHandler.send((Packet)diameterRequest);
    } catch (Exception e) {
      LogManager.getLogger().error("DIAMETER-STACK", "Error in sending DPR to Unknown-Peer from Conncetion: " + connectionHandler
          .getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
      LogManager.getLogger().trace("DIAMETER-STACK", e);
    } 
  }
  
  protected void addInfoAVPs(DiameterPacket diameterPacket, NetworkConnectionHandler connectionHandler) {
    addHeaderInfoAVPs(diameterPacket, connectionHandler);
    addhorizonInfoAVP(diameterPacket);
    addSubscriptionIdInfoAvp(diameterPacket);
    addUserEquipmentInfoAvp(diameterPacket);
    addPeerInfoAVPs(diameterPacket, connectionHandler);
  }
  
  private void addPeerInfoAVPs(DiameterPacket diameterPacket, NetworkConnectionHandler connectionHandler) {
    IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65625");
    if (diameterAVP == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Originator-Peer-Name(21067:65625) not added in Diameter packet. Reason: attribute not found in dictionary"); 
    } else {
      PeerData peerData1 = this.diameterPeersTable.getPeerData(diameterPacket.getAVPValue("0:264"));
      if (peerData1 == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Originator-Peer-Name(21067:65625) not added in Diameter packet. Reason: Peer data not found for " + diameterPacket
              .getAVPValue("0:264")); 
      } else {
        diameterAVP.setStringValue(peerData1.getPeerName());
        diameterPacket.addInfoAvp(diameterAVP);
      } 
    } 
    PeerData peerData = this.diameterPeersTable.getPeerData(connectionHandler.getHostName());
    if (peerData == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Proxy-Agent-Name(21067:65626) not added in Diameter packet. Reason: Peer data not found for " + connectionHandler
            .getHostName()); 
    } else if (!peerData.getPeerName().equals(diameterPacket.getInfoAVPValue("21067:65625"))) {
      diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65626");
      if (diameterAVP == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Proxy-Agent-Name(21067:65626) not added in Diameter packet. Reason: attribute not found in dictionary"); 
      } else {
        diameterAVP.setStringValue(peerData.getPeerName());
        diameterPacket.addInfoAvp(diameterAVP);
      } 
    } 
  }
  
  private void addhorizonInfoAVP(DiameterPacket diameterPacket) {
    IDiameterAVP serverName = DiameterDictionary.getInstance().getKnownAttribute("21067:195");
    if (serverName != null) {
      serverName.setStringValue(getDiameterStackIdentity());
      diameterPacket.addInfoAvp(serverName);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
      LogManager.getLogger().info("DIAMETER-STACK", "EC-Server Name attribute not found in horizon dictionary");
    } 
    IDiameterAVP domainName = DiameterDictionary.getInstance().getKnownAttribute("21067:196");
    if (domainName != null) {
      domainName.setStringValue(getDiameterStackRealm());
      diameterPacket.addInfoAvp(domainName);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
      LogManager.getLogger().info("DIAMETER-STACK", "EC-Domain Name attribute not found in horizon dictionary");
    } 
    IDiameterAVP serverInstanceID = DiameterDictionary.getInstance().getKnownAttribute("21067:143");
    if (serverInstanceID != null) {
      serverInstanceID.setStringValue(getServerInstanceId());
      diameterPacket.addInfoAvp(serverInstanceID);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "EC-Server Instance Id attribute not found in horizon dictionary");
    } 
  }
  
  private void addSubscriptionIdInfoAvp(DiameterPacket diameterPacket) {
    List<IDiameterAVP> subscriptionAvps = diameterPacket.getAVPList("0:443");
    if (subscriptionAvps != null && subscriptionAvps.size() > 0) {
      IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65599");
      if (diameterAVP != null) {
        int numOfSubscriptionAvps = subscriptionAvps.size();
        IDiameterAVP subscriptionAvp = null;
        IDiameterAVP subscriptionType = null;
        IDiameterAVP subscriptionData = null;
        for (int i = 0; i < numOfSubscriptionAvps; i++) {
          try {
            IDiameterAVP subIdDataValueAvp = null;
            subscriptionAvp = subscriptionAvps.get(i);
            subscriptionType = ((AvpGrouped)subscriptionAvp).getSubAttribute("0:450");
            subscriptionData = ((AvpGrouped)subscriptionAvp).getSubAttribute("0:444");
            if (subscriptionType != null && subscriptionData != null) {
              long intSubType = subscriptionType.getInteger();
              if (intSubType == 0L) {
                subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65600");
              } else if (intSubType == 1L) {
                subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65601");
              } else if (intSubType == 2L) {
                subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65602");
              } else if (intSubType == 3L) {
                subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65603");
              } else if (intSubType == 4L) {
                subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65604");
              } 
              if (subIdDataValueAvp != null) {
                subIdDataValueAvp.setStringValue(subscriptionData.getStringValue());
                ((AvpGrouped)diameterAVP).addSubAvp(subIdDataValueAvp);
              } 
            } 
          } catch (Exception e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
              LogManager.getLogger().error("DIAMETER-STACK", "Skipping adition of Subscription-ID AVP, Reason: " + e.getMessage()); 
            LogManager.getLogger().trace("DIAMETER-STACK", e);
          } 
        } 
        diameterPacket.addInfoAvp(diameterAVP);
      } 
    } 
  }
  
  private void addUserEquipmentInfoAvp(DiameterPacket diameterPacket) {
    List<IDiameterAVP> userEquipmenetAvps = diameterPacket.getAVPList("0:458");
    if (userEquipmenetAvps != null && !userEquipmenetAvps.isEmpty()) {
      IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65609");
      if (diameterAVP != null) {
        int numOfUserEquipmenetAvpss = userEquipmenetAvps.size();
        IDiameterAVP userEquipmenetAvp = null;
        IDiameterAVP euipmentType = null;
        AvpUserEquipmentInfoValue euipmentData = null;
        for (int i = 0; i < numOfUserEquipmenetAvpss; i++) {
          try {
            userEquipmenetAvp = userEquipmenetAvps.get(i);
            euipmentType = ((AvpGrouped)userEquipmenetAvp).getSubAttribute("0:459");
            try {
              euipmentData = (AvpUserEquipmentInfoValue)((AvpGrouped)userEquipmenetAvp).getSubAttribute("0:460");
            } catch (ClassCastException e) {
              if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                LogManager.getLogger().info("DIAMETER-STACK", "Skipping adition of User-Equipment-Info AVP, Reason: " + e.getMessage()); 
              LogManager.getLogger().trace("DIAMETER-STACK", e);
              return;
            } 
            if (euipmentType != null && euipmentData != null) {
              long intEquipmentType = euipmentType.getInteger();
              if (intEquipmentType == 0L) {
                String val = null;
                Map<String, String> imeisvMap = euipmentData.getIMEISV();
                IDiameterAVP euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65610");
                StringBuilder strIMEISV = new StringBuilder();
                IDiameterAVP userEquipmentTAC = DiameterDictionary.getInstance().getKnownAttribute("21067:65617");
                if (userEquipmentTAC != null) {
                  val = imeisvMap.get("TAC");
                  if (val != null) {
                    userEquipmentTAC.setStringValue(val);
                    strIMEISV.append(val);
                    ((AvpGrouped)diameterAVP).addSubAvp(userEquipmentTAC);
                  } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug("DIAMETER-STACK", "Skipping adition of EC_USER_EQUIPMENT_IMEISV_TAC AVP, Reason : TAC value is null");
                  } 
                } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                  LogManager.getLogger().debug("DIAMETER-STACK", "Skipping adition of EC_USER_EQUIPMENT_IMEISV_TAC AVP, Reason : AVP not found in dictionary");
                } 
                IDiameterAVP userEquipmentSNR = DiameterDictionary.getInstance().getKnownAttribute("21067:65618");
                if (userEquipmentSNR != null) {
                  val = imeisvMap.get("SNR");
                  if (val != null) {
                    userEquipmentSNR.setStringValue(val);
                    strIMEISV.append(val);
                    ((AvpGrouped)diameterAVP).addSubAvp(userEquipmentSNR);
                  } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug("DIAMETER-STACK", "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SNR AVP, Reason : SNR value is null");
                  } 
                } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                  LogManager.getLogger().debug("DIAMETER-STACK", "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SNR AVP, Reason : AVP not found in dictionary");
                } 
                IDiameterAVP userEquipmentSVN = DiameterDictionary.getInstance().getKnownAttribute("21067:65619");
                if (userEquipmentSVN != null) {
                  val = imeisvMap.get("SVN");
                  if (val != null) {
                    userEquipmentSVN.setStringValue(val);
                    strIMEISV.append(val);
                    ((AvpGrouped)diameterAVP).addSubAvp(userEquipmentSVN);
                  } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug("DIAMETER-STACK", "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SVN AVP, Reason : SVN value is null");
                  } 
                } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                  LogManager.getLogger().debug("DIAMETER-STACK", "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SVN AVP, Reason : AVP not found in dictionary");
                } 
                if (euipmentDataValueAvp != null) {
                  euipmentDataValueAvp.setStringValue(strIMEISV.toString());
                  ((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
                } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                  LogManager.getLogger().debug("DIAMETER-STACK", "Skipping adition of EC_USER_EQUIPMENT_IMEISV AVP, Reason : AVP not found in dictionary");
                } 
              } else if (intEquipmentType == 1L) {
                IDiameterAVP euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65611");
                if (euipmentDataValueAvp != null) {
                  euipmentDataValueAvp.setStringValue(euipmentData.getStringValue());
                  ((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
                } 
              } else if (intEquipmentType == 2L) {
                IDiameterAVP euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65612");
                if (euipmentDataValueAvp != null) {
                  euipmentDataValueAvp.setStringValue(euipmentData.getStringValue());
                  ((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
                } 
              } else if (intEquipmentType == 3L) {
                IDiameterAVP euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65613");
                if (euipmentDataValueAvp != null) {
                  euipmentDataValueAvp.setStringValue(euipmentData.getStringValue());
                  ((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
                } 
              } 
            } 
          } catch (Exception e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
              LogManager.getLogger().error("DIAMETER-STACK", "Skipping adition of User-Equipment-Info AVP, Reason: " + e.getMessage()); 
            LogManager.getLogger().trace("DIAMETER-STACK", e);
          } 
        } 
        diameterPacket.addInfoAvp(diameterAVP);
      } 
    } 
  }
  
  public void setDiameterStackURI(String ownDiameterURI) {
    this.ownDiameterURI = ownDiameterURI;
  }
  
  public String getDiameterStackURI() {
    return this.ownDiameterURI;
  }
  
  public void setDiameterStackIdentity(String ownDiameterIdentity) {
    this.ownDiameterIdentity = ownDiameterIdentity;
  }
  
  protected void setIsNAIEnabled(boolean isNAIEnabled) {
    this.isNAIEnabled = isNAIEnabled;
  }
  
  protected void setNAIRealms(List<String> naiRealmNames) {
    this.naiRealmNames = naiRealmNames;
  }
  
  protected void setIsRealmVerificationRequired(boolean isRealmVerificationRequired) {
    this.isRealmVerificationRequired = isRealmVerificationRequired;
  }
  
  public String getDiameterStackIdentity() {
    return this.ownDiameterIdentity;
  }
  
  public void setDiameterStackRealm(String ownDiameterRealm) {
    this.ownDiameterRealm = ownDiameterRealm;
  }
  
  public void setRoutingTableName(String routingTableName) {
    this.routingTableName = routingTableName;
  }
  
  public String getRoutingTable() {
    return this.routingTableName;
  }
  
  public String getDiameterStackRealm() {
    return this.ownDiameterRealm;
  }
  
  public String getServerInstanceId() {
    return this.serverInstanceId;
  }
  
  public void setServerInstanceId(String serverInstanceId) {
    this.serverInstanceId = serverInstanceId;
  }
  
  private void initDiameterPeers() {
    for (PeerData peerData : this.peerDataList) {
      try {
        DiameterPeer diameterPeer = new DiameterPeer(peerData, this.stackContext, this.diameterRouter, this.sessionFactoryManager, this.diameterAppMessageHandler, this.explicitRoutingHandler, this.duplicateMessageHandler);
        diameterPeer.init();
        this.diameterPeersTable.addPeer(diameterPeer);
      } catch (Exception ex) {
        LogManager.getLogger().error("DIAMETER-STACK", "Error while creating DiameterPeer from PeerData: " + peerData.getPeerName() + ". Reason:" + ex.getMessage());
        LogManager.getLogger().trace("DIAMETER-STACK", ex);
      } 
    } 
  }
  
  private void startDiameterPeers() {
    for (DiameterPeer peer : this.diameterPeersTable.getPeerList())
      peer.start(); 
  }
  
  private List<InetAddress> getAvailableInetAddresses(String remoteIPAddress) {
    try {
      List<String> availableIPs = DiameterUtility.getAvailableIPs(remoteIPAddress);
      if (availableIPs == null || availableIPs.isEmpty())
        return null; 
      List<InetAddress> inetAddresses = new ArrayList<>();
      for (String address : availableIPs) {
        try {
          InetAddress inetAddr = InetAddress.getByName(address);
          inetAddresses.add(inetAddr);
        } catch (UnknownHostException e) {
          LogManager.getLogger().debug("DIAMETER-STACK", "Unknown host address " + address + ". Reason: " + e.getMessage());
          LogManager.ignoreTrace(e);
        } 
      } 
      return inetAddresses;
    } catch (NumberFormatException e) {
      LogManager.getLogger().error("DIAMETER-STACK", "Error while getting available IP address from range " + remoteIPAddress + ". Reason: " + e.getMessage());
    } catch (Exception e) {
      LogManager.getLogger().error("DIAMETER-STACK", "Error while getting available IP address from range " + remoteIPAddress + ". Reason: " + e.getMessage());
      LogManager.getLogger().trace("DIAMETER-STACK", e);
    } 
    return null;
  }
  
  public synchronized boolean stop() {
    if (this.currentStackStatus == Status.STOPPED) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIAMETER-STACK", "Diameter Stack is already Stopped, Rejecting Stop Operation"); 
      return false;
    } 
    this.currentStackStatus = Status.STOPPING;
    if (this.scheduledExecutorService != null)
      try {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("DIAMETER-STACK", "Stop session clean up task"); 
        this.scheduledExecutorService.shutdownNow();
      } catch (Exception ex) {
        LogManager.getLogger().trace("DIAMETER-STACK", ex);
      }  
    Collection<DiameterPeer> peers = this.diameterPeersTable.getPeerList();
    Iterator<DiameterPeer> peerItr = peers.iterator();
    while (peerItr.hasNext()) {
      DiameterPeer diameterPeer = peerItr.next();
      diameterPeer.stop();
    } 
    if (this.scheduledThreadPoolExecutor != null) {
      this.scheduledThreadPoolExecutor.shutdown();
      try {
        LogManager.getLogger().info("DIAMETER-STACK", "Waiting for server level Scheduled async task executor to complete execution");
        if (!this.scheduledThreadPoolExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
          LogManager.getLogger().info("DIAMETER-STACK", "Shutting down thread pool executer forcefully, Reason: Async task taking more time to complete");
          this.scheduledThreadPoolExecutor.shutdownNow();
        } 
      } catch (InterruptedException e) {
        this.scheduledThreadPoolExecutor.shutdownNow();
        Thread.currentThread().interrupt();
      } 
    } 
    if (!super.stop()) {
      LogManager.getLogger().error("DIAMETER-STACK", "Unknown Problem in Stopping Diameter Stack");
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("DIAMETER-STACK", "Diameter Stack stopped successfully");
    } 
    stopWorkerThreads();
    try {
      this.mibIndexRecorder.store();
    } catch (FileAllocatorException e) {
      LogManager.getLogger().error("DIAMETER-STACK", "Unable to serialize Diameter MIB Indices, Reason " + e
          .getMessage() + ". This may effect SNMP Index Management");
      LogManager.ignoreTrace((Exception)e);
    } 
    Stack.generateAlert(StackAlertSeverity.CRITICAL, DiameterStackAlerts.DIAMETER_STACK_DOWN, "DIAMETER-STACK", "Diameter Stack Down");
    this.currentStackStatus = Status.STOPPED;
    return true;
  }
  
  private void stopWorkerThreads() {
    try {
      if (this.subProcessThreadExecutor != null) {
        this.subProcessThreadExecutor.shutdown();
        try {
          LogManager.getLogger().info("DIAMETER-STACK", "Waiting for connection level Scheduled async task executor to complete execution");
          if (!this.subProcessThreadExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
            LogManager.getLogger().info("DIAMETER-STACK", "Shutting down connection level Scheduled forcefully, Reason: Async task taking more time to complete");
            this.subProcessThreadExecutor.shutdownNow();
          } 
        } catch (InterruptedException e) {
          this.subProcessThreadExecutor.shutdownNow();
          Thread.currentThread().interrupt();
        } 
      } 
      if (this.baseThreadExecutor != null) {
        this.baseThreadExecutor.shutdown();
        try {
          LogManager.getLogger().info("DIAMETER-STACK", "Waiting for Base message connection level Scheduled async task executor to complete execution");
          if (!this.baseThreadExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
            LogManager.getLogger().info("DIAMETER-STACK", "Shutting down Base message connection level Scheduled forcefully, Reason: Async task taking more time to complete");
            this.baseThreadExecutor.shutdownNow();
          } 
        } catch (InterruptedException e) {
          this.baseThreadExecutor.shutdownNow();
          Thread.currentThread().interrupt();
        } 
      } 
    } catch (Exception ex) {
      LogManager.getLogger().trace("DIAMETER-STACK", ex);
    } 
  }
  
  public DiameterStatisticListener getDiameterStatisticListner() {
    return this.diameterStatisticListener;
  }
  
  public class DiameterStackContext implements IDiameterStackContext {
    DiameterStack.TranslationSequencer translationSequencer = new DiameterStack.TranslationSequencer();
    
    public void updateInputStatistics(DiameterPacket packet, String hostIdentity) {
      DiameterStack.this.diameterStatisticListener.updateInputStatistics(packet, hostIdentity);
    }
    
    public void updateOutputStatistics(DiameterPacket packet, String hostIdentity) {
      DiameterStack.this.diameterStatisticListener.updateOutputStatistics(packet, hostIdentity);
    }
    
    public void updateRealmInputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
      DiameterStack.this.diameterStatisticListener.updateRealmInputStatistics(packet, realmName, routeAction);
    }
    
    public void updateRealmOutputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
      DiameterStack.this.diameterStatisticListener.updateRealmOutputStatistics(packet, realmName, routeAction);
    }
    
    public void updateTimeoutRequestStatistics(DiameterRequest diameterRequest, String hostIdentity) {
      DiameterStack.this.diameterStatisticListener.updateTimeoutRequestStatistics(diameterRequest, hostIdentity);
    }
    
    public void updateRealmTimeoutRequestStatistics(DiameterRequest diameterRequest, String realmName, RoutingActions routingAction) {
      DiameterStack.this.diameterStatisticListener.updateRealmTimeoutRequestStatistics(diameterRequest, realmName, routingAction);
    }
    
    public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity) {
      DiameterStack.this.diameterStatisticListener.updateUnknownH2HDropStatistics(answer, hostIdentity);
    }
    
    public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity, String realmName, RoutingActions routeAction) {
      DiameterStack.this.diameterStatisticListener.updateUnknownH2HDropStatistics(answer, hostIdentity, realmName, routeAction);
    }
    
    public void updateDiameterStatsMalformedPacket(DiameterPacket packet, String hostIdentity) {
      DiameterStack.this.diameterStatisticListener.updateMalformedPacketCount(packet, hostIdentity);
    }
    
    public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, String hostIdentity) {
      DiameterStack.this.diameterStatisticListener.updatePacketDroppedStatistics(packet, hostIdentity);
    }
    
    public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, String hostIdentity, String realmName, RoutingActions routeAction) {
      DiameterStack.this.diameterStatisticListener.updatePacketDroppedStatistics(packet, hostIdentity, realmName, routeAction);
    }
    
    public void updateDuplicatePacketStatistics(DiameterPacket packet, String hostIdentity) {
      DiameterStack.this.diameterStatisticListener.updateDuplicatePacketStatistics(packet, hostIdentity);
    }
    
    public ScheduledFuture<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
      return DiameterStack.this.scheduleIntervalBasedTask(task);
    }
    
    public ScheduledFuture<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
      return DiameterStack.this.scheduleSingleExecutionTask(task);
    }
    
    public boolean hasSession(String sessionId, long appId) {
      return DiameterStack.this.sessionFactoryManager.getSessionFactory(appId).hasSession(sessionId);
    }
    
    public ISession readOnlySession(String sessionId, long appId) {
      return DiameterStack.this.sessionFactoryManager.getSessionFactory(appId).readOnlySession(sessionId);
    }
    
    public Session getOrCreateSession(String sessionId, long appId) {
      return DiameterStack.this.sessionFactoryManager.getSessionFactory(appId).getOrCreateSession(sessionId);
    }
    
    public Session generateSession(long appId) {
      return generateSession(null, appId);
    }
    
    @Nullable
    public Session generateSession(@Nullable String sessionIdSuffix, long appId) {
      String newSessionId;
      if (sessionIdSuffix != null) {
        newSessionId = getNextSessionID(sessionIdSuffix);
      } else {
        newSessionId = getNextSessionID();
      } 
      return DiameterStack.this.sessionFactoryManager.getSessionFactory(appId).getOrCreateSession(newSessionId);
    }
    
    public TaskScheduler getTaskScheduler() {
      return DiameterStack.this.getTaskScheduler();
    }
    
    public Set<ApplicationEnum> getApplicationsIdentifiersList() {
      return DiameterStack.this.supportedApplicationIdentifiers;
    }
    
    public void finalPreResponseProcess(DiameterPacket packet) {
      if (packet != null)
        DiameterStack.this.finalPreResponseProcess(packet); 
    }
    
    public PeerData getPeerData(String hostIdentity) {
      return DiameterStack.this.getPeerData(hostIdentity);
    }
    
    public boolean validate() {
      return DiameterStack.this.rfcValidation;
    }
    
    public DiameterPeerState registerPeerStatusListener(String peerName, DiameterPeerStatusListener listener) throws StatusListenerRegistrationFailException {
      if (DiameterStack.this.currentStackStatus == Status.NOT_INITIALIZE)
        throw new StatusListenerRegistrationFailException("Peer Not found", StatusListenerRegistrationFailException.ListenerRegFailResultCode.STACK_NOT_INITIALIZED); 
      if (DiameterStack.this.currentStackStatus == Status.STOPPED || DiameterStack.this.currentStackStatus == Status.STOPPING)
        throw new StatusListenerRegistrationFailException("Peer Not found", StatusListenerRegistrationFailException.ListenerRegFailResultCode.STOP_CALLED); 
      DiameterPeer peer = DiameterStack.this.diameterPeersTable.getPeerByName(peerName);
      if (peer == null) {
        for (PeerData peerData : DiameterStack.this.peerDataList) {
          if (peerData.getPeerName().equals(peerName)) {
            peer = DiameterStack.this.diameterPeersTable.getPeerByName(peerName);
            if (peer == null)
              throw new StatusListenerRegistrationFailException("Peer Not found", StatusListenerRegistrationFailException.ListenerRegFailResultCode.STARTUP_IN_PROGRESS); 
          } 
        } 
        if (peer == null)
          throw new StatusListenerRegistrationFailException("Peer Not found", StatusListenerRegistrationFailException.ListenerRegFailResultCode.PEER_NOT_FOUND); 
      } 
      return peer.registerStatusListener(listener);
    }
    
    public INetworkConnector getNetworkConnector(TransportProtocols transportProtocol) {
      return DiameterStack.this.getNetworkConnector(transportProtocol);
    }
    
    public long getNextServerSequence() {
      return this.translationSequencer.getNextServerSequence();
    }
    
    public long getNextPeerSequence(String hostIdentity) {
      return this.translationSequencer.getNextPeerSequence(hostIdentity);
    }
    
    public String getNextSessionID() {
      if (DiameterStack.this.sessLowerBit.compareAndSet(2147483647, 0) && 
        !DiameterStack.this.sessHigherBit.compareAndSet(2147483647, 1))
        DiameterStack.this.sessHigherBit.getAndIncrement(); 
      int higherBit = DiameterStack.this.sessHigherBit.get();
      return Parameter.getInstance().getOwnDiameterIdentity() + ";" + higherBit + ";" + DiameterStack.this.sessLowerBit.getAndIncrement();
    }
    
    public String getNextSessionID(String optionalVal) {
      String sessionID = getNextSessionID();
      if (optionalVal != null)
        sessionID = sessionID + ";" + optionalVal; 
      return sessionID;
    }
    
    public boolean isNAIEnabled() {
      return DiameterStack.this.isNAIEnabled;
    }
    
    public boolean isValidNAIRealm(String realm) {
      if (!DiameterStack.this.isRealmVerificationRequired)
        return true; 
      return DiameterStack.this.naiRealmNames.contains(realm);
    }
    
    public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> task) {
      return DiameterStack.this.scheduleCallableSingleExecutionTask(task);
    }
    
    public EliteSSLContextFactory getEliteSSLContextFactory() {
      return DiameterStack.this.eliteSSLContextFactory;
    }
    
    public VirtualConnectionHandler registerVirtualPeer(PeerData peerData, VirtualOutputStream outpurStream) throws ElementRegistrationFailedException {
      return DiameterStack.this.registerVirtualPeer(peerData, outpurStream);
    }
    
    public void clearMDC() {
      DiameterStack.this.clearMDC();
    }
    
    public void addMDC(DiameterPacket diameterPacket) {
      DiameterStack.this.addMDC(diameterPacket);
    }
    
    public void purgeCancelledTasks() {
      DiameterStack.this.purgeCancelledTasks();
    }
    
    public boolean isEREnabled() {
      return true;
    }
    
    public int getTotalActiveSessionCount() {
      return DiameterStack.this.sessionFactoryManager.getSessionCount();
    }
    
    public int getOverloadResultCode() {
      return DiameterStack.this.resultCodeOnOverload;
    }
    
    public OverloadAction getActionOnOverload() {
      return DiameterStack.this.actionOnOverload;
    }
    
    public boolean isOverLoad(DiameterRequest diameterRequest) {
      return DiameterStack.this.isThresholdForLicenceTPSReached();
    }
    
    public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException {
      if (DiameterStack.this.diameterCDRDriverFactory == null)
        throw new DriverNotFoundException("Unable to Create Driver configured with the Name: " + name); 
      return DiameterStack.this.diameterCDRDriverFactory.getDriver(name);
    }
    
    public void scheduleSingleExecutionTask(Runnable command) {
      DiameterStack.this.scheduleSingleExecutionTask(command);
    }
    
    public DiameterStatisticsProvider getDiameterStatisticsProvider() {
      return DiameterStack.this.diameterStatisticListener.getDiameterStatisticProvider();
    }
    
    public long releasePeerSessions(DiameterRequest request) {
      if (DiameterStack.this.diameterSessionManager != null)
        DiameterStack.this.diameterSessionManager.delete(request, new DiameterAnswer(request)); 
      SessionsFactory diameterSessionFactory = DiameterStack.this.sessionFactoryManager.getSessionFactory(request.getApplicationID());
      return diameterSessionFactory.removeAllSessions(request.getAVPValue("0:264"));
    }
    
    public boolean isServerInitiatedMessage(int commandCode) {
      return DiameterStack.this.isServerInitiatedMessage(commandCode);
    }
    
    public void submitToWorker(DiameterStack.PacketProcess packetProcess) {
      DiameterStack.this.submitToWorker(packetProcess);
    }
    
    public int getMaxWorkerThreads() {
      return DiameterStack.this.getMaxThreadPoolSize();
    }
    
    public DiameterPeerCommunicator getPeerCommunicator(String hostIdentityOrName) {
      return DiameterStack.this.diameterPeerCommunicatorFactory.createInstance(hostIdentityOrName);
    }
  }
  
  public boolean isThresholdForLicenceTPSReached() {
    return false;
  }
  
  public boolean isServerInitiatedMessage(int commandCode) {
    return (CommandCode.getCommandCode(commandCode)).isServerInitiated;
  }
  
  public TaskScheduler getTaskScheduler() {
    return this.taskScheduler;
  }
  
  public PeerData getPeerData(String hostIdentityOrIP) {
    if (hostIdentityOrIP == null)
      return null; 
    return this.diameterPeersTable.getPeerData(hostIdentityOrIP);
  }
  
  public Map<String, IStateEnum> getPeersState() {
    return this.diameterPeersTable.getPeersState();
  }
  
  public boolean isValidPeer(String hostIdentity) {
    return this.diameterPeersTable.getPeersState().containsKey(hostIdentity);
  }
  
  private void finalPreResponseProcess(DiameterPacket diameterPacket) {
    ISession session = null;
    String sessionID = diameterPacket.getSessionID();
    SessionsFactory diameterSessionFactory = this.sessionFactoryManager.getSessionFactory(diameterPacket.getApplicationID());
    if (sessionID != null)
      session = diameterSessionFactory.readOnlySession(sessionID); 
    if (diameterPacket.isRequest()) {
      //
    } else {
      IDiameterAVP resultCodeAvp = diameterPacket.getAVP("0:268");
      if (resultCodeAvp != null && 
        ResultCodeCategory.getResultCodeCategory(resultCodeAvp.getInteger()) == ResultCodeCategory.RC3XXX)
        diameterPacket.setErrorBit(); 
    } 
  }
  
  public String getRealm(String hostIdentity) {
    DiameterPeer diameterPeer = this.diameterPeersTable.getPeer(hostIdentity);
    if (diameterPeer != null)
      return diameterPeer.getRealm(); 
    return null;
  }
  
  public DiameterStackContext getStackContext() {
    return this.stackContext;
  }
  
  public class TranslationSequencer {
    private AtomicLong serverSequence = new AtomicLong(0L);
    
    private AtomicLong ownIdentitySequence = new AtomicLong(0L);
    
    private long getNextServerSequence() {
      return this.serverSequence.getAndAdd(1L);
    }
    
    private long getNextPeerSequence(String hostIdentity) {
      DiameterPeer diameterPeer = DiameterStack.this.diameterPeersTable.getPeer(hostIdentity);
      if (diameterPeer != null)
        return diameterPeer.getNextSequence(); 
      return this.ownIdentitySequence.getAndAdd(1L);
    }
  }
  
  private void addHeaderInfoAVPs(DiameterPacket diameterPacket, NetworkConnectionHandler connectionHandler) {
    IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65536");
    if (diameterAVP != null) {
      diameterAVP.setStringValue(String.valueOf(diameterPacket.getVersion()));
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Diameter-version(21067:65536) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65537");
    if (diameterAVP != null) {
      if (diameterAVP.isGrouped()) {
        AvpGrouped avpGrouped = (AvpGrouped)diameterAVP;
        IDiameterAVP subAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65542");
        if (subAvp != null) {
          subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isRequest()));
          avpGrouped.addSubAvp(subAvp);
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Request(21067:65542) not added in Diameter packet. Reason: attribute not found in dictionary");
        } 
        subAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65543");
        if (subAvp != null) {
          subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isProxiable()));
          avpGrouped.addSubAvp(subAvp);
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Proxy(21067:65543) not added in Diameter packet. Reason: attribute not found in dictionary");
        } 
        subAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65544");
        if (subAvp != null) {
          subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isError()));
          avpGrouped.addSubAvp(subAvp);
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Error(21067:65544) not added in Diameter packet. Reason: attribute not found in dictionary");
        } 
        subAvp = DiameterDictionary.getInstance().getKnownAttribute("21067:65545");
        if (subAvp != null) {
          subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isReTransmitted()));
          avpGrouped.addSubAvp(subAvp);
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Re-Transmitted(21067:65545) not added in Diameter packet. Reason: attribute not found in dictionary");
        } 
        if (avpGrouped.getGroupedAvp().size() > 1) {
          diameterPacket.addInfoAvp(diameterAVP);
        } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
          LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Command-Flags(21067:65537) not added in Diameter packet. Reason: No sub attribute added in grouped attribute");
        } 
      } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
        LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Command-Flags(21067:65537) not added in Diameter packet. Reason: attribute must be grouped");
      } 
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Command-Flags(21067:65537) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65538");
    if (diameterAVP != null) {
      diameterAVP.setStringValue(String.valueOf(diameterPacket.getCommandCode()));
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Command-Code(21067:65538) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65539");
    if (diameterAVP != null) {
      diameterAVP.setStringValue(String.valueOf(diameterPacket.getApplicationID()));
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Application-Id(21067:65539) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65540");
    if (diameterAVP != null) {
      diameterAVP.setValueBytes(DiameterUtility.intToByteArray(diameterPacket.getHop_by_hopIdentifier()));
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Hop-by-Hop-Identifier(21067:65540) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65541");
    if (diameterAVP != null) {
      diameterAVP.setValueBytes(DiameterUtility.intToByteArray(diameterPacket.getEnd_to_endIdentifier()));
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-End-to-End-Identifier(21067:65541) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65546");
    if (diameterAVP != null) {
      diameterAVP.setStringValue(connectionHandler.getSourceIpAddress());
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Source-Ip-Address(21067:65546) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:65547");
    if (diameterAVP != null) {
      diameterAVP.setStringValue(String.valueOf(connectionHandler.getSourcePort()));
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Source-Port(21067:65547) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
    diameterAVP = DiameterDictionary.getInstance().getKnownAttribute("21067:206");
    if (diameterAVP != null) {
      diameterAVP.setStringValue(connectionHandler.getHostName());
      diameterPacket.addInfoAvp(diameterAVP);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
      LogManager.getLogger().debug("DIAMETER-STACK", "Info Attribute EC-Reqeuster-Id(21067:206) not added in Diameter packet. Reason: attribute not found in dictionary");
    } 
  }
  
  private class AsyncTaskContextImpl implements AsyncTaskContext {
    private Map<String, Object> attributes;
    
    private AsyncTaskContextImpl() {}
    
    public synchronized void setAttribute(String key, Object attribute) {
      if (this.attributes == null)
        this.attributes = new HashMap<>(); 
      this.attributes.put(key, attribute);
    }
    
    public Object getAttribute(String key) {
      if (this.attributes != null)
        return this.attributes.get(key); 
      return null;
    }
  }
  
  public void scheduleSingleExecutionTask(Runnable command) {
    this.scheduledThreadPoolExecutor.execute(command);
  }
  
  private <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(final CallableSingleExecutionAsyncTask<T> task) {
    if (task == null)
      return null; 
    if (task.getInitialDelay() > 0L)
      return this.scheduledThreadPoolExecutor.schedule(new Callable<T>() {
            public T call() {
              DiameterStack.AsyncTaskContextImpl taskContext = new DiameterStack.AsyncTaskContextImpl();
              return (T)task.execute(taskContext);
            }
          },  task.getInitialDelay(), task.getTimeUnit()); 
    return this.scheduledThreadPoolExecutor.schedule(new Callable<T>() {
          public T call() {
            DiameterStack.AsyncTaskContextImpl taskContext = new DiameterStack.AsyncTaskContextImpl();
            return (T)task.execute(taskContext);
          }
        },  0L, TimeUnit.SECONDS);
  }
  
  private ScheduledFuture<?> scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
    if (task == null)
      return null; 
    if (task.getInitialDelay() > 0L)
      return this.scheduledThreadPoolExecutor.schedule(new Runnable() {
            public void run() {
              DiameterStack.AsyncTaskContextImpl taskContext = new DiameterStack.AsyncTaskContextImpl();
              try {
                task.execute(taskContext);
              } catch (Exception e) {
                LogManager.ignoreTrace(e);
              } 
            }
          },  task.getInitialDelay(), task.getTimeUnit()); 
    return this.scheduledThreadPoolExecutor.schedule(new Runnable() {
          public void run() {
            DiameterStack.AsyncTaskContextImpl taskContext = new DiameterStack.AsyncTaskContextImpl();
            try {
              task.execute(taskContext);
            } catch (Exception e) {
              LogManager.ignoreTrace(e);
            } 
          }
        },  0L, TimeUnit.SECONDS);
  }
  
  private ScheduledFuture<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {
    if (task == null)
      return null; 
    if (task.isFixedDelay())
      return this.scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
              DiameterStack.AsyncTaskContextImpl taskContext = new DiameterStack.AsyncTaskContextImpl();
              try {
                task.preExecute(taskContext);
              } catch (Exception e) {
                LogManager.ignoreTrace(e);
              } 
              try {
                task.execute(taskContext);
              } catch (Exception e) {
                LogManager.ignoreTrace(e);
              } 
              try {
                task.postExecute(taskContext);
              } catch (Exception e) {
                LogManager.ignoreTrace(e);
              } 
            }
          },  task.getInitialDelay(), task.getInterval(), task.getTimeUnit()); 
    return this.scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
          public void run() {
            DiameterStack.AsyncTaskContextImpl taskContext = new DiameterStack.AsyncTaskContextImpl();
            try {
              task.preExecute(taskContext);
            } catch (Exception e) {
              LogManager.ignoreTrace(e);
            } 
            try {
              task.execute(taskContext);
            } catch (Exception e) {
              LogManager.ignoreTrace(e);
            } 
            try {
              task.postExecute(taskContext);
            } catch (Exception e) {
              LogManager.ignoreTrace(e);
            } 
          }
        },  task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
  }
  
  private class TaskSchedulerImpl implements TaskScheduler {
    private TaskSchedulerImpl() {}
    
    public void execute(Runnable command) {
      DiameterStack.this.scheduledExecutorService.execute(command);
    }
    
    public Future<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
      return DiameterStack.this.scheduleSingleExecutionTask(task);
    }
    
    public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
      return DiameterStack.this.scheduleIntervalBasedTask(task);
    }
  }
  
  private void purgeCancelledTasks() {
    this.scheduledThreadPoolExecutor.purge();
  }
  
  public class DiameterSessionCleanupTask implements Runnable {
    long sessionTimeOut;
    
    public DiameterSessionCleanupTask(long sessionTimeOut) {
      this.sessionTimeOut = sessionTimeOut;
    }
    
    public void run() {
      DiameterStack.this.sessionFactoryManager.removeIdleSession(this.sessionTimeOut);
    }
  }
  
  public void setSessionInterval(long sessionCleanupInterval) {
    this.sessionCleanupInterval = sessionCleanupInterval;
  }
  
  public long getSessionInterval() {
    return this.sessionCleanupInterval;
  }
  
  public void setSessionTimeOut(long sessionTimeOut) {
    this.sessionTimeOut = sessionTimeOut;
  }
  
  public long getSessionTimeOut() {
    return this.sessionTimeOut;
  }
  
  @Nonnull
  public List<ApplicationListener> getDiameterApplicationListeners() {
    return this.applicationListeners;
  }
  
  public void setActionOnOverload(OverloadAction actionOnOverload) {
    this.actionOnOverload = actionOnOverload;
  }
  
  public void setResultCodeOnOverload(int resultCodeOnOverload) {
    if (ResultCode.isValid(resultCodeOnOverload))
      this.resultCodeOnOverload = resultCodeOnOverload; 
  }
  
  public SessionFactoryManager getSessionFactoryManager() {
    return this.sessionFactoryManager;
  }
  
  public void applyMonitoryLogLevel(DiameterPacket diameterPacket) {}
  
  public boolean closePeer(String peerHostIdentity) {
    DiameterPeer peer = this.peerProvider.getPeer(peerHostIdentity);
    if (peer == null)
      return false; 
    peer.handleEvent((IEventEnum)DiameterPeerEvent.Stop, ConnectionEvents.CONNECTION_DPR);
    return true;
  }
  
  public boolean forceClosePeer(String peerHostIdentity) {
    DiameterPeer peer = this.peerProvider.getPeer(peerHostIdentity);
    if (peer == null)
      return false; 
    peer.closeConnection(ConnectionEvents.FORCE_CLOSE);
    peer.handleEvent((IEventEnum)DiameterPeerEvent.RPeerDisc, ConnectionEvents.FORCE_CLOSE);
    return true;
  }
  
  public boolean startPeer(String peerHostIdentity) {
    DiameterPeer peer = this.peerProvider.getPeer(peerHostIdentity);
    if (peer == null) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("DIAMETER-STACK", "Unable to start peer. Reason: Peer " + peerHostIdentity + " not found"); 
      return false;
    } 
    if (peer.getRemoteInetAddress() == null) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("DIAMETER-STACK", "Unable to start peer. Reason: Remote address not found for peer " + peerHostIdentity); 
      return false;
    } 
    peer.handleEvent((IEventEnum)DiameterPeerEvent.Start, null, null);
    return true;
  }
  
  public void registerMIBIndexRecorder(String basePath) throws FileAllocatorException {
    this.mibIndexRecorder.build(basePath);
  }
  
  public void registerDiameterSessionManager(IDiameterSessionManager diameterSessionManager) {
    this.diameterSessionManager = diameterSessionManager;
    this.diameterRouter.registerDiameterSessionManager(diameterSessionManager);
  }
  
  public void setDuplicatePacketPurgeInterval(int dupicatePacketPurgeIntervalInSec) {
    this.duplicateMessageHandler.setDupicatePacketPurgeIntervalInSec(dupicatePacketPurgeIntervalInSec);
  }
  
  public void setDuplicateDetectionEnabled(boolean duplicateDetectionEnabled) {
    this.duplicateMessageHandler.duplicateDetectionEnabled(duplicateDetectionEnabled);
  }
  
  public void registerDiameterCDRDriverFactory(DiameterCDRDriverFactory diameterCDRDriverFactory) {
    this.diameterCDRDriverFactory = diameterCDRDriverFactory;
  }
  
  public Status getStackStatus() {
    return this.currentStackStatus;
  }
  
  public DiameterNetworkStatisticsProvider getStatisticsProvider() {
    return this.statisticsProvider;
  }
  
	public DiameterPeersTable getDiameterPeersTable() {
		return this.diameterPeersTable;
	}

	public Status getCurrentStackStatus() {
		return this.currentStackStatus;
	}
  
  public void submitToWorker(PacketProcess packetProcess) {
    packetProcess.preSubmit();
    try {
      if (DiameterUtility.isBaseProtocolPacket(((DiameterPacket)packetProcess.getPacket()).getCommandCode())) {
        this.baseThreadExecutor.execute(packetProcess);
      } else {
        this.subProcessThreadExecutor.execute(packetProcess);
      } 
    } finally {
      packetProcess.postSubmit();
    } 
  }
  
	public static interface PacketProcess extends Runnable {
		NetworkConnectionHandler getConnectionHandler();

		Packet getPacket();

		void preSubmit();

		void postSubmit();

		void run();
	}

}