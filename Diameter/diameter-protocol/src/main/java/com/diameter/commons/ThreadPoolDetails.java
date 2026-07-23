package com.diameter.commons;

public interface ThreadPoolDetails {
  int getMinSize();
  
  int getMaxSize();
  
  int getActiveCount();
  
  int getPoolSize();
  
  int getPeakPoolSize();
  
  int getQueueSize();
}
