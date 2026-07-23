package com.diameter.commons;

import java.util.concurrent.TimeUnit;

public abstract class BaseIntervalBasedTask implements IntervalBasedTask {
  public long getInitialDelay() {
    return 1L;
  }
  
  public boolean isFixedDelay() {
    return false;
  }
  
  public TimeUnit getTimeUnit() {
    return TimeUnit.SECONDS;
  }
  
  public void preExecute(AsyncTaskContext context) {}
  
  public void postExecute(AsyncTaskContext context) {}
}
