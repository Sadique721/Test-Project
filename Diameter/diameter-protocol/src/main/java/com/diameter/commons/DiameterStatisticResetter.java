package com.diameter.commons;

public interface DiameterStatisticResetter {
  boolean reset();
  
  boolean resetStackStatistics();
  
  boolean resetAllPeerStatistics();
  
  boolean resetAllRealmStatistics();
  
  boolean resetApplicationStatistics(String paramString);
  
  boolean resetApplicationAllPeerStatistics(String paramString);
  
  boolean resetApplicationPeerStatistics(String paramString1, String paramString2);
  
  boolean resetPeerStatistics(String paramString);
  
  boolean resetRealmStatistics(String paramString);
  
  boolean resetAllApplicationStatistics();
}
