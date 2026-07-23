package com.diameter.commons;

import java.util.Map;
import java.util.Set;

public interface DiameterStatisticsProvider {
  Set<String> getApplicationsSet();
  
  GroupedStatistics getStackStatistics();
  
  Map<String, GroupedStatistics> getPeerStatsMap();
  
  Map<RealmIdentifier, GroupedStatistics> getRealmStatsMap();
  
  Map<ApplicationStatsIdentifier, GroupedStatistics> getApplicationMap();
  
  Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> getApplicationPeerMap();
  
  long getAvgIncomingMPS();
  
  long getAvgRoundTripTime();
  
  long getMessagePerMinute();
  
  Set<ApplicationEnum> getSupportedApplicationIdentifiers();
  
  Long geTotalOutMessages();
  
  Long getTotalInMessages();
  
  long getLastResetTimeInMilli();
  
  Set<Map.Entry<ApplicationStatsIdentifier, Map<String, RttStatistics>>> getApplicationRttStatistics();
  
  Set<Map.Entry<ApplicationStatsIdentifier, Map<String, MpsStatistics>>> getApplicationMpsStatistics();
}