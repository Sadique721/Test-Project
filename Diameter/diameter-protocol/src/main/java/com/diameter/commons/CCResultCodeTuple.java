package com.diameter.commons;

import java.util.concurrent.atomic.AtomicLong;

public class CCResultCodeTuple extends ResultCodeTuple {
  private AtomicLong initialResultCodeIn = new AtomicLong(0L);
  
  private AtomicLong updateResultCodeIn = new AtomicLong(0L);
  
  private AtomicLong terminateResultCodeIn = new AtomicLong(0L);
  
  private AtomicLong otherResultCodeIn = new AtomicLong(0L);
  
  private AtomicLong initialResultCodeOut = new AtomicLong(0L);
  
  private AtomicLong updateResultCodeOut = new AtomicLong(0L);
  
  private AtomicLong terminateResultCodeOut = new AtomicLong(0L);
  
  private AtomicLong otherResultCodeOut = new AtomicLong(0L);
  
  public void incrementResultCodeIn(DiameterPacket answer) {
    super.incrementResultCodeIn(answer);
    IDiameterAVP requestType = answer.getAVP("0:416");
    if (requestType == null) {
      this.otherResultCodeIn.incrementAndGet();
      return;
    } 
    int lRequestType = (int)requestType.getInteger();
    switch (lRequestType) {
      case 2:
        this.updateResultCodeIn.incrementAndGet();
        return;
      case 1:
        this.initialResultCodeIn.incrementAndGet();
        return;
      case 3:
        this.terminateResultCodeIn.incrementAndGet();
        return;
    } 
    this.otherResultCodeIn.incrementAndGet();
  }
  
  public void incrementResultCodeOut(DiameterPacket answer) {
    super.incrementResultCodeOut(answer);
    IDiameterAVP requestType = answer.getAVP("0:416");
    if (requestType == null) {
      this.otherResultCodeOut.incrementAndGet();
      return;
    } 
    int lRequestType = (int)requestType.getInteger();
    switch (lRequestType) {
      case 2:
        this.updateResultCodeOut.incrementAndGet();
        return;
      case 1:
        this.initialResultCodeOut.incrementAndGet();
        return;
      case 3:
        this.terminateResultCodeOut.incrementAndGet();
        return;
    } 
    this.otherResultCodeOut.incrementAndGet();
  }
  
  public long getInitialResultCodeIn() {
    return this.initialResultCodeIn.get();
  }
  
  public long getUpdateResultCodeIn() {
    return this.updateResultCodeIn.get();
  }
  
  public long getTerminateResultCodeIn() {
    return this.terminateResultCodeIn.get();
  }
  
  public long getOtherResultCodeIn() {
    return this.otherResultCodeIn.get();
  }
  
  public long getInitialResultCodeOut() {
    return this.initialResultCodeOut.get();
  }
  
  public long getUpdateResultCodeOut() {
    return this.updateResultCodeOut.get();
  }
  
  public long getTerminateResultCodeOut() {
    return this.terminateResultCodeOut.get();
  }
  
  public long getOtherResultCodeOut() {
    return this.otherResultCodeOut.get();
  }
}
