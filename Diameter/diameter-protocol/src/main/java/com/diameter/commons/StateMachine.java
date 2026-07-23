package com.diameter.commons;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class StateMachine implements IStateMachine, IAtomicActionsExecutor {
  private String MODULE = "STATE-MACHNE";
  
  protected IStateEnum currentState = (IStateEnum)StateEnum.NOT_INITIALIZED;
  
  private long elapsedTime;
  
  protected IStateMachineContext stateMachineContext;
  
  private final List<State> states;
  
  private final ReentrantLock stateLock;
  
  private final IStateMachineListener stateMachineListener;
  
  public StateMachine() {
    this((IStateEnum)StateEnum.UNKNOWN);
  }
  
  public StateMachine(IStateEnum state) {
    this.currentState = state;
    this.elapsedTime = System.currentTimeMillis();
    this.stateMachineContext = createStateMachineContext();
    this.states = createStates();
    this.stateLock = new ReentrantLock();
    this.stateMachineListener = getStateMachineListener();
    this.MODULE = getKey();
  }
  
  public final void onStateTransitionTrigger(IStateTransitionData stateTransitionData) throws UnhandledTransitionException {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", Current State : " + this.currentState); 
    try {
      if (this.currentState.isSync() || this.stateLock.isLocked()) {
        executeSyncState(stateTransitionData);
      } else {
        StateEvent stateEvent = createStateEvent(stateTransitionData);
        checkStateEvent(stateEvent);
        if (stateEvent.isSyncEvent() || this.stateLock.isLocked()) {
          executeSyncStateEvent(stateEvent);
        } else {
          processStateTransitionTrigger(stateEvent);
        } 
      } 
    } catch (UnhandledTransitionException e) {
      throw e;
    } catch (InvalidRoutingPacketException e) {
      throw e;
    } catch (Exception e) {
      throw new UnhandledTransitionException(e);
    } 
  }
  
  private void executeSyncState(IStateTransitionData stateTransitionData) throws UnhandledTransitionException {
    boolean bLocked = false;
    try {
      if (!this.stateLock.tryLock(1L, TimeUnit.SECONDS)) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn(this.MODULE, "Unable to acquire State Machine Lock for Current State: " + this.currentState + ", Attempting Again."); 
        if (!this.stateLock.tryLock(1L, TimeUnit.SECONDS)) {
          LogManager.getLogger().error(this.MODULE, "Unable to acquire State Machine Lock for Current State: " + this.currentState + ", Discarding event");
          return;
        } 
      } 
      bLocked = true;
      if (!this.currentState.isSync()) {
        StateEvent stateEvent = createStateEvent(stateTransitionData);
        checkStateEvent(stateEvent);
        if (!stateEvent.isSyncEvent()) {
          this.stateLock.unlock();
          bLocked = false;
          processStateTransitionTrigger(stateEvent);
        } else {
          processSyncStateTransitionTrigger(stateEvent);
          this.stateLock.unlock();
          bLocked = false;
        } 
      } else {
        StateEvent stateEvent = createStateEvent(stateTransitionData);
        checkStateEvent(stateEvent);
        processSyncStateTransitionTrigger(stateEvent);
        this.stateLock.unlock();
        bLocked = false;
      } 
    } catch (UnhandledTransitionException e) {
      throw e;
    } catch (Exception e) {
      throw new UnhandledTransitionException(e);
    } finally {
      if (bLocked)
        this.stateLock.unlock(); 
    } 
  }
  
  private void executeSyncStateEvent(StateEvent stateEvent) throws UnhandledTransitionException {
    boolean bLocked = false;
    try {
      if (!this.stateLock.tryLock(1L, TimeUnit.SECONDS)) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn(this.MODULE, "Unable to acquire State Machine Lock for Current State: " + this.currentState + ", Attempting Again."); 
        if (!this.stateLock.tryLock(1L, TimeUnit.SECONDS)) {
          LogManager.getLogger().error(this.MODULE, "Unable to acquire State Machine Lock for Current State: " + this.currentState + ", Discarding event");
          return;
        } 
      } 
      bLocked = true;
      if (this.currentState != stateEvent.getStateIdentifier()) {
        stateEvent = createStateEvent(stateEvent.getStateTransitionData());
        checkStateEvent(stateEvent);
        if (!stateEvent.isSyncEvent()) {
          this.stateLock.unlock();
          bLocked = false;
          processStateTransitionTrigger(stateEvent);
        } else {
          processSyncStateTransitionTrigger(stateEvent);
          this.stateLock.unlock();
          bLocked = false;
        } 
      } else {
        processSyncStateTransitionTrigger(stateEvent);
        this.stateLock.unlock();
        bLocked = false;
      } 
    } catch (UnhandledTransitionException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn(this.MODULE, "Peer: " + getPeerName() + ", " + e.getMessage()); 
      LogManager.getLogger().trace(this.MODULE, (Throwable)e);
      throw e;
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn(this.MODULE, "Peer: " + getPeerName() + ", " + e.getMessage()); 
      LogManager.getLogger().trace(this.MODULE, e);
      throw new UnhandledTransitionException(e);
    } finally {
      if (bLocked)
        this.stateLock.unlock(); 
    } 
  }
  
  private void processSyncStateTransitionTrigger(StateEvent stateEvent) {
    fetchCurrentState().processEvent(stateEvent);
    do {
      fetchCurrentState().exitAction(stateEvent);
      switchCurrentStateTo(stateEvent.getStateIdentifier(), stateEvent.getNextStateIdentifier());
      stateEvent = fetchCurrentState().entryAction(stateEvent);
    } while (stateEvent != null && stateEvent.isSyncEvent());
  }
  
  private void processStateTransitionTrigger(StateEvent stateEvent) {
    fetchCurrentState().processEvent(stateEvent);
  }
  
  private void notifyListener(IStateEnum oldState, IStateEnum newState) {
    if (this.stateMachineListener != null)
      this.stateMachineListener.stateSwitched(oldState, newState); 
  }
  
  public final void switchCurrentStateTo(IStateEnum oldState, IStateEnum newState) {
    if (oldState != newState)
      if (this.currentState == oldState || this.currentState != oldState) {
        try {
          if (this.currentState == oldState) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
              LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", " + this.currentState + " state is changed to " + newState + " state."); 
            this.elapsedTime = System.currentTimeMillis();
            this.currentState = newState;
            notifyListener(oldState, newState);
          } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", Cannot switch from " + oldState + " to " + newState + ", because current state is " + this.currentState);
          } 
        } catch (Exception e) {
          LogManager.getLogger().trace(this.MODULE, e);
        } 
      } else if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
        LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", Cannot switch from " + oldState + " to " + newState + ", because current state is " + this.currentState);
      }  
  }
  
  public final long getStateDuration() {
    return System.currentTimeMillis() - this.elapsedTime;
  }
  
  public int getCurrentState() {
    return this.currentState.stateOrdinal();
  }
  
  public State fetchCurrentState() {
    return this.states.get(this.currentState.stateOrdinal());
  }
  
  protected abstract StateEvent createStateEvent(IStateTransitionData paramIStateTransitionData);
  
  protected abstract String getPeerName();
  
  protected abstract List<State> createStates();
  
  public IStateEnum currentState() {
    return this.currentState;
  }
  
  private void checkStateEvent(StateEvent stateEvent) throws UnhandledTransitionException {
    if (stateEvent == null)
      throw new UnhandledTransitionException("Event can't be decided as per received transition data, peer: " + 
          getPeerName() + " remains in same state: " + this.currentState); 
    if (stateEvent.getStateIdentifier() == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", Invalid Event : " + stateEvent); 
      throw new UnhandledTransitionException("Invalid Event, peer remains in same state: " + this.currentState);
    } 
    if (stateEvent.getEventIdentifier() == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", Invalid Event : " + stateEvent); 
      throw new UnhandledTransitionException("Invalid Event, peer remains in same state: " + this.currentState);
    } 
    if (stateEvent.getNextStateIdentifier() == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", Invalid Event : " + stateEvent); 
      throw new UnhandledTransitionException("Invalid Event, peer remains in same state: " + this.currentState);
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug(this.MODULE, "Peer: " + getPeerName() + ", Raised Transition : " + stateEvent); 
  }
  
  protected String getKey() {
    return "STATE-MACHINE";
  }
  
  protected IStateMachineContext createStateMachineContext() {
    return new BaseStateMachineContext();
  }
  
  protected IStateMachineContext getStateMachineContext() {
    return this.stateMachineContext;
  }
  
  public boolean stop() {
    return true;
  }
  
  public IStateMachineListener getStateMachineListener() {
    return null;
  }
}
