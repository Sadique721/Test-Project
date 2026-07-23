package com.diameter.commons;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;

public class DiameterStatistic extends Observable implements DiameterStatisticResetter, DiameterStatisticsProvider {
  private static final String MODULE = "DIAM-STATS";
  
  private Map<String, GroupedStatistics> peerStatisticsMap;
  
  private Map<RealmIdentifier, GroupedStatistics> realmStatisticsMap;
  
  private Map<ApplicationStatsIdentifier, GroupedStatistics> applicationStatisticsMap;
  
  private Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> appSpecificPeerStatisticsMap;
  
  private Map<ApplicationStatsIdentifier, Map<String, RttStatistics>> appSpecificRTTMap;
  
  private Map<ApplicationStatsIdentifier, Map<String, MpsStatistics>> appSpecificMPSMap;
  
  private Set<ApplicationEnum> supportedApplicationEnums;
  
  private GroupedStatistics stackStatistics;
  
  private AtomicLong requestCounter;
  
  private AtomicLong responseCounter;
  
  private AtomicLong totalResponseTimeInMillis;
  
  private long avgIncomingMPS;
  
  private long avgRoundTripTimeMS;
  
  private long lastResetTimeInMilli;
  
  public DiameterStatistic(Set<ApplicationEnum> supportedApplicationEnums, TaskScheduler taskScheduler) {
    this.supportedApplicationEnums = supportedApplicationEnums;
    this.stackStatistics = new GroupedStatistics();
    this.peerStatisticsMap = new ConcurrentHashMap<>();
    this.realmStatisticsMap = new ConcurrentHashMap<>();
    this.applicationStatisticsMap = new ConcurrentHashMap<>();
    this.appSpecificPeerStatisticsMap = new ConcurrentHashMap<>();
    this.appSpecificRTTMap = new ConcurrentHashMap<>();
    this.appSpecificMPSMap = new ConcurrentHashMap<>();
    this.requestCounter = new AtomicLong();
    this.responseCounter = new AtomicLong();
    this.totalResponseTimeInMillis = new AtomicLong();
    taskScheduler.scheduleIntervalBasedTask((IntervalBasedTask)new MPSCalculator());
  }
  
  public void init(Collection<DiameterPeer> peerList) {
    initApplicationMap();
    initPeerStatistics(peerList);
  }
  
  private void initPeerStatistics(Collection<DiameterPeer> peerList) {
    if (peerList != null)
      for (DiameterPeer peer : peerList) {
        if (peer != null && !Strings.isNullOrBlank(peer.getHostIdentity()))
          this.peerStatisticsMap.put(peer.getHostIdentity(), new GroupedStatistics()); 
      }  
  }
  
  private void initApplicationMap() {
    this.applicationStatisticsMap.put(getApplicationStatsIdentifier((ApplicationEnum)ApplicationIdentifier.BASE), new GroupedStatistics());
    if (this.supportedApplicationEnums != null)
      for (ApplicationEnum appEnum : this.supportedApplicationEnums)
        this.applicationStatisticsMap.put(getApplicationStatsIdentifier(appEnum), new GroupedStatistics());  
  }
  
  private ApplicationStatsIdentifier getApplicationStatsIdentifier(ApplicationEnum applicationEnum) {
    return new ApplicationStatsIdentifier(applicationEnum.getApplicationId(), applicationEnum
        .getVendorId(), applicationEnum.getApplication().getDisplayName());
  }
  
  public void updateInputStatistics(DiameterPacket packet, String hostIdentity) {
    if (packet.getApplicationID() != ApplicationIdentifier.BASE.getApplicationId()) {
      this.requestCounter.incrementAndGet();
      updateApplicationWiseMPS(packet, hostIdentity);
    } 
    updatePeerInputStatistics(packet, hostIdentity);
    updateApplicationInputStatistics(packet, hostIdentity);
    this.stackStatistics.incrementInputStatistics(packet);
  }
  
  public void updateOutputStatistics(DiameterPacket packet, String hostIdentity) {
    if (packet.isResponse() && packet.getApplicationID() != ApplicationIdentifier.BASE.getApplicationId() && (
      (DiameterAnswer)packet).getRequestReceivedTime() > 0L) {
      long currentTimeMillis = System.currentTimeMillis();
      this.totalResponseTimeInMillis.addAndGet(currentTimeMillis - ((DiameterAnswer)packet).getRequestReceivedTime());
      this.responseCounter.incrementAndGet();
      updateApplicationWiseRTT(packet.getAsDiameterAnswer(), currentTimeMillis, hostIdentity);
    } 
    updatePeerOutputStatistics(packet, hostIdentity);
    updateApplicationOutputStatistics(packet, hostIdentity);
    this.stackStatistics.incrementOutputStatistics(packet);
  }
  
  private void updateApplicationWiseRTT(DiameterAnswer diameterAnswer, long currentTimeInMillis, String hostIdentity) {
    ApplicationEnum appEnum = getApplicationEnum((DiameterPacket)diameterAnswer);
    if (Objects.isNull(appEnum) || Strings.isNullOrBlank(hostIdentity))
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    Map<String, RttStatistics> applicationStats = getApplicationWiseRTTStatistic(applicationStr);
    RttStatistics applicationPeerRTT = applicationStats.computeIfAbsent(hostIdentity, identity -> new RttStatistics());
    applicationPeerRTT.add(diameterAnswer, currentTimeInMillis);
  }
  
  private void updateApplicationWiseMPS(DiameterPacket packet, String hostIdentity) {
    ApplicationEnum appEnum = getApplicationEnum(packet);
    if (Objects.isNull(appEnum) || Strings.isNullOrBlank(hostIdentity))
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    Map<String, MpsStatistics> applicationStats = getApplicationWiseMpsStatistics(applicationStr);
    MpsStatistics applicationPeerMps = applicationStats.computeIfAbsent(hostIdentity, identity -> new MpsStatistics());
    applicationPeerMps.add();
  }
  
  private void updatePeerInputStatistics(DiameterPacket packet, String hostIdentity) {
    GroupedStatistics peerStatistics = getPeerStatistic(hostIdentity);
    if (peerStatistics == null)
      return; 
    peerStatistics.incrementInputStatistics(packet);
  }
  
  private void updatePeerOutputStatistics(DiameterPacket packet, String hostIdentity) {
    GroupedStatistics peerStatistics = getPeerStatistic(hostIdentity);
    if (peerStatistics == null)
      return; 
    peerStatistics.incrementOutputStatistics(packet);
  }
  
  @Nullable
  private GroupedStatistics getPeerStatistic(String peerIdentity) {
    if (Strings.isNullOrBlank(peerIdentity))
      return null; 
    GroupedStatistics peerStatistics = this.peerStatisticsMap.get(peerIdentity);
    if (peerStatistics == null)
      synchronized (this.peerStatisticsMap) {
        peerStatistics = this.peerStatisticsMap.get(peerIdentity);
        if (peerStatistics == null) {
          peerStatistics = new GroupedStatistics();
          this.peerStatisticsMap.put(peerIdentity, peerStatistics);
          setChanged();
          notifyObservers(peerIdentity);
        } 
      }  
    return peerStatistics;
  }
  
  public void updateRealmInputStatistics(String realmName, RoutingActions routeAction, DiameterPacket packet) {
    GroupedStatistics realmStatistics = getRealmStatistic(realmName, routeAction, packet);
    if (realmStatistics == null)
      return; 
    realmStatistics.incrementInputStatistics(packet);
  }
  
  public void updateRealmOutputStatistics(String realmName, RoutingActions routeAction, DiameterPacket packet) {
    GroupedStatistics realmStatistics = getRealmStatistic(realmName, routeAction, packet);
    if (realmStatistics == null)
      return; 
    realmStatistics.incrementOutputStatistics(packet);
  }
  
  private GroupedStatistics getRealmStatistic(String realmName, RoutingActions routeAction, DiameterPacket packet) {
    RealmIdentifier realmIdentifier = getRealmIdentifier(packet, realmName, routeAction);
    if (realmIdentifier == null)
      return null; 
    GroupedStatistics realmStatistics = this.realmStatisticsMap.get(realmIdentifier);
    if (realmStatistics == null)
      synchronized (this.realmStatisticsMap) {
        realmStatistics = this.realmStatisticsMap.get(realmIdentifier);
        if (realmStatistics == null) {
          realmStatistics = new GroupedStatistics();
          this.realmStatisticsMap.put(realmIdentifier, realmStatistics);
          setChanged();
          notifyObservers(realmIdentifier);
        } 
      }  
    return realmStatistics;
  }
  
  private RealmIdentifier getRealmIdentifier(DiameterPacket packet, String realmName, RoutingActions routeAction) {
    ApplicationEnum applicationEnum = getApplicationEnum(packet);
    if (applicationEnum == null || applicationEnum.getApplication() == null || realmName == null || routeAction == null)
      return null; 
    return new RealmIdentifier(realmName, applicationEnum.getApplicationId(), applicationEnum.getApplicationType(), routeAction);
  }
  
  private void updateApplicationInputStatistics(DiameterPacket packet, String hostIdentity) {
    ApplicationEnum appEnum = getApplicationEnum(packet);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    GroupedStatistics applicationStats = getApplicationStatistic(applicationStr);
    applicationStats.incrementInputStatistics(packet);
    GroupedStatistics peerStatistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
    if (peerStatistics == null)
      return; 
    peerStatistics.incrementInputStatistics(packet);
  }
  
  private void updateApplicationOutputStatistics(DiameterPacket packet, String hostIdentity) {
    ApplicationEnum appEnum = getApplicationEnum(packet);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    GroupedStatistics applicationStats = getApplicationStatistic(applicationStr);
    applicationStats.incrementOutputStatistics(packet);
    GroupedStatistics peerStatistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
    if (peerStatistics == null)
      return; 
    peerStatistics.incrementOutputStatistics(packet);
  }
  
  private GroupedStatistics getApplicationStatistic(ApplicationStatsIdentifier applicationStatsId) {
    GroupedStatistics applicationStats = this.applicationStatisticsMap.get(applicationStatsId);
    if (applicationStats == null)
      synchronized (this.applicationStatisticsMap) {
        applicationStats = this.applicationStatisticsMap.get(applicationStatsId);
        if (applicationStats == null) {
          applicationStats = new GroupedStatistics();
          this.applicationStatisticsMap.put(applicationStatsId, applicationStats);
          setChanged();
          notifyObservers(applicationStatsId);
        } 
      }  
    return applicationStats;
  }
  
  private Map<String, RttStatistics> getApplicationWiseRTTStatistic(ApplicationStatsIdentifier applicationStatsId) {
    return this.appSpecificRTTMap.computeIfAbsent(applicationStatsId, id -> new ConcurrentHashMap<>());
  }
  
  private Map<String, MpsStatistics> getApplicationWiseMpsStatistics(ApplicationStatsIdentifier applicationStatsId) {
    return this.appSpecificMPSMap.computeIfAbsent(applicationStatsId, id -> new ConcurrentHashMap<>());
  }
  
  @Nullable
  private GroupedStatistics getApplicationPeerStatistic(ApplicationStatsIdentifier applicationStatsId, String hostIdentity, DiameterPacket packet) {
    if (Strings.isNullOrBlank(hostIdentity))
      return null; 
    boolean needToNotify = false;
    Map<String, GroupedStatistics> appPeerStatsMap = this.appSpecificPeerStatisticsMap.get(applicationStatsId);
    if (appPeerStatsMap == null)
      synchronized (this.appSpecificPeerStatisticsMap) {
        appPeerStatsMap = this.appSpecificPeerStatisticsMap.get(applicationStatsId);
        if (appPeerStatsMap == null) {
          appPeerStatsMap = new ConcurrentHashMap<>();
          this.appSpecificPeerStatisticsMap.put(applicationStatsId, appPeerStatsMap);
          needToNotify = true;
        } 
      }  
    GroupedStatistics peerStatistics = appPeerStatsMap.get(hostIdentity);
    if (peerStatistics == null)
      synchronized (this.appSpecificPeerStatisticsMap) {
        peerStatistics = appPeerStatsMap.get(hostIdentity);
        if (peerStatistics == null) {
          peerStatistics = new GroupedStatistics();
          appPeerStatsMap.put(hostIdentity, peerStatistics);
          needToNotify = true;
        } 
      }  
    if (needToNotify) {
      setChanged();
      notifyObservers(new DiameterStatisticsEvents(applicationStatsId, hostIdentity));
    } 
    needToNotify = !peerStatistics.getCommandCodeCountersMap().containsKey(Integer.valueOf(packet.getCommandCode()));
    if (needToNotify)
      synchronized (peerStatistics) {
        needToNotify = !peerStatistics.getCommandCodeCountersMap().containsKey(Integer.valueOf(packet.getCommandCode()));
        if (needToNotify) {
          peerStatistics.getCommandCodeCounterTuple(packet);
          setChanged();
          notifyObservers(new DiameterStatisticsEvents(applicationStatsId, hostIdentity, packet.getCommandCode()));
        } 
      }  
    if (packet.isResponse()) {
      IDiameterAVP resultCodeAvp = packet.getAVP("0:268");
      if (resultCodeAvp == null)
        resultCodeAvp = packet.getAVP("0:297.0:298"); 
      if (resultCodeAvp != null) {
        needToNotify = !peerStatistics.getResultCodeCountersMap().containsKey(Integer.valueOf((int)resultCodeAvp.getInteger()));
        if (needToNotify)
          synchronized (peerStatistics) {
            needToNotify = !peerStatistics.getResultCodeCountersMap().containsKey(Integer.valueOf((int)resultCodeAvp.getInteger()));
            if (needToNotify) {
              peerStatistics.getResultCodeTuple(packet.getCommandCode(), (int)resultCodeAvp.getInteger());
              setChanged();
              notifyObservers(new DiameterStatisticsEvents(applicationStatsId, hostIdentity, resultCodeAvp.getInteger()));
            } 
          }  
      } 
    } 
    return peerStatistics;
  }
  
  private ApplicationEnum getApplicationEnum(DiameterPacket packet) {
    final long applicationId = packet.getApplicationID();
    if (applicationId == 0L)
      return (ApplicationEnum)ApplicationIdentifier.BASE; 
    for (ApplicationEnum applicationEnum : this.supportedApplicationEnums) {
      if (applicationId == applicationEnum.getApplicationId())
        return applicationEnum; 
    } 
    ApplicationIdentifier applicationIdentifier = ApplicationIdentifier.fromApplicationIdentifiers(applicationId);
    if (applicationIdentifier != null)
      return (ApplicationEnum)applicationIdentifier; 
    long vendorId = ApplicationIdentifier.BASE.getVendorId();
    AvpGrouped vendorSpeceficAppId = (AvpGrouped)packet.getAVP("0:260");
    if (vendorSpeceficAppId != null) {
      IDiameterAVP vendorIdAvp = vendorSpeceficAppId.getSubAttribute(266);
      if (vendorIdAvp != null)
        vendorId = vendorIdAvp.getInteger(); 
    } 
    final long finalVendorId = vendorId;
    return new ApplicationEnum() {
        public long getVendorId() {
          return finalVendorId;
        }
        
        public ServiceTypes getApplicationType() {
          return ServiceTypes.BOTH;
        }
        
        public long getApplicationId() {
          return applicationId;
        }
        
        public Application getApplication() {
          return Application.UNKNOWN;
        }
        
        public String toString() {
          return 
            getVendorId() + ":" + 
            
            getApplicationId() + " [" + 
            getApplication().getDisplayName() + "]";
        }
      };
  }
  
  public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity) {
    this.stackStatistics.incrementUnknownH2HDropCount(answer);
    GroupedStatistics statistics = getPeerStatistic(hostIdentity);
    if (statistics != null)
      statistics.incrementUnknownH2HDropCount(answer); 
    ApplicationEnum appEnum = getApplicationEnum((DiameterPacket)answer);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    statistics = getApplicationStatistic(applicationStr);
    if (statistics != null)
      statistics.incrementUnknownH2HDropCount(answer); 
    statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, (DiameterPacket)answer);
    if (statistics != null)
      statistics.incrementUnknownH2HDropCount(answer); 
  }
  
  public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity, String realmName, RoutingActions routeAction) {
    this.stackStatistics.incrementUnknownH2HDropCount(answer);
    GroupedStatistics statistics = getPeerStatistic(hostIdentity);
    if (statistics != null)
      statistics.incrementUnknownH2HDropCount(answer); 
    statistics = getRealmStatistic(realmName, routeAction, (DiameterPacket)answer);
    if (statistics != null)
      statistics.incrementUnknownH2HDropCount(answer); 
    ApplicationEnum appEnum = getApplicationEnum((DiameterPacket)answer);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    statistics = getApplicationStatistic(applicationStr);
    if (statistics != null)
      statistics.incrementUnknownH2HDropCount(answer); 
    statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, (DiameterPacket)answer);
    if (statistics != null)
      statistics.incrementUnknownH2HDropCount(answer); 
  }
  
  public void updateMalformedPacketStatistics(DiameterPacket packet, String hostIdentity) {
    this.stackStatistics.incrementMalformedPacketCount(packet);
    GroupedStatistics statistics = getPeerStatistic(hostIdentity);
    if (statistics != null)
      statistics.incrementMalformedPacketCount(packet); 
    ApplicationEnum appEnum = getApplicationEnum(packet);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    statistics = getApplicationStatistic(applicationStr);
    if (statistics != null)
      statistics.incrementMalformedPacketCount(packet); 
    statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
    if (statistics != null)
      statistics.incrementMalformedPacketCount(packet); 
  }
  
  public void updateDuplicatePacketStatistics(DiameterPacket packet, String hostIdentity) {
    this.stackStatistics.incrementDuplicatePacketCount(packet);
    GroupedStatistics statistics = getPeerStatistic(hostIdentity);
    if (statistics != null)
      statistics.incrementDuplicatePacketCount(packet); 
    ApplicationEnum appEnum = getApplicationEnum(packet);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    statistics = getApplicationStatistic(applicationStr);
    if (statistics != null)
      statistics.incrementDuplicatePacketCount(packet); 
    statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
    if (statistics != null)
      statistics.incrementDuplicatePacketCount(packet); 
  }
  
  public void updateTimeoutRequestStatistics(DiameterRequest request, String hostIdentity) {
    this.stackStatistics.incrementTimeoutRequestCount(request);
    GroupedStatistics statistics = getPeerStatistic(hostIdentity);
    if (statistics != null)
      statistics.incrementTimeoutRequestCount(request); 
    ApplicationEnum appEnum = getApplicationEnum((DiameterPacket)request);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    statistics = getApplicationStatistic(applicationStr);
    if (statistics != null)
      statistics.incrementTimeoutRequestCount(request); 
    statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, (DiameterPacket)request);
    if (statistics != null)
      statistics.incrementTimeoutRequestCount(request); 
  }
  
  public void updateRealmTimeoutRequestStatistics(DiameterRequest request, String realmName, RoutingActions routingAction) {
    GroupedStatistics statistics = getRealmStatistic(realmName, routingAction, (DiameterPacket)request);
    if (statistics != null)
      statistics.incrementTimeoutRequestCount(request); 
  }
  
  public void updatePacketDroppedStatistics(DiameterPacket packet, String hostIdentity) {
    this.stackStatistics.incrementPacketDroppedCount(packet);
    GroupedStatistics statistics = getPeerStatistic(hostIdentity);
    if (statistics != null)
      statistics.incrementPacketDroppedCount(packet); 
    ApplicationEnum appEnum = getApplicationEnum(packet);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    statistics = getApplicationStatistic(applicationStr);
    if (statistics != null)
      statistics.incrementPacketDroppedCount(packet); 
    statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
    if (statistics != null)
      statistics.incrementPacketDroppedCount(packet); 
  }
  
  public void updatePacketDroppedStatistics(DiameterPacket packet, String hostIdentity, String realmName, RoutingActions routeAction) {
    this.stackStatistics.incrementPacketDroppedCount(packet);
    GroupedStatistics statistics = getPeerStatistic(hostIdentity);
    if (statistics != null)
      statistics.incrementPacketDroppedCount(packet); 
    statistics = getRealmStatistic(realmName, routeAction, packet);
    if (statistics != null)
      statistics.incrementPacketDroppedCount(packet); 
    ApplicationEnum appEnum = getApplicationEnum(packet);
    if (appEnum == null)
      return; 
    ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
    statistics = getApplicationStatistic(applicationStr);
    if (statistics != null)
      statistics.incrementPacketDroppedCount(packet); 
    statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
    if (statistics != null)
      statistics.incrementPacketDroppedCount(packet); 
  }
  
  public Set<String> getApplicationsSet() {
    Set<String> appKeys = new TreeSet<>();
    for (ApplicationStatsIdentifier appStatsIdentifier : this.applicationStatisticsMap.keySet())
      appKeys.add(appStatsIdentifier.getApplication()); 
    return appKeys;
  }
  
  public boolean reset() {
    resetStackStatistics();
    resetAllPeerStatistics();
    resetAllRealmStatistics();
    resetAllApplicationStatistics();
    return true;
  }
  
  public boolean resetStackStatistics() {
    this.stackStatistics = new GroupedStatistics();
    return true;
  }
  
  public boolean resetAllPeerStatistics() {
    for (String peerHostId : this.peerStatisticsMap.keySet())
      this.peerStatisticsMap.put(peerHostId, new GroupedStatistics()); 
    return true;
  }
  
  public boolean resetAllRealmStatistics() {
    for (RealmIdentifier realmIdentifier : this.realmStatisticsMap.keySet())
      this.realmStatisticsMap.put(realmIdentifier, new GroupedStatistics()); 
    return true;
  }
  
  public boolean resetApplicationStatistics(String applicationStr) {
    boolean found = false;
    for (ApplicationStatsIdentifier appStatsIdentifier : this.applicationStatisticsMap.keySet()) {
      if (appStatsIdentifier.getApplication().equalsIgnoreCase(applicationStr) || 
        String.valueOf(appStatsIdentifier.getApplicationId()).equals(applicationStr)) {
        this.applicationStatisticsMap.put(appStatsIdentifier, new GroupedStatistics());
        found = true;
      } 
    } 
    return found;
  }
  
  public boolean resetApplicationAllPeerStatistics(String applicationStr) {
    boolean found = false;
    for (Map.Entry<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> entry : this.appSpecificPeerStatisticsMap.entrySet()) {
      if ((entry.getKey()).getApplication().equalsIgnoreCase(applicationStr) || 
        String.valueOf((entry.getKey()).getApplicationId()).equals(applicationStr)) {
        for (String peerHostId : (entry.getValue()).keySet())
          (entry.getValue()).put(peerHostId, new GroupedStatistics()); 
        found = true;
      } 
    } 
    return found;
  }
  
  public boolean resetApplicationPeerStatistics(String applicationStr, String hostIdentity) {
    boolean found = false;
    for (Map.Entry<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> appEntry : this.appSpecificPeerStatisticsMap.entrySet()) {
      if (((appEntry.getKey()).getApplication().equalsIgnoreCase(applicationStr) || 
        String.valueOf((appEntry.getKey()).getApplicationId()).equals(applicationStr)) && (
        (Map)appEntry.getValue()).containsKey(hostIdentity)) {
        (appEntry.getValue()).put(hostIdentity, new GroupedStatistics());
        found = true;
      } 
    } 
    return found;
  }
  
  public boolean resetPeerStatistics(String hostIdentity) {
    boolean found = false;
    if (this.peerStatisticsMap.containsKey(hostIdentity)) {
      this.peerStatisticsMap.put(hostIdentity, new GroupedStatistics());
      found = true;
    } 
    return found;
  }
  
  public boolean resetRealmStatistics(String realmName) {
    boolean found = false;
    for (RealmIdentifier realmIdentifier : this.realmStatisticsMap.keySet()) {
      if (realmIdentifier.getDbpRealmMessageRouteRealm().equals(realmName)) {
        this.realmStatisticsMap.put(realmIdentifier, new GroupedStatistics());
        found = true;
      } 
    } 
    return found;
  }
  
  public boolean resetAllApplicationStatistics() {
    initApplicationMap();
    for (Map<String, GroupedStatistics> appEntry : this.appSpecificPeerStatisticsMap.values()) {
      for (String peerHostId : appEntry.keySet())
        appEntry.put(peerHostId, new GroupedStatistics()); 
    } 
    return true;
  }
  
  public GroupedStatistics getStackStatistics() {
    return this.stackStatistics;
  }
  
  public Map<String, GroupedStatistics> getPeerStatsMap() {
    return this.peerStatisticsMap;
  }
  
  public Map<RealmIdentifier, GroupedStatistics> getRealmStatsMap() {
    return this.realmStatisticsMap;
  }
  
  public Map<ApplicationStatsIdentifier, GroupedStatistics> getApplicationMap() {
    return this.applicationStatisticsMap;
  }
  
  public Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> getApplicationPeerMap() {
    return this.appSpecificPeerStatisticsMap;
  }
  
  private class MPSCalculator extends BaseIntervalBasedTask {
    public MPSCalculator() {
      DiameterStatistic.this.lastResetTimeInMilli = System.currentTimeMillis();
    }
    
    public long getInitialDelay() {
      return 60L;
    }
    
    public long getInterval() {
      return 60L;
    }
    
    public void execute(AsyncTaskContext context) {
      long totalRequestCount = DiameterStatistic.this.requestCounter.get();
      long totalResponseCount = DiameterStatistic.this.responseCounter.get();
      long tempTotalResponseTime = DiameterStatistic.this.totalResponseTimeInMillis.get();
      DiameterStatistic.this.requestCounter.set(0L);
      DiameterStatistic.this.responseCounter.set(0L);
      DiameterStatistic.this.totalResponseTimeInMillis.set(0L);
      long currentTimeInMilli = System.currentTimeMillis();
      long timeDiffInSec = (currentTimeInMilli - DiameterStatistic.this.getLastResetTimeInMilli()) / 1000L;
      DiameterStatistic.this.lastResetTimeInMilli = currentTimeInMilli;
      DiameterStatistic.this.appSpecificRTTMap.values().stream().flatMap(map -> map.values().stream()).forEach(RttStatistics::roll);
      DiameterStatistic.this.appSpecificMPSMap.values().stream().flatMap(map -> map.values().stream()).forEach(stat -> stat.roll(timeDiffInSec));
      DiameterStatistic.this.avgIncomingMPS = totalRequestCount / timeDiffInSec;
      DiameterStatistic.this.avgRoundTripTimeMS = (totalResponseCount > 0L) ? (tempTotalResponseTime / totalResponseCount) : 0L;
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
        LogManager.getLogger().debug("DIAM-STATS", "Total Request Count = " + totalRequestCount);
        LogManager.getLogger().debug("DIAM-STATS", "Total Response Count = " + totalResponseCount);
        LogManager.getLogger().debug("DIAM-STATS", "Total Response Time = " + tempTotalResponseTime + " ms");
      } 
      LogManager.getLogger().warn("DIAM-STATS", "Average incoming MPS = " + DiameterStatistic.this.avgIncomingMPS + ", Average Round Trip Time = " + DiameterStatistic.this.avgRoundTripTimeMS + " ms for last 1 minute");
    }
  }
  
  public long getAvgIncomingMPS() {
    return this.avgIncomingMPS;
  }
  
  public long getAvgRoundTripTime() {
    return this.avgRoundTripTimeMS;
  }
  
  public long getMessagePerMinute() {
    return this.requestCounter.get();
  }
  
  public Set<ApplicationEnum> getSupportedApplicationIdentifiers() {
    return this.supportedApplicationEnums;
  }
  
  public Long geTotalOutMessages() {
    return Long.valueOf(this.stackStatistics.getTotalRequestOutCount() + this.stackStatistics.getTotalAnswerOutCount());
  }
  
  public Long getTotalInMessages() {
    return Long.valueOf(this.stackStatistics.getTotalRequestInCount() + this.stackStatistics.getTotalAnswerInCount());
  }
  
  public long getLastResetTimeInMilli() {
    return this.lastResetTimeInMilli;
  }
  
  public Set<Map.Entry<ApplicationStatsIdentifier, Map<String, RttStatistics>>> getApplicationRttStatistics() {
    return this.appSpecificRTTMap.entrySet();
  }
  
  public Set<Map.Entry<ApplicationStatsIdentifier, Map<String, MpsStatistics>>> getApplicationMpsStatistics() {
    return this.appSpecificMPSMap.entrySet();
  }
}
