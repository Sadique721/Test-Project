package com.diameter.commons;

public interface LoadBalancer<T extends ESCommunicator> {
  public static final int DEFAULT_WEIGHT = 1;
  
  public static final int SECONDARY_WEIGHT = 0;
  
  T getCommunicator();
  
  void addCommunicator(T paramT, int paramInt);
  
  T getSecondaryCommunicator(T... paramVarArgs);
  
  boolean isAlive();
}