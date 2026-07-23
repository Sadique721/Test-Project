package com.diameter.commons;

import java.util.concurrent.TimeUnit;

public abstract class BaseSingleExecutionAsyncTask implements SingleExecutionAsyncTask {
  public long getInitialDelay() {
    return 0L;
  }
  
  public TimeUnit getTimeUnit() {
    return TimeUnit.SECONDS;
  }
}
