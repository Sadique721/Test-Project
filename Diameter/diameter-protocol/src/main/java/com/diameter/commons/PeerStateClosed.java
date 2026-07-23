package com.diameter.commons;

public class PeerStateClosed extends PeerStateBase {
  private static final String MODULE = "PEER-STATE-CLOSED";
  
  public PeerStateClosed(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    super((IStateEnum)DiameterPeerState.Closed, actionsExecutor, peerStateMachineContext);
  }
  
  public StateEvent entryAction(StateEvent event) {
    StateEvent stateEvent = null;
    try {
      this.peerActionsExecutor.onConnectionDown();
    } catch (Exception e) {
      LogManager.getLogger().warn("PEER-STATE-CLOSED", e.getMessage());
    } 
    return stateEvent;
  }
  
  public boolean processEvent(StateEvent stateEvent) {
    ResultCode resultCode;
    IEventEnum event = stateEvent.getEventIdentifier();
    DiameterPeerEvent peerEvent = DiameterPeerEvent.getByEventOrdinal(event.eventOrdinal());
    switch (peerEvent) {
      case Start:
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("PEER-STATE-CLOSED", "Processing Start event."); 
        this.peerActionsExecutor.atomicActionSndConnReq(stateEvent);
        return true;
      case RConnCER:
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("PEER-STATE-CLOSED", "Processing RConnCER event."); 
        resultCode = ResultCode.DIAMETER_UNABLE_TO_DELIVER;
        if (this.peerActionsExecutor.atomicActionRAccept(stateEvent))
          resultCode = this.peerActionsExecutor.atomicActionProcessCER(stateEvent); 
        this.peerActionsExecutor.atomicActionRSndCEA(stateEvent, resultCode);
        if (resultCode != ResultCode.DIAMETER_SUCCESS && LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("PEER-STATE-CLOSED", "Result code is " + resultCode.toString()); 
        return true;
    } 
    return false;
  }
  
  public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
    DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.DIAMETER_RECEIVED_PACKET);
    NetworkConnectionHandler connectionHandler = (NetworkConnectionHandler)stateTransitionData.getData((IStateTransitionDataCode)PeerDataCode.CONNECTION);
    if (diameterPacket != null) {
      if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode() && diameterPacket.isRequest())
        return new StateEvent((IStateEnum)DiameterPeerState.Closed, (IEventEnum)DiameterPeerEvent.RConnCER, (IStateEnum)DiameterPeerState.R_Open, stateTransitionData); 
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER-STATE-CLOSED", "Received Packet is other than CER, Closing received connection"); 
      connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
    } 
    return null;
  }
}