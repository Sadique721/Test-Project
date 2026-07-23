package com.diameter.commons;

public class PeerStateIOpen extends PeerStateBase {
  public PeerStateIOpen(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.I_Open, actionsExecutor, peerStateMachineContext);
  }
  
  public StateEvent entryAction(StateEvent event) {
    StateEvent stateEvent = null;
    if (!this.peerStateMachineContext.getPeerListener().isPeerConnected()) {
      stateEvent = new StateEvent(this.stateEnum, (IEventEnum)DiameterPeerEvent.IPeerDisc, (IStateEnum)DiameterPeerState.Closed);
      processEvent(stateEvent);
    } else {
      this.peerActionsExecutor.onConnectionUp();
    } 
    return stateEvent;
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    IEventEnum eventEnum = stateEvent.getEventIdentifier();
    DiameterPeerEvent diameterPeerEvents = DiameterPeerEvent.getByEventOrdinal(eventEnum.eventOrdinal());
    switch (diameterPeerEvents) {
      case SendMessage:
        this.peerActionsExecutor.atomicActionSndMessage(stateEvent);
        return true;
      case IrcvMessage:
        this.peerActionsExecutor.atomicActionProcess(stateEvent);
        return true;
      case IRcvDWR:
        this.peerActionsExecutor.atomicActionProcessDWR(stateEvent);
        this.peerActionsExecutor.atomicActionRSndDWA(stateEvent);
        return true;
      case IRcvDWA:
        this.peerActionsExecutor.atomicActionProcessDWA(stateEvent);
        return true;
      case RConnCER:
        this.peerActionsExecutor.atomicActionProcessDuplicateConnection(stateEvent);
        return true;
      case Stop:
        this.peerActionsExecutor.atomicActionISndDPR(stateEvent, diameterPeerEvents);
        return true;
      case IRcvDPR:
        this.peerActionsExecutor.atomicActionISndDPA(stateEvent);
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        return true;
      case IPeerDisc:
        this.peerActionsExecutor.atomicActionIDisc(stateEvent);
        return true;
      case IRcvCER:
        this.peerActionsExecutor.atomicActionISndCEA(stateEvent);
        return true;
      case IRcvCEA:
        this.peerActionsExecutor.atomicActionProcessCEA(stateEvent);
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
    if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && diameterPacket.isRequest()) {
      if (!this.peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler))
        return new StateEvent((IStateEnum)DiameterPeerState.I_Open, (IEventEnum)DiameterPeerEvent.RConnCER, (IStateEnum)DiameterPeerState.I_Open, stateTransitionData); 
      return new StateEvent((IStateEnum)DiameterPeerState.I_Open, (IEventEnum)DiameterPeerEvent.IRcvCER, (IStateEnum)DiameterPeerState.I_Open, stateTransitionData);
    } 
    if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && !diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.I_Open, (IEventEnum)DiameterPeerEvent.IRcvCEA, (IStateEnum)DiameterPeerState.I_Open, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode() && diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.I_Open, (IEventEnum)DiameterPeerEvent.IRcvDWR, (IStateEnum)DiameterPeerState.I_Open, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode() && !diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.I_Open, (IEventEnum)DiameterPeerEvent.IRcvDWA, (IStateEnum)DiameterPeerState.I_Open, stateTransitionData); 
    if (diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode() && diameterPacket.isRequest())
      return new StateEvent((IStateEnum)DiameterPeerState.I_Open, (IEventEnum)DiameterPeerEvent.IRcvDPR, (IStateEnum)DiameterPeerState.Closed, stateTransitionData); 
    if (!DiameterUtility.isBaseProtocolPacket(diameterPacket.getCommandCode()))
      return new StateEvent((IStateEnum)DiameterPeerState.I_Open, (IEventEnum)DiameterPeerEvent.IrcvMessage, (IStateEnum)DiameterPeerState.I_Open, stateTransitionData); 
    return null;
  }
}
