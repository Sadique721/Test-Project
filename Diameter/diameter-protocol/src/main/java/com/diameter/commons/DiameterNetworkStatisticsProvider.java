package com.diameter.commons;

public interface DiameterNetworkStatisticsProvider {
  ThreadPoolDetails getBaseThreadPoolDetails();
  
  ThreadPoolDetails getMessageThreadPoolDetails();
}
