package com.diameter.commons;

import java.util.concurrent.atomic.AtomicLong;

public class ResultCodeTuple {
  private AtomicLong inResultCode = new AtomicLong(0L);
  
  private AtomicLong outResultCode = new AtomicLong(0L);
  
  public long getResultCodeIn() {
    return this.inResultCode.get();
  }
  
  public void incrementResultCodeIn(DiameterPacket answer) {
    this.inResultCode.incrementAndGet();
  }
  
  public long getResultCodeOut() {
    return this.outResultCode.get();
  }
  
  public void incrementResultCodeOut(DiameterPacket answer) {
    this.outResultCode.incrementAndGet();
  }
}