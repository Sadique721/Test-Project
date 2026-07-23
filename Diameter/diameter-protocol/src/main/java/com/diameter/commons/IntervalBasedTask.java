package com.diameter.commons;

import java.util.concurrent.TimeUnit;

public interface IntervalBasedTask {
  long getInitialDelay();
  
  long getInterval();
  
  boolean isFixedDelay();
  
  TimeUnit getTimeUnit();
  
  void preExecute(AsyncTaskContext paramAsyncTaskContext);
  
  void execute(AsyncTaskContext paramAsyncTaskContext);
  
  void postExecute(AsyncTaskContext paramAsyncTaskContext);
}
