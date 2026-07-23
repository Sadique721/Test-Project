package com.diameter.commons;

public class ReopenState extends BasePCBState {
  public ReopenState(PCBActionExecutor actionExecutor) {
    super(actionExecutor);
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum type = stateEvent.getEventIdentifier();
    PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
    switch (pcbEvent) {
      case ReceiveDWAAndNumEqualsTwo:
        this.actionExecutor.setPending(false);
        this.actionExecutor.incrementNumDwa();
        this.actionExecutor.failback();
        break;
      case ReceiveDWAAndNumLessThanTwo:
        this.actionExecutor.setPending(false);
        this.actionExecutor.incrementNumDwa();
        break;
      case ReceiveNonDWA:
        this.actionExecutor.throwaway();
        break;
      case TimerExpiresAndNotPending:
        this.actionExecutor.setPending(true);
        this.actionExecutor.sendWatchdog();
        this.actionExecutor.setWatchdog();
        break;
      case TimerExpiresAndPendingAndDWALessThanZero:
        this.actionExecutor.sendDPR();
        this.actionExecutor.setWatchdog();
        break;
      case TimerExpiresAndPendingAndDWANotLessThanZero:
        this.actionExecutor.setNumDwa(-1);
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
