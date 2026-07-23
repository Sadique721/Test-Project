package com.diameter.commons;

public class PeerStateWaitICEA extends PeerStateBase {
  private final String MODULE = "WAIT_I_CEA";
  
  public PeerStateWaitICEA(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.Wait_I_CEA, actionsExecutor, peerStateMachineContext);
  }
  
  public StateEvent entryAction(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("WAIT_I_CEA", "Entry action is called. with event::" + event); 
    this.peerActionsExecutor.startTimeoutEventTimer();
    return null;
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    ResultCode resultCode;
    IPeerListener peerListener;
    IEventEnum event = stateEvent.getEventIdentifier();
    DiameterPeerEvent peerEvent = DiameterPeerEvent.getByEventOrdinal(event.eventOrdinal());
    switch (peerEvent) {
      case Start:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        break;
      case IRcvCEA:
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("WAIT_I_CEA", "Processing IRcvCEA event."); 
        resultCode = this.peerActionsExecutor.atomicActionProcessCEA(stateEvent);
        if (resultCode != ResultCode.DIAMETER_SUCCESS);
        break;
      case IPeerDisc:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        break;
      case IRcvNonCEA:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_DPR);
        break;
      case Timeout:
        peerListener = this.peerStateMachineContext.getPeerListener();
        if (peerListener != null) {
          if (peerListener.isSendDPRonCloseEvent()) {
            this.peerActionsExecutor.atomicActionISndDPR(stateEvent, peerEvent);
            break;
          } 
          this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        } 
        break;
      case RPeerDisc:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        break;
    } 
    return true;
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    NetworkConnectionHandler connectionHandler = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && !diameterPacket.isRequest()) {
      if (this.peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler))
        return new StateEvent((IStateEnum)DiameterPeerState.Wait_I_CEA, (IEventEnum)DiameterPeerEvent.IRcvCEA, (IStateEnum)DiameterPeerState.I_Open, stateTransitionData); 
      return new StateEvent((IStateEnum)DiameterPeerState.Wait_I_CEA, (IEventEnum)DiameterPeerEvent.RConnCER, (IStateEnum)DiameterPeerState.Wait_Returns, stateTransitionData);
    } 
    return new StateEvent((IStateEnum)DiameterPeerState.Wait_I_CEA, (IEventEnum)DiameterPeerEvent.IRcvNonCEA, (IStateEnum)DiameterPeerState.Closed, stateTransitionData);
  }
}
