package com.diameter.commons;

import java.util.concurrent.atomic.AtomicLong;

public class CounterTuple {
  private AtomicLong requestRx = new AtomicLong(0L);
  
  private AtomicLong requestTx = new AtomicLong(0L);
  
  private AtomicLong answerRx = new AtomicLong(0L);
  
  private AtomicLong answerTx = new AtomicLong(0L);
  
  private AtomicLong requestDr = new AtomicLong(0L);
  
  private AtomicLong answerDr = new AtomicLong(0L);
  
  private AtomicLong requestRetransmitted = new AtomicLong(0L);
  
  private AtomicLong unknownHbHAnswerDropped = new AtomicLong(0L);
  
  private AtomicLong duplicateRequest = new AtomicLong(0L);
  
  private AtomicLong duplicateEtEAnswer = new AtomicLong(0L);
  
  private AtomicLong malformedPacketRx = new AtomicLong(0L);
  
  private AtomicLong requestTimeOut = new AtomicLong(0L);
  
  public long getAnswerDroppedCount() {
    return this.answerDr.get();
  }
  
  public void incrementAnswerDroppedCount() {
    this.answerDr.incrementAndGet();
  }
  
  public long getRequestDroppedCount() {
    return this.requestDr.get();
  }
  
  public void incrementRequestDroppedCount() {
    this.requestDr.incrementAndGet();
  }
  
  public long getAnswerOutCount() {
    return this.answerTx.get();
  }
  
  public void incrementAnswerOutCount(DiameterPacket packet) {
    this.answerTx.incrementAndGet();
  }
  
  public long getAnswerInCount() {
    return this.answerRx.get();
  }
  
  public void incrementAnswerInCount(DiameterPacket packet) {
    this.answerRx.incrementAndGet();
  }
  
  public long getRequestOutCount() {
    return this.requestTx.get();
  }
  
  public void incrementRequestOutCount(DiameterPacket packet) {
    this.requestTx.incrementAndGet();
  }
  
  public long getRequestInCount() {
    return this.requestRx.get();
  }
  
  public void incrementRequestInCount(DiameterPacket packet) {
    this.requestRx.incrementAndGet();
  }
  
  public long getRequestsRetransmittedCount() {
    return this.requestRetransmitted.get();
  }
  
  public void incrementRequestsRetransmittedCount() {
    this.requestRetransmitted.incrementAndGet();
  }
  
  public long getUnknownHbHAnswerDroppedCount() {
    return this.unknownHbHAnswerDropped.get();
  }
  
  public void incrementUnknownHbHAnswerDroppedCount() {
    this.unknownHbHAnswerDropped.incrementAndGet();
  }
  
  public long getDuplicateEtEAnswerCount() {
    return this.duplicateEtEAnswer.get();
  }
  
  public void incrementDuplicateEtEAnswerCount() {
    this.duplicateEtEAnswer.incrementAndGet();
  }
  
  public long getDuplicateRequestCount() {
    return this.duplicateRequest.get();
  }
  
  public void incrementDuplicateRequestCount() {
    this.duplicateRequest.incrementAndGet();
  }
  
  public void incrementMalformedPacketReceivedCount() {
    this.malformedPacketRx.incrementAndGet();
  }
  
  public long getMalformedPacketReceivedCount() {
    return this.malformedPacketRx.get();
  }
  
  public void incrementTimeoutRequestStatistics() {
    this.requestTimeOut.incrementAndGet();
  }
  
  public long getTimeoutRequestStatistics() {
    return this.requestTimeOut.get();
  }
  
  public long getPendingRequestCount() {
    long pendingCount = getRequestOutCount() - getAnswerInCount() - getRequestsRetransmittedCount();
    if (pendingCount < 0L)
      pendingCount = 0L; 
    return pendingCount;
  }
}
