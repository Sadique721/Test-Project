package com.diameter.commons;

import java.util.concurrent.TimeUnit;

public interface CallableSingleExecutionAsyncTask<T> {
  long getInitialDelay();
  
  TimeUnit getTimeUnit();
  
  T execute(AsyncTaskContext paramAsyncTaskContext);
}
