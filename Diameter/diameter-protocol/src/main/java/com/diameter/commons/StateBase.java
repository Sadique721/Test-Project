package com.diameter.commons;

public abstract class StateBase implements State {
  private static final String MODULE = "STATE-BASE";
  
  public StateEvent entryAction(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STATE-BASE", "Entry action of Base is called. with event::" + event); 
    return null;
  }
  
  public void exitAction(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STATE-BASE", "Exit action of Base is called. with event::" + event); 
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STATE-BASE", "GET STATE EVENT of Base is called with State Transition Data: " + stateTransitionData); 
    return null;
  }
}

