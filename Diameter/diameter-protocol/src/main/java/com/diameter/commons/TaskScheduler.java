package com.diameter.commons;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

public interface TaskScheduler extends Executor {
  @Nullable
  Future<?> scheduleSingleExecutionTask(@Nullable SingleExecutionAsyncTask paramSingleExecutionAsyncTask);
  
  @Nullable
  Future<?> scheduleIntervalBasedTask(@Nullable IntervalBasedTask paramIntervalBasedTask);
}