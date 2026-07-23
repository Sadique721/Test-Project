package com.diameter.commons;

import java.util.concurrent.atomic.LongAdder;

public class MpsStatistics {
  private LongAdder totalRequest = new LongAdder();
  
  private long mps;
  
  public void add() {
    this.totalRequest.increment();
  }
  
  public void roll(long timeInDiff) {
    long totalRequestCount = this.totalRequest.sumThenReset();
    this.mps = totalRequestCount / timeInDiff;
  }
  
  public long getMps() {
    return this.mps;
  }
}
