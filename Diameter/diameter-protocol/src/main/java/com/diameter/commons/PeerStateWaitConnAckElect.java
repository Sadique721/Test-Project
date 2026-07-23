package com.diameter.commons;

public class PeerStateWaitConnAckElect extends PeerStateBase {
  private final String MODULE = "WAIT_I_CEA";
  
  public PeerStateWaitConnAckElect(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.Wait_Conn_Ack_Elect, actionsExecutor, peerStateMachineContext);
  }
  
  public StateEvent entryAction(StateEvent event) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("WAIT_I_CEA", "Entry action is called. with event::" + event); 
    this.peerActionsExecutor.startTimeoutEventTimer();
    return null;
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum eventEnum = stateEvent.getEventIdentifier();
    DiameterPeerEvent diameterPeerEvents = DiameterPeerEvent.getByEventOrdinal(eventEnum.eventOrdinal());
    switch (diameterPeerEvents) {
      case IRcvConnAck:
        this.peerActionsExecutor.atomicActionSndCER(stateEvent);
        this.peerActionsExecutor.atomicActionElect(stateEvent);
        break;
      case IRcvConnNack:
        this.peerActionsExecutor.atomicActionRSndCEA(stateEvent);
        break;
      case RPeerDisc:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        break;
      case RConnCER:
        this.peerActionsExecutor.atomicActionRReject((NetworkConnectionHandler)stateEvent.getStateTransitionData().getData((IStateTransitionDataCode)PeerDataCode.CONNECTION));
        break;
      case Timeout:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        break;
      case Start:
        this.peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
        break;
      case IPeerDisc:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        break;
    } 
    return true;
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    NetworkConnectionHandler connectionHandler = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (diameterPacket == null) {
      if (connectionHandler != null)
        return new StateEvent((IStateEnum)DiameterPeerState.Wait_Conn_Ack_Elect, (IEventEnum)DiameterPeerEvent.IRcvConnAck, (IStateEnum)DiameterPeerState.Wait_Returns, stateTransitionData); 
      if (connectionHandler == null)
        return new StateEvent((IStateEnum)DiameterPeerState.Wait_Conn_Ack_Elect, (IEventEnum)DiameterPeerEvent.IRcvConnNack, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
    } else if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && diameterPacket.isRequest() && 
      !this.peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler)) {
      return new StateEvent((IStateEnum)DiameterPeerState.Wait_Conn_Ack_Elect, (IEventEnum)DiameterPeerEvent.RConnCER, (IStateEnum)DiameterPeerState.Wait_Conn_Ack_Elect, stateTransitionData);
    } 
    return null;
  }
}
