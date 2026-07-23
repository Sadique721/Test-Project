package com.diameter.commons;

public abstract class TimeSource {
  private static final TimeSource SYSTEM_TIME_SOURCE = new TimeSource() {
      public long currentTimeInMillis() {
        return System.currentTimeMillis();
      }
    };
  
  public abstract long currentTimeInMillis();
  
  public static TimeSource systemTimeSource() {
    return SYSTEM_TIME_SOURCE;
  }
}
