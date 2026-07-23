package com.diameter.commons;

public class PeerStateClosing extends PeerStateBase {
  private final String MODULE = "CLOSING";
  
  public PeerStateClosing(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.Closing, actionsExecutor, peerStateMachineContext);
  }
  
  public StateEvent entryAction(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("CLOSING", "Entry action is called. with event::" + event); 
    this.peerActionsExecutor.startTimeoutEventTimer();
    return null;
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    ResultCode resultCode;
    IEventEnum type = stateEvent.getEventIdentifier();
    DiameterPeerEvent peerEvent = DiameterPeerEvent.getByEventOrdinal(type.eventOrdinal());
    switch (peerEvent) {
      case IRcvDPA:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        return true;
      case RRcvDPA:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        return true;
      case Timeout:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        return true;
      case IPeerDisc:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        return true;
      case RPeerDisc:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        return true;
      case Start:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        return true;
      case RConnCER:
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("CLOSING", "Processing RConnCER event.");
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.FORCE_CLOSE);
        resultCode = ResultCode.DIAMETER_UNABLE_TO_DELIVER;
        if (this.peerActionsExecutor.atomicActionRAccept(stateEvent))
          resultCode = this.peerActionsExecutor.atomicActionProcessCER(stateEvent); 
        this.peerActionsExecutor.atomicActionRSndCEA(stateEvent, resultCode);
        if (resultCode != ResultCode.DIAMETER_SUCCESS && LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("CLOSING", "Result code is " + resultCode.toString()); 
        return true;
    } 
    return false;
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    NetworkConnectionHandler connectionHandler = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode() && !diameterPacket.isRequest() && connectionHandler.isResponder())
      return new StateEvent((IStateEnum)DiameterPeerState.Closing, (IEventEnum)DiameterPeerEvent.RRcvDPA, (IStateEnum)DiameterPeerState.Closed, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode() && !diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.Closing, (IEventEnum)DiameterPeerEvent.IRcvDPA, (IStateEnum)DiameterPeerState.Closed, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.Closing, (IEventEnum)DiameterPeerEvent.RConnCER, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
    return null;
  }
}
