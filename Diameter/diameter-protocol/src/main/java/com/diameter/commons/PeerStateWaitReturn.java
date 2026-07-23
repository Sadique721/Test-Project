package com.diameter.commons;

public class PeerStateWaitReturn extends PeerStateBase {
  public PeerStateWaitReturn(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.Wait_Returns, actionsExecutor, peerStateMachineContext);
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum eventEnum = stateEvent.getEventIdentifier();
    DiameterPeerEvent diameterPeerEvents = DiameterPeerEvent.getByEventOrdinal(eventEnum.eventOrdinal());
    switch (diameterPeerEvents) {
      case WinElection:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        this.peerActionsExecutor.atomicActionRSndCEA(stateEvent);
        break;
      case IPeerDisc:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        this.peerActionsExecutor.atomicActionRSndCEA(stateEvent);
        break;
      case IRcvCEA:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        break;
      case RPeerDisc:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        break;
      case RConnCER:
        this.peerActionsExecutor.atomicActionRReject((NetworkConnectionHandler)stateEvent.getStateTransitionData().getData((IStateTransitionDataCode)PeerDataCode.CONNECTION));
        break;
      case Timeout:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_DPR);
        break;
      case Start:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        break;
    } 
    return true;
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    return null;
  }
}