package com.diameter.commons;

public interface IStateTransitionData {
  Object getData(IStateTransitionDataCode paramIStateTransitionDataCode);
  
  void addObject(IStateTransitionDataCode paramIStateTransitionDataCode, Object paramObject);
}
