package com.diameter.commons;

public interface ESCommunicatorGroup<T extends ESCommunicator> {
  public static final int SECONDARY_COMM_WEIGHTAGE = 0;
  
  void addCommunicator(T paramT, int paramInt);
  
  void addCommunicator(T paramT);
  
  boolean isAlive();
}
