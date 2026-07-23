package com.diameter.commons;

public enum Status {
  NOT_INITIALIZE(0),
  INITIALIZING(1),
  INITIALIZED(2),
  RUNNING(3),
  STOPPING(4),
  STOPPED(5);
  
  public final int status;
  
  Status(int status) {
    this.status = status;
  }
}
