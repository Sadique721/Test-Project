package com.diameter.commons;

public class PeerStateWaitConnAck extends PeerStateBase {
  private final String MODULE = "WAIT-CONN-ACK";
  
  public PeerStateWaitConnAck(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.Wait_Conn_Ack, actionsExecutor, peerStateMachineContext);
  }
  
  public StateEvent entryAction(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("WAIT-CONN-ACK", "Entry action is called. with event::" + event); 
    this.peerActionsExecutor.startTimeoutEventTimer();
    return null;
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum eventEnum = stateEvent.getEventIdentifier();
    DiameterPeerEvent diameterPeerEvents = DiameterPeerEvent.getByEventOrdinal(eventEnum.eventOrdinal());
    switch (diameterPeerEvents) {
      case IRcvConnAck:
        this.peerActionsExecutor.atomicActionSndCER(stateEvent);
        return true;
      case IRcvConnNack:
        this.peerActionsExecutor.atomicActionCleanup(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        return true;
      case RConnCER:
        this.peerActionsExecutor.atomicActionRAccept(stateEvent);
        this.peerActionsExecutor.atomicActionProcessCER(stateEvent);
        return true;
      case Timeout:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        return true;
      case Start:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        return true;
      case IPeerDisc:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        return true;
      case RPeerDisc:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        break;
    } 
    return false;
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    NetworkConnectionHandler connectionHandler = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (diameterPacket == null) {
      if (connectionHandler != null)
        return new StateEvent((IStateEnum)DiameterPeerState.Wait_Conn_Ack, (IEventEnum)DiameterPeerEvent.IRcvConnAck, (IStateEnum)DiameterPeerState.Wait_I_CEA, stateTransitionData); 
      if (connectionHandler == null)
        return new StateEvent((IStateEnum)DiameterPeerState.Wait_Conn_Ack, (IEventEnum)DiameterPeerEvent.IRcvConnNack, (IStateEnum)DiameterPeerState.Closed, stateTransitionData); 
    } else if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && diameterPacket.isRequest() && 
      !this.peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler)) {
      return new StateEvent((IStateEnum)DiameterPeerState.Wait_Conn_Ack, (IEventEnum)DiameterPeerEvent.RConnCER, (IStateEnum)DiameterPeerState.Wait_Conn_Ack_Elect, stateTransitionData);
    } 
    return null;
  }
}
