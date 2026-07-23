package com.diameter.commons;

public interface ESCommunicator extends ReInitializable {
  public static final int NO_SCANNER_THREAD = 0;
  
  public static final int ALWAYS_ALIVE = -1;
  
  void init() throws InitializationFailedException;
  
  boolean isAlive();
  
  void scan();
  
  void addESIEventListener(ESIEventListener<ESCommunicator> paramESIEventListener);
  
  void removeESIEventListener(ESIEventListener<ESCommunicator> paramESIEventListener);
  
  void stop();
  
  String getName();
  
  String getTypeName();
  
  ESIStatistics getStatistics();
  
  void registerAlertListener(AlertListener paramAlertListener);
}
