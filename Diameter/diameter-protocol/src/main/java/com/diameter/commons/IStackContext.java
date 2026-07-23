package com.diameter.commons;

import java.util.concurrent.ScheduledFuture;

public interface IStackContext {
  INetworkConnector getNetworkConnector(TransportProtocols paramTransportProtocols);
  
  ScheduledFuture<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask paramSingleExecutionAsyncTask);
  
  <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> paramCallableSingleExecutionAsyncTask);
  
  ScheduledFuture<?> scheduleIntervalBasedTask(IntervalBasedTask paramIntervalBasedTask);
  
  void scheduleSingleExecutionTask(Runnable paramRunnable);
  
  void purgeCancelledTasks();
  
  void finalPreResponseProcess(DiameterPacket paramDiameterPacket);
  
  PeerData getPeerData(String paramString);
  
  long getNextPeerSequence(String paramString);
  
  long getNextServerSequence();
  
  String getNextSessionID();
  
  String getNextSessionID(String paramString);
  
  boolean hasSession(String paramString, long paramLong);
  
  ISession readOnlySession(String paramString, long paramLong);
  
  Session getOrCreateSession(String paramString, long paramLong);
  
  Session generateSession(long paramLong);
  
  Session generateSession(String paramString, long paramLong);
  
  EliteSSLContextFactory getEliteSSLContextFactory();
  
  void updateInputStatistics(DiameterPacket paramDiameterPacket, String paramString);
  
  void updateOutputStatistics(DiameterPacket paramDiameterPacket, String paramString);
  
  void updateTimeoutRequestStatistics(DiameterRequest paramDiameterRequest, String paramString);
  
  void updateUnknownH2HDropStatistics(DiameterAnswer paramDiameterAnswer, String paramString);
  
  void updateUnknownH2HDropStatistics(DiameterAnswer paramDiameterAnswer, String paramString1, String paramString2, RoutingActions paramRoutingActions);
  
  void updateDiameterStatsMalformedPacket(DiameterPacket paramDiameterPacket, String paramString);
  
  void updateDiameterStatsPacketDroppedStatistics(DiameterPacket paramDiameterPacket, String paramString);
  
  void updateRealmTimeoutRequestStatistics(DiameterRequest paramDiameterRequest, String paramString, RoutingActions paramRoutingActions);
  
  void updateDiameterStatsPacketDroppedStatistics(DiameterPacket paramDiameterPacket, String paramString1, String paramString2, RoutingActions paramRoutingActions);
  
  void updateRealmInputStatistics(DiameterPacket paramDiameterPacket, String paramString, RoutingActions paramRoutingActions);
  
  void updateRealmOutputStatistics(DiameterPacket paramDiameterPacket, String paramString, RoutingActions paramRoutingActions);
  
  void updateDuplicatePacketStatistics(DiameterPacket paramDiameterPacket, String paramString);
  
  int getOverloadResultCode();
  
  OverloadAction getActionOnOverload();
  
  TaskScheduler getTaskScheduler();
  
  DiameterPeerCommunicator getPeerCommunicator(String paramString);
}
