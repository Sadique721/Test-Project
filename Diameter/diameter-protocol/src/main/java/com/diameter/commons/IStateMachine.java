package com.diameter.commons;

public interface IStateMachine {
  void switchCurrentStateTo(IStateEnum paramIStateEnum1, IStateEnum paramIStateEnum2);
  
  void onStateTransitionTrigger(IStateTransitionData paramIStateTransitionData) throws UnhandledTransitionException;
  
  State fetchCurrentState();
  
  IStateEnum currentState();
  
  boolean stop();
  
  int getCurrentState();
  
  long getStateDuration();
}
