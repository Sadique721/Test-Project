package com.diameter.commons;

import javax.annotation.Nullable;

public interface IPeerAtomicActionsExecutor extends IAtomicActionsExecutor {
  void atomicActionSndConnReq(StateEvent paramStateEvent);
  
  boolean atomicActionRAccept(StateEvent paramStateEvent);
  
  ResultCode atomicActionProcessCER(StateEvent paramStateEvent);
  
  void atomicActionRSndCEA(StateEvent paramStateEvent, ResultCode paramResultCode);
  
  void atomicActionRSndCEA(StateEvent paramStateEvent);
  
  void atomicActionISndCEA(StateEvent paramStateEvent);
  
  void atomicActionSndCER(StateEvent paramStateEvent);
  
  void atomicActionCleanup(StateEvent paramStateEvent, ConnectionEvents paramConnectionEvents);
  
  void atomicActionError(StateEvent paramStateEvent, ConnectionEvents paramConnectionEvents);
  
  void atomicActionElect(StateEvent paramStateEvent);
  
  void atomicActionIDisc(StateEvent paramStateEvent);
  
  ResultCode atomicActionProcessCEA(StateEvent paramStateEvent);
  
  void atomicActionRDisc(StateEvent paramStateEvent);
  
  void atomicActionRReject(@Nullable NetworkConnectionHandler paramNetworkConnectionHandler);
  
  void atomicActionSndMessage(StateEvent paramStateEvent);
  
  void atomicActionProcess(StateEvent paramStateEvent);
  
  void atomicActionProcessDWR(StateEvent paramStateEvent);
  
  void atomicActionRSndDWA(StateEvent paramStateEvent);
  
  void atomicActionISndDWA(StateEvent paramStateEvent);
  
  void atomicActionProcessDWA(StateEvent paramStateEvent);
  
  void atomicActionRSndDPR(StateEvent paramStateEvent, DiameterPeerEvent paramDiameterPeerEvent);
  
  void atomicActionISndDPR(StateEvent paramStateEvent, DiameterPeerEvent paramDiameterPeerEvent);
  
  void atomicActionRSndDPA(StateEvent paramStateEvent);
  
  void atomicActionISndDPA(StateEvent paramStateEvent);
  
  void onConnectionUp();
  
  void onConnectionDown();
  
  void startTimeoutEventTimer();
  
  void atomicActionProcessDuplicateConnection(StateEvent paramStateEvent);
}
