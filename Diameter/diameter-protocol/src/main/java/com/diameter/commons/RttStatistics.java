package com.diameter.commons;

import java.util.concurrent.atomic.LongAdder;

public class RttStatistics {
  private long averageRtt;
  
  private LongAdder totalRequestAnswers = new LongAdder();
  
  private LongAdder totalResponseTime = new LongAdder();
  
  public void add(DiameterAnswer packet, long currentTime) {
    this.totalRequestAnswers.increment();
    this.totalResponseTime.add(currentTime - packet.getRequestReceivedTime());
  }
  
  public void roll() {
    long totalResponseCount = this.totalRequestAnswers.sumThenReset();
    long tempTotalResponseTime = this.totalResponseTime.sumThenReset();
    this.averageRtt = (totalResponseCount > 0L) ? (tempTotalResponseTime / totalResponseCount) : 0L;
  }
  
  public long getAverageRtt() {
    return this.averageRtt;
  }
}
