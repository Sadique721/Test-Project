package com.diameter.commons;

public interface PCBActionExecutor extends IAtomicActionsExecutor {
  void setWatchdog();
  
  void onReceive(IStateTransitionData paramIStateTransitionData);
  
  void onTimerElapsed();
  
  void onConnectionUp();
  
  void onConnectionDown();
  
  void throwaway();
  
  void sendWatchdog();
  
  void attemptOpen();
  
  void sendDPR();
  
  void closeConnection(ConnectionEvents paramConnectionEvents);
  
  void failover();
  
  void failback();
  
  void setPending(boolean paramBoolean);
  
  int getNumDwa();
  
  void setNumDwa(int paramInt);
  
  void incrementNumDwa();
}
