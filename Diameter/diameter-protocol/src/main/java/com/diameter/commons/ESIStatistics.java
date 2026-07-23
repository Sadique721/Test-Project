package com.diameter.commons;

public interface ESIStatistics {
  public static final String ALIVE = "ALIVE";
  
  public static final String DEAD = "DEAD";
  
  String getName();
  
  String getTypeName();
  
  String currentStatus();
  
  float getLastMinAvgResponseTime();
  
  float getLastTenMinAvgResponseTime();
  
  float getLastHourAvgResponseTime();
  
  long getLastScanTimestamp();
  
  long getLastDeadTimestamp();
  
  long getTotalTimedouts();
  
  long getTotalRequests();
  
  long getTotalSuccesses();
  
  long getTotalErrors();
  
  long getDeadCount();
  
  long getLastMarkDeadTimestamp();
}
