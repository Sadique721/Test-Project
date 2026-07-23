package com.diameter.commons;

import java.util.concurrent.TimeUnit;

public interface SingleExecutionAsyncTask {
  long getInitialDelay();
  
  TimeUnit getTimeUnit();
  
  void execute(AsyncTaskContext paramAsyncTaskContext);
}
