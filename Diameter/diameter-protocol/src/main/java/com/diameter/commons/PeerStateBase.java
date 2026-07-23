package com.diameter.commons;

public abstract class PeerStateBase extends StateBase {
  protected IPeerAtomicActionsExecutor peerActionsExecutor;
  
  protected IPeerStateMachineContext peerStateMachineContext;
  
  protected IPeerListener peerListener;
  
  protected IStateEnum stateEnum;
  
  protected PeerStateBase(IStateEnum stateEnum, IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
    this.stateEnum = stateEnum;
    this.peerActionsExecutor = actionsExecutor;
    this.peerStateMachineContext = peerStateMachineContext;
  }
  
  protected IPeerAtomicActionsExecutor getActionExecutor() {
    return this.peerActionsExecutor;
  }
}
