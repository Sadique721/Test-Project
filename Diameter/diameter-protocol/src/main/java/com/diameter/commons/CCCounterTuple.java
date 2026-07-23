package com.diameter.commons;

import java.util.concurrent.atomic.AtomicLong;

public class CCCounterTuple extends CounterTuple {
  private AtomicLong initialRequestRx = new AtomicLong(0L);
  
  private AtomicLong initialRequestTx = new AtomicLong(0L);
  
  private AtomicLong initialAnswerRx = new AtomicLong(0L);
  
  private AtomicLong initialAnswerTx = new AtomicLong(0L);
  
  private AtomicLong updateRequestRx = new AtomicLong(0L);
  
  private AtomicLong updateRequestTx = new AtomicLong(0L);
  
  private AtomicLong updateAnswerRx = new AtomicLong(0L);
  
  private AtomicLong updateAnswerTx = new AtomicLong(0L);
  
  private AtomicLong terminateRequestRx = new AtomicLong(0L);
  
  private AtomicLong terminateRequestTx = new AtomicLong(0L);
  
  private AtomicLong terminateAnswerRx = new AtomicLong(0L);
  
  private AtomicLong terminateAnswerTx = new AtomicLong(0L);
  
  private AtomicLong otherRequestRx = new AtomicLong(0L);
  
  private AtomicLong otherRequestTx = new AtomicLong(0L);
  
  private AtomicLong otherAnswerRx = new AtomicLong(0L);
  
  private AtomicLong otherAnswerTx = new AtomicLong(0L);
  
  public void incrementAnswerInCount(DiameterPacket packet) {
    super.incrementAnswerInCount(packet);
    IDiameterAVP requestType = packet.getAVP("0:416");
    if (requestType == null) {
      this.otherAnswerRx.incrementAndGet();
      return;
    } 
    int lRequestType = (int)requestType.getInteger();
    switch (lRequestType) {
      case 2:
        this.updateAnswerRx.incrementAndGet();
        return;
      case 1:
        this.initialAnswerRx.incrementAndGet();
        return;
      case 3:
        this.terminateAnswerRx.incrementAndGet();
        return;
    } 
    this.otherAnswerRx.incrementAndGet();
  }
  
  public void incrementAnswerOutCount(DiameterPacket packet) {
    super.incrementAnswerOutCount(packet);
    IDiameterAVP requestType = packet.getAVP("0:416");
    if (requestType == null) {
      this.otherAnswerTx.incrementAndGet();
      return;
    } 
    int lRequestType = (int)requestType.getInteger();
    switch (lRequestType) {
      case 2:
        this.updateAnswerTx.incrementAndGet();
        return;
      case 1:
        this.initialAnswerTx.incrementAndGet();
        return;
      case 3:
        this.terminateAnswerTx.incrementAndGet();
        return;
    } 
    this.otherAnswerTx.incrementAndGet();
  }
  
  public void incrementRequestInCount(DiameterPacket packet) {
    super.incrementRequestInCount(packet);
    IDiameterAVP requestType = packet.getAVP("0:416");
    if (requestType == null) {
      this.otherRequestRx.incrementAndGet();
      return;
    } 
    int lRequestType = (int)requestType.getInteger();
    switch (lRequestType) {
      case 2:
        this.updateRequestRx.incrementAndGet();
        return;
      case 1:
        this.initialRequestRx.incrementAndGet();
        return;
      case 3:
        this.terminateRequestRx.incrementAndGet();
        return;
    } 
    this.otherRequestRx.incrementAndGet();
  }
  
  public void incrementRequestOutCount(DiameterPacket packet) {
    super.incrementRequestOutCount(packet);
    IDiameterAVP requestType = packet.getAVP("0:416");
    if (requestType == null) {
      this.otherRequestTx.incrementAndGet();
      return;
    } 
    int lRequestType = (int)requestType.getInteger();
    switch (lRequestType) {
      case 2:
        this.updateRequestTx.incrementAndGet();
        return;
      case 1:
        this.initialRequestTx.incrementAndGet();
        return;
      case 3:
        this.terminateRequestTx.incrementAndGet();
        return;
    } 
    this.otherRequestTx.incrementAndGet();
  }
  
  public long getInitialRequestRx() {
    return this.initialRequestRx.get();
  }
  
  public long getInitialRequestTx() {
    return this.initialRequestTx.get();
  }
  
  public long getInitialAnswerRx() {
    return this.initialAnswerRx.get();
  }
  
  public long getInitialAnswerTx() {
    return this.initialAnswerTx.get();
  }
  
  public long getUpdateRequestRx() {
    return this.updateRequestRx.get();
  }
  
  public long getUpdateRequestTx() {
    return this.updateRequestTx.get();
  }
  
  public long getUpdateAnswerRx() {
    return this.updateAnswerRx.get();
  }
  
  public long getUpdateAnswerTx() {
    return this.updateAnswerTx.get();
  }
  
  public long getTerminateRequestRx() {
    return this.terminateRequestRx.get();
  }
  
  public long getTerminateRequestTx() {
    return this.terminateRequestTx.get();
  }
  
  public long getTerminateAnswerRx() {
    return this.terminateAnswerRx.get();
  }
  
  public long getTerminateAnswerTx() {
    return this.terminateAnswerTx.get();
  }
  
  public long getOtherRequestRx() {
    return this.otherRequestRx.get();
  }
  
  public long getOtherRequestTx() {
    return this.otherRequestTx.get();
  }
  
  public long getOtherAnswerRx() {
    return this.otherAnswerRx.get();
  }
  
  public long getOtherAnswerTx() {
    return this.otherAnswerTx.get();
  }
}
