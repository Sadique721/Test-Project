package com.diameter.commons;

public class StateEvent {
  private IEventEnum event;
  
  private IStateEnum state;
  
  private IStateEnum nextState;
  
  private IStateEnum nextStateEntryEvent;
  
  private IStateTransitionData stateTransitionData;
  
  public StateEvent(IStateEnum state, IEventEnum event) {
    this(state, event, state.getNextState(event), null);
  }
  
  public StateEvent(IStateEnum state, IEventEnum event, IStateEnum nextState) {
    this(state, event, nextState, null);
  }
  
  public StateEvent(IStateEnum state, IEventEnum event, IStateTransitionData stateTransitionData) {
    this(state, event, state.getNextState(event), stateTransitionData);
  }
  
  public StateEvent(IStateEnum state, IEventEnum event, IStateEnum nextState, IStateTransitionData stateTransitionData) {
    this.state = state;
    this.event = event;
    this.nextState = nextState;
    this.stateTransitionData = stateTransitionData;
  }
  
  public IEventEnum getEventIdentifier() {
    return this.event;
  }
  
  public IStateEnum getStateIdentifier() {
    return this.state;
  }
  
  public IStateEnum getNextStateIdentifier() {
    return this.nextState;
  }
  
  public IStateTransitionData getStateTransitionData() {
    return this.stateTransitionData;
  }
  
  public void setNextStateEntryEvent(IStateEnum nextStateEntryEvent) {
    this.nextStateEntryEvent = nextStateEntryEvent;
  }
  
  public IStateEnum getNextStateEntryEvent() {
    return this.nextStateEntryEvent;
  }
  
  public boolean isSyncEvent() {
    if (this.state != this.nextState)
      return true; 
    return false;
  }
  
  public String toString() {
    return "State: " + this.state + "/Event: " + this.event + "/Next State: " + this.nextState;
  }
}
