package com.diameter.commons;

public enum PCBStates implements IStateEnum {
  OKAY(false),
  SUSPECT(false),
  DOWN(false),
  REOPEN(false),
  INITIAL(false);
  
  public final boolean sync;
  
  PCBStates(boolean sync) {
    this.sync = sync;
  }
  
  public boolean isSync() {
    return this.sync;
  }
  
  public IStateEnum getNextState(IEventEnum event) {
    return null;
  }
  
  public int stateOrdinal() {
    return ordinal();
  }
}
