package com.diameter.commons;

public class InitialState extends BasePCBState {
  public InitialState(PCBActionExecutor actionExecutor) {
    super(actionExecutor);
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum type = stateEvent.getEventIdentifier();
    PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
    switch (pcbEvent) {
      case ReceiveDWA:
        this.actionExecutor.setPending(false);
        this.actionExecutor.throwaway();
        break;
      case ReceiveNonDWA:
        this.actionExecutor.throwaway();
        break;
      case TimerExpires:
        this.actionExecutor.closeConnection(ConnectionEvents.TIMER_EXPIRED);
        this.actionExecutor.attemptOpen();
        this.actionExecutor.setWatchdog();
        break;
      case ConnectionUp:
        this.actionExecutor.setNumDwa(0);
        this.actionExecutor.setPending(false);
        this.actionExecutor.setWatchdog();
        break;
    } 
    return true;
  }
}
