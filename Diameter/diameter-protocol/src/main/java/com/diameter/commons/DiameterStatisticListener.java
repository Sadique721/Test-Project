package com.diameter.commons;

import java.util.Collection;
import java.util.Observer;
import java.util.Set;

public class DiameterStatisticListener {
  private DiameterConfiguration diameterConfiguration;
  
  private DiameterPeersTable diameterPeersTable;
  
  private DiameterStatistic diameterStatistic;
  
  public DiameterStatisticListener(MIBIndexRecorder mibIndexRecorder, DiameterPeersTable peersTable, Set<ApplicationEnum> supportedApplicationIdentifiers, TaskScheduler taskScheduler) {
    this.diameterPeersTable = peersTable;
    this.diameterConfiguration = new DiameterConfiguration(mibIndexRecorder);
    this.diameterStatistic = new DiameterStatistic(supportedApplicationIdentifiers, taskScheduler);
  }
  
  public void init() {
    this.diameterStatistic.init(this.diameterPeersTable.getPeerList());
    this.diameterConfiguration.init(this.diameterPeersTable.getPeerList());
  }
  
  public DiameterConfigProvider getDiameterConfigProvider() {
    return (DiameterConfigProvider)this.diameterConfiguration;
  }
  
  public DiameterStatisticsProvider getDiameterStatisticProvider() {
    return (DiameterStatisticsProvider)this.diameterStatistic;
  }
  
  public void addDiameterPeer(DiameterPeer peer) {
    this.diameterConfiguration.addDiameterPeer(peer);
  }
  
  public void updateInputStatistics(DiameterPacket packet, String hostIdentity) {
    this.diameterStatistic.updateInputStatistics(packet, hostIdentity);
  }
  
  public void updateOutputStatistics(DiameterPacket packet, String hostIdentity) {
    this.diameterStatistic.updateOutputStatistics(packet, hostIdentity);
  }
  
  public void updateRealmInputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
    this.diameterStatistic.updateRealmInputStatistics(realmName, routeAction, packet);
  }
  
  public void updateRealmOutputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
    this.diameterStatistic.updateRealmOutputStatistics(realmName, routeAction, packet);
  }
  
  public void updateTimeoutRequestStatistics(DiameterRequest request, String hostIdentity) {
    this.diameterStatistic.updateTimeoutRequestStatistics(request, hostIdentity);
  }
  
  public void updateRealmTimeoutRequestStatistics(DiameterRequest request, String realmName, RoutingActions routingAction) {
    this.diameterStatistic.updateRealmTimeoutRequestStatistics(request, realmName, routingAction);
  }
  
  public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity) {
    this.diameterStatistic.updateUnknownH2HDropStatistics(answer, hostIdentity);
  }
  
  public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity, String realmName, RoutingActions routeAction) {
    this.diameterStatistic.updateUnknownH2HDropStatistics(answer, hostIdentity, realmName, routeAction);
  }
  
  public void updateDuplicatePacketStatistics(DiameterPacket packet, String hostIdentity) {
    this.diameterStatistic.updateDuplicatePacketStatistics(packet, hostIdentity);
  }
  
  public void updateMalformedPacketCount(DiameterPacket packet, String hostIdentity) {
    this.diameterStatistic.updateMalformedPacketStatistics(packet, hostIdentity);
  }
  
  public void updatePacketDroppedStatistics(DiameterPacket packet, String hostIdentity) {
    this.diameterStatistic.updatePacketDroppedStatistics(packet, hostIdentity);
  }
  
  public void updatePacketDroppedStatistics(DiameterPacket packet, String hostIdentity, String realmName, RoutingActions routeAction) {
    this.diameterStatistic.updatePacketDroppedStatistics(packet, hostIdentity, realmName, routeAction);
  }
  
  public void addStatisticObserver(Observer statisticObserver) {
    this.diameterStatistic.addObserver(statisticObserver);
  }
  
  public void addConfigurationObserver(Observer configurationObserver) {
    this.diameterConfiguration.addObserver(configurationObserver);
  }
  
  public void reload(Collection<DiameterPeer> peerList) {
    this.diameterConfiguration.reload(peerList);
  }
  
  public DiameterStatisticResetter getDiameterStatisticResetter() {
    return (DiameterStatisticResetter)this.diameterStatistic;
  }
}
