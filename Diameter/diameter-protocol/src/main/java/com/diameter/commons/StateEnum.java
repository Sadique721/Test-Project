package com.diameter.commons;

public enum StateEnum implements IStateEnum {
  NOT_INITIALIZED(false),
  UNKNOWN(false);
  
  public final boolean sync;
  
  StateEnum(boolean sync) {
    this.sync = sync;
  }
  
  public boolean isSync() {
    return this.sync;
  }
  
  public int stateOrdinal() {
    return ordinal();
  }
  
  public IStateEnum getNextState(IEventEnum event) {
    return null;
  }
}
