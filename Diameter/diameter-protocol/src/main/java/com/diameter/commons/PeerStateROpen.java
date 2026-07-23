package com.diameter.commons;

public class PeerStateROpen extends PeerStateBase {
  public PeerStateROpen(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.R_Open, actionsExecutor, peerStateMachineContext);
  }
  
  public StateEvent entryAction(StateEvent event) {
    StateEvent stateEvent = null;
    if (!this.peerStateMachineContext.getPeerListener().isPeerConnected()) {
      stateEvent = new StateEvent(this.stateEnum, (IEventEnum)DiameterPeerEvent.RPeerDisc, (IStateEnum)DiameterPeerState.Closed);
      processEvent(stateEvent);
    } else {
      this.peerActionsExecutor.onConnectionUp();
    } 
    return stateEvent;
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum type = stateEvent.getEventIdentifier();
    DiameterPeerEvent peerEvent = DiameterPeerEvent.getByEventOrdinal(type.eventOrdinal());
    switch (peerEvent) {
      case SendMessage:
        this.peerActionsExecutor.atomicActionSndMessage(stateEvent);
        return true;
      case RrcvMessage:
        this.peerActionsExecutor.atomicActionProcess(stateEvent);
        return true;
      case RRcvDWR:
        this.peerActionsExecutor.atomicActionProcessDWR(stateEvent);
        this.peerActionsExecutor.atomicActionRSndDWA(stateEvent);
        return true;
      case RRcvDWA:
        this.peerActionsExecutor.atomicActionProcessDWA(stateEvent);
        return true;
      case RConnCER:
        this.peerActionsExecutor.atomicActionProcessDuplicateConnection(stateEvent);
        return true;
      case Stop:
        this.peerActionsExecutor.atomicActionRSndDPR(stateEvent, peerEvent);
        return true;
      case RRcvDPR:
        this.peerActionsExecutor.atomicActionRSndDPA(stateEvent);
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        return true;
      case RPeerDisc:
        this.peerActionsExecutor.atomicActionRDisc(stateEvent);
        return true;
      case RRcvCER:
        this.peerActionsExecutor.atomicActionRSndCEA(stateEvent);
        return true;
      case RRcvCEA:
        this.peerActionsExecutor.atomicActionProcessCEA(stateEvent);
        return true;
      case IPeerDisc:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        break;
    } 
    return false;
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    NetworkConnectionHandler connectionHandler = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && diameterPacket.isRequest()) {
      if (!this.peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler))
        return new StateEvent((IStateEnum)DiameterPeerState.R_Open, (IEventEnum)DiameterPeerEvent.RConnCER, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
      return new StateEvent((IStateEnum)DiameterPeerState.R_Open, (IEventEnum)DiameterPeerEvent.RRcvCER, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData);
    } 
    if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && !diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.R_Open, (IEventEnum)DiameterPeerEvent.RRcvCEA, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode() && diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.R_Open, (IEventEnum)DiameterPeerEvent.RRcvDWR, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode() && !diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.R_Open, (IEventEnum)DiameterPeerEvent.RRcvDWA, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode() && diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.R_Open, (IEventEnum)DiameterPeerEvent.RRcvDPR, (IStateEnum)DiameterPeerState.Closed, stateTransitionData); 
    if (!DiameterUtility.isBaseProtocolPacket(diameterPacket.getCommandCode()))
      return new StateEvent((IStateEnum)DiameterPeerState.R_Open, (IEventEnum)DiameterPeerEvent.RrcvMessage, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
    return null;
  }
}
