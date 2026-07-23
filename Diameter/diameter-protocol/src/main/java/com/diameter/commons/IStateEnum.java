package com.diameter.commons;

public interface IStateEnum {
  IStateEnum getNextState(IEventEnum paramIEventEnum);
  
  int stateOrdinal();
  
  boolean isSync();
  
  String name();
}
