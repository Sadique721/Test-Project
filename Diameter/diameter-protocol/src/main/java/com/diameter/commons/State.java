package com.diameter.commons;


public interface State {
  StateEvent entryAction(StateEvent paramStateEvent);
  
  void exitAction(StateEvent paramStateEvent);
  
  boolean processEvent(StateEvent paramStateEvent);
  
  StateEvent getStateEvent(IStateTransitionData paramIStateTransitionData);
}

