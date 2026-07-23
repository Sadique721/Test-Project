package com.diameter.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public abstract class PCBStateMachine extends StateMachine implements PCBActionExecutor {
  private static final String MODULE = "PCB-ST-MCHN";
  
  private AtomicBoolean pending;
  
  private AtomicInteger numDwa;
  
  private long watchdogTimer;
  
  private long watchdogDuration;
  
  private IStackContext stackContext;
  
  private ScheduledFuture<?> connectionInitiatorTask;
  
  private ScheduledFuture<?> watchDogTask;
  
  private long initiateConnectionDuration;
  
  private final ReentrantLock timerExpireStateLock;
  
  private final Object watchDogLock = new Object();
  
  private final Object initConnectionLock = new Object();
  
  public PCBStateMachine(long watchDogTimerMs, int initiateConnectionDuration, IStackContext stackContext) {
    this.stackContext = stackContext;
    this.currentState = (IStateEnum)PCBStates.INITIAL;
    this.numDwa = new AtomicInteger(0);
    this.pending = new AtomicBoolean(false);
    this.watchdogDuration = watchDogTimerMs;
    this.initiateConnectionDuration = initiateConnectionDuration;
    this.timerExpireStateLock = new ReentrantLock();
  }
  
  public void start() {
    scheduleConnectionInitiatorTask();
  }
  
  protected StateEvent createStateEvent(IStateTransitionData transitionData) {
    if (this.currentState == PCBStates.OKAY)
      return getOkayStateEvent(transitionData); 
    if (this.currentState == PCBStates.INITIAL)
      return getInitialStateEvent(transitionData); 
    if (this.currentState == PCBStates.DOWN)
      return getDownStateEvent(transitionData); 
    if (this.currentState == PCBStates.REOPEN)
      return getReopenStateEvent(transitionData); 
    if (this.currentState == PCBStates.SUSPECT)
      return getSuspectStateEvent(transitionData); 
    return null;
  }
  
  protected List<State> createStates() {
    List<State> states = new ArrayList<>();
    states.add(PCBStates.OKAY.stateOrdinal(), new OkayState(this));
    states.add(PCBStates.SUSPECT.stateOrdinal(), new SuspectState(this));
    states.add(PCBStates.DOWN.stateOrdinal(), new DownState(this));
    states.add(PCBStates.REOPEN.stateOrdinal(), new ReopenState(this));
    states.add(PCBStates.INITIAL.stateOrdinal(), new InitialState(this));
    return states;
  }
  
  public void act() {}
  
  public void onConnectionUp() {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PCB-ST-MCHN", "Peer: " + getPeerName() + ", Connection is Up so generating ConnectionUp event."); 
    cancelConnectionInitiatorTask();
    IStateTransitionData transitionData = new IStateTransitionData() {
        Map<IStateTransitionDataCode, Object> data = new HashMap<>();
        
        public Object getData(IStateTransitionDataCode key) {
          return this.data.get(key);
        }
        
        public void addObject(IStateTransitionDataCode key, Object value) {
          this.data.put(key, value);
        }
      };
    transitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, PCBEvents.ConnectionUp);
    try {
      onStateTransitionTrigger(transitionData);
    } catch (UnhandledTransitionException unhandledTransitionException) {}
    scheduleWatchDogTask();
  }
  
  public void onConnectionDown() {
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("PCB-ST-MCHN", "Peer: " + getPeerName() + ", Connection goes down so generating ConnectionDown event"); 
    cancelWatchDogTask();
    IStateTransitionData transitionData = new IStateTransitionData() {
        Map<IStateTransitionDataCode, Object> data = new HashMap<>();
        
        public Object getData(IStateTransitionDataCode key) {
          return this.data.get(key);
        }
        
        public void addObject(IStateTransitionDataCode key, Object value) {
          this.data.put(key, value);
        }
      };
    transitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, PCBEvents.ConnectionDown);
    try {
      onStateTransitionTrigger(transitionData);
    } catch (UnhandledTransitionException unhandledTransitionException) {}
    scheduleConnectionInitiatorTask();
  }
  
  public void onReceive(IStateTransitionData transitionData) {
    try {
      onStateTransitionTrigger(transitionData);
    } catch (UnhandledTransitionException unhandledTransitionException) {}
  }
  
  public void onTimerElapsed() {
    boolean lockAquired = false;
    try {
      if (!(lockAquired = this.timerExpireStateLock.tryLock())) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("PCB-ST-MCHN", "Peer: " + getPeerName() + ", TimerExpires event is already generated."); 
        return;
      } 
      if (LogManager.getLogger().isDebugLogLevel())
        LogManager.getLogger().debug("PCB-ST-MCHN", "Peer: " + getPeerName() + ", Timer elapsed so generating TimerExpires event."); 
      IStateTransitionData transitionData = new IStateTransitionData() {
          Map<IStateTransitionDataCode, Object> data = new HashMap<>();
          
          public Object getData(IStateTransitionDataCode key) {
            return this.data.get(key);
          }
          
          public void addObject(IStateTransitionDataCode key, Object value) {
            this.data.put(key, value);
          }
        };
      transitionData.addObject((IStateTransitionDataCode)PeerDataCode.PEER_EVENT, PCBEvents.TimerExpires);
      onStateTransitionTrigger(transitionData);
    } catch (UnhandledTransitionException unhandledTransitionException) {
    
    } finally {
      if (lockAquired)
        try {
          this.timerExpireStateLock.unlock();
        } catch (Exception e) {
          LogManager.ignoreTrace(e);
        }  
    } 
  }
  
  public void setWatchdog() {
    this.watchdogTimer = this.watchdogDuration + System.currentTimeMillis();
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PCB-ST-MCHN", "Peer: " + getPeerName() + ", Rescheduling the watchdog timer to " + this.watchdogDuration + " ms."); 
  }
  
  public void setPending(boolean pending) {
    this.pending.set(pending);
  }
  
  public int getNumDwa() {
    return this.numDwa.get();
  }
  
  public void incrementNumDwa() {
    this.numDwa.incrementAndGet();
  }
  
  public void setNumDwa(int numDwa) {
    this.numDwa.set(numDwa);
  }
  
  private StateEvent getOkayStateEvent(IStateTransitionData transitionData) {
    PCBEvents events = (PCBEvents)transitionData.getData((IStateTransitionDataCode)PeerDataCode.PEER_EVENT);
    DiameterPacket request = (DiameterPacket)transitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    StateEvent stateEvent = null;
    if (events != null) {
      switch (events) {
        case ConnectionDown:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ConnectionDown, (IStateEnum)PCBStates.DOWN, transitionData);
          break;
        case TimerExpires:
          if (!isPending()) {
            stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpiresAndNotPending, (IStateEnum)PCBStates.OKAY, transitionData);
            break;
          } 
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpiresAndPending, (IStateEnum)PCBStates.SUSPECT, transitionData);
          break;
      } 
    } else if (request != null) {
      if (isDwa(request)) {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveDWA, (IStateEnum)PCBStates.OKAY, transitionData);
      } else {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveNonDWA, (IStateEnum)PCBStates.OKAY, transitionData);
      } 
    } 
    return stateEvent;
  }
  
  private StateEvent getSuspectStateEvent(IStateTransitionData transitionData) {
    PCBEvents events = (PCBEvents)transitionData.getData((IStateTransitionDataCode)PeerDataCode.PEER_EVENT);
    DiameterPacket request = (DiameterPacket)transitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    StateEvent stateEvent = null;
    if (events != null) {
      switch (events) {
        case ConnectionDown:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ConnectionDown, (IStateEnum)PCBStates.DOWN, transitionData);
          break;
        case TimerExpires:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpires, (IStateEnum)PCBStates.DOWN, transitionData);
          break;
      } 
    } else if (request != null) {
      if (isDwa(request)) {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveDWA, (IStateEnum)PCBStates.OKAY, transitionData);
      } else {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveNonDWA, (IStateEnum)PCBStates.OKAY, transitionData);
      } 
    } 
    return stateEvent;
  }
  
  private StateEvent getInitialStateEvent(IStateTransitionData transitionData) {
    PCBEvents events = (PCBEvents)transitionData.getData((IStateTransitionDataCode)PeerDataCode.PEER_EVENT);
    DiameterPacket request = (DiameterPacket)transitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    StateEvent stateEvent = null;
    if (events != null) {
      switch (events) {
        case ConnectionUp:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ConnectionUp, (IStateEnum)PCBStates.OKAY, transitionData);
          break;
        case TimerExpires:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpires, (IStateEnum)PCBStates.INITIAL, transitionData);
          break;
      } 
    } else if (request != null) {
      if (isDwa(request)) {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveDWA, (IStateEnum)PCBStates.INITIAL, transitionData);
      } else {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveNonDWA, (IStateEnum)PCBStates.INITIAL, transitionData);
      } 
    } 
    return stateEvent;
  }
  
  private StateEvent getReopenStateEvent(IStateTransitionData transitionData) {
    PCBEvents events = (PCBEvents)transitionData.getData((IStateTransitionDataCode)PeerDataCode.PEER_EVENT);
    DiameterPacket request = (DiameterPacket)transitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    StateEvent stateEvent = null;
    if (events != null) {
      switch (events) {
        case ConnectionDown:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ConnectionDown, (IStateEnum)PCBStates.DOWN, transitionData);
          break;
        case TimerExpires:
          if (isPending()) {
            if (getNumDwa() < 0) {
              stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpiresAndPendingAndDWALessThanZero, (IStateEnum)PCBStates.DOWN, transitionData);
              break;
            } 
            stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpiresAndPendingAndDWANotLessThanZero, (IStateEnum)PCBStates.REOPEN, transitionData);
            break;
          } 
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpiresAndNotPending, (IStateEnum)PCBStates.REOPEN, transitionData);
          break;
      } 
    } else if (request != null) {
      if (isDwa(request)) {
        if (getNumDwa() == 2) {
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveDWAAndNumEqualsTwo, (IStateEnum)PCBStates.OKAY, transitionData);
        } else if (getNumDwa() < 2) {
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveDWAAndNumLessThanTwo, (IStateEnum)PCBStates.REOPEN, transitionData);
        } 
      } else {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveNonDWA, (IStateEnum)PCBStates.REOPEN, transitionData);
      } 
    } 
    return stateEvent;
  }
  
  private StateEvent getDownStateEvent(IStateTransitionData transitionData) {
    PCBEvents events = (PCBEvents)transitionData.getData((IStateTransitionDataCode)PeerDataCode.PEER_EVENT);
    DiameterPacket request = (DiameterPacket)transitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    StateEvent stateEvent = null;
    if (events != null) {
      switch (events) {
        case ConnectionUp:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ConnectionUp, (IStateEnum)PCBStates.REOPEN, transitionData);
          break;
        case TimerExpires:
          stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.TimerExpires, (IStateEnum)PCBStates.DOWN, transitionData);
          break;
      } 
    } else if (request != null) {
      if (isDwa(request)) {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveDWA, (IStateEnum)PCBStates.DOWN, transitionData);
      } else {
        stateEvent = new StateEvent(this.currentState, (IEventEnum)PCBEvents.ReceiveNonDWA, (IStateEnum)PCBStates.DOWN, transitionData);
      } 
    } 
    return stateEvent;
  }
  
  private boolean isPending() {
    return this.pending.get();
  }
  
  private boolean isDwa(DiameterPacket packet) {
    return (packet.getCommandCode() == CommandCode.DEVICE_WATCHDOG.code && !packet.isRequest());
  }
  
  private boolean isTimerElapsed() {
    long timeMillies = System.currentTimeMillis();
    long diff = timeMillies - this.watchdogTimer;
    if (diff >= -1000L) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PCB-ST-MCHN", "Peer: " + getPeerName() + ", Watchdog timer elapsed"); 
      return true;
    } 
    return false;
  }
  
  private class ConnectionInitiatorTask extends BaseIntervalBasedTask {
    private long interval = 30L;
    
    private ConnectionInitiatorTask(long interval) {
      this.interval = interval;
    }
    
    public void execute(AsyncTaskContext context) {
      PCBStateMachine.this.attemptOpen();
    }
    
    public long getInterval() {
      return this.interval;
    }
    
    public boolean isFixedDelay() {
      return true;
    }
    
    public long getInitialDelay() {
      return 10000L;
    }
    
    public TimeUnit getTimeUnit() {
      return TimeUnit.MILLISECONDS;
    }
  }
  
  private class WatchDogTask extends BaseIntervalBasedTask {
    private long interval = 30L;
    
    private WatchDogTask(long interval) {
      this.interval = interval;
    }
    
    public void execute(AsyncTaskContext context) {
      if (PCBStateMachine.this.isTimerElapsed())
        PCBStateMachine.this.onTimerElapsed(); 
    }
    
    public long getInterval() {
      return this.interval;
    }
    
    public boolean isFixedDelay() {
      return true;
    }
    
    public long getInitialDelay() {
      return this.interval;
    }
    
    public TimeUnit getTimeUnit() {
      return TimeUnit.MILLISECONDS;
    }
  }
  
  public int getTimeout() {
    return 3000;
  }
  
  protected String getKey() {
    return "PCB-ST-MCHN";
  }
  
  private void scheduleConnectionInitiatorTask() {
    if (!isInitiateConnection() || this.connectionInitiatorTask != null)
      return; 
    synchronized (this.initConnectionLock) {
      if (this.connectionInitiatorTask != null)
        return; 
      this.connectionInitiatorTask = this.stackContext.scheduleIntervalBasedTask((IntervalBasedTask)new ConnectionInitiatorTask(this.initiateConnectionDuration));
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PCB-ST-MCHN", "Initiate Connection Task scheduled for Peer: " + getPeerName()); 
  }
  
  private void scheduleWatchDogTask() {
    if (this.watchdogDuration <= 0L || this.watchDogTask != null)
      return; 
    synchronized (this.watchDogLock) {
      if (this.watchDogTask != null)
        return; 
      this.watchDogTask = this.stackContext.scheduleIntervalBasedTask((IntervalBasedTask)new WatchDogTask(this.watchdogDuration));
      this.watchdogTimer = this.watchdogDuration + System.currentTimeMillis();
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PCB-ST-MCHN", "Watch Dog Task scheduled for Peer: " + getPeerName()); 
  }
  
  private void cancelConnectionInitiatorTask() {
    if (this.connectionInitiatorTask == null)
      return; 
    boolean isCancelled = true;
    synchronized (this.initConnectionLock) {
      if (this.connectionInitiatorTask == null)
        return; 
      isCancelled = this.connectionInitiatorTask.cancel(false);
      this.connectionInitiatorTask = null;
    } 
    if (!isCancelled) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PCB-ST-MCHN", "Unable to cancel Connection initiator Task for Peer: " + getPeerName()); 
      return;
    } 
    this.stackContext.purgeCancelledTasks();
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PCB-ST-MCHN", "Connection initiator Task cancelled for Peer: " + getPeerName()); 
  }
  
  private void cancelWatchDogTask() {
    if (this.watchDogTask == null)
      return; 
    boolean isCancelled = true;
    synchronized (this.watchDogLock) {
      if (this.watchDogTask == null)
        return; 
      isCancelled = this.watchDogTask.cancel(false);
      this.watchDogTask = null;
    } 
    if (!isCancelled) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PCB-ST-MCHN", "Unable to cancel Watch Dog Task for Peer: " + getPeerName()); 
      return;
    } 
    this.stackContext.purgeCancelledTasks();
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PCB-ST-MCHN", "Watch Dog Task cancelled for Peer: " + getPeerName()); 
  }
  
  protected abstract boolean isInitiateConnection();
}
