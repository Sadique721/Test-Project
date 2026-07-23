package com.diameter.commons;

public class OkayState extends BasePCBState {
  public OkayState(PCBActionExecutor actionExecutor) {
    super(actionExecutor);
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum type = stateEvent.getEventIdentifier();
    PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
    switch (pcbEvent) {
      case ReceiveDWA:
        this.actionExecutor.setPending(false);
        this.actionExecutor.setWatchdog();
        break;
      case ReceiveNonDWA:
        this.actionExecutor.setWatchdog();
        break;
      case TimerExpiresAndPending:
        this.actionExecutor.failover();
        this.actionExecutor.setWatchdog();
        break;
      case TimerExpiresAndNotPending:
        this.actionExecutor.setPending(true);
        this.actionExecutor.sendWatchdog();
        this.actionExecutor.setWatchdog();
        break;
      case ConnectionDown:
        this.actionExecutor.closeConnection(ConnectionEvents.CONNECTION_BREAK);
        this.actionExecutor.failover();
        this.actionExecutor.setWatchdog();
        break;
    } 
    return true;
  }
}
