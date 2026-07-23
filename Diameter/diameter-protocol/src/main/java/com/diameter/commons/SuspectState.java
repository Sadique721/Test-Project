package com.diameter.commons;

public class SuspectState extends BasePCBState {
  public SuspectState(PCBActionExecutor actionExecutor) {
    super(actionExecutor);
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum type = stateEvent.getEventIdentifier();
    PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
    switch (pcbEvent) {
      case ReceiveDWA:
        this.actionExecutor.setPending(false);
        this.actionExecutor.failback();
        this.actionExecutor.setWatchdog();
        break;
      case ReceiveNonDWA:
        this.actionExecutor.failback();
        this.actionExecutor.setWatchdog();
        break;
      case TimerExpires:
        this.actionExecutor.sendDPR();
        this.actionExecutor.setWatchdog();
        break;
      case ConnectionDown:
        this.actionExecutor.closeConnection(ConnectionEvents.CONNECTION_BREAK);
        this.actionExecutor.setWatchdog();
        break;
    } 
    return true;
  }
}
