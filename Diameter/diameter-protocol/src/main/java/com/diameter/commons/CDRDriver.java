package com.diameter.commons;

public interface CDRDriver<T> extends ESCommunicator {
  void init() throws DriverInitializationFailedException;
  
  void handleRequest(T paramT) throws DriverProcessFailedException;
  
  String getDriverInstanceUUID();
  
  default String getDriverInstanceId() {
    return getDriverInstanceUUID();
  }
  
  int getDriverType();
  
  String getDriverName();
}
