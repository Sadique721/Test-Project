package com.diameter.commons;

public enum PCBEvents implements IEventEnum {
  ReceiveNonDWA(true),
  ReceiveDWA(true),
  ReceiveDWAAndNumEqualsTwo(true),
  ReceiveDWAAndNumLessThanTwo(true),
  TimerExpires(true),
  TimerExpiresAndPending(true),
  TimerExpiresAndNotPending(true),
  TimerExpiresAndPendingAndDWALessThanZero(true),
  TimerExpiresAndPendingAndDWANotLessThanZero(true),
  ConnectionUp(true),
  ConnectionDown(true);
  
  private final boolean sync;
  
  PCBEvents(boolean sync) {
    this.sync = sync;
  }
  
  public int eventOrdinal() {
    return ordinal();
  }
  
  public boolean isSync() {
    return this.sync;
  }
}
