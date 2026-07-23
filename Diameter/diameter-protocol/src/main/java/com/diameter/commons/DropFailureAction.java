package com.diameter.commons;

import java.util.List;

public class DropFailureAction implements RoutingFailureAction {
  private static final String MODULE = "DROP-FLR-ACT";
  
  public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession, DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity, String originPeerName) {
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("DROP-FLR-ACT", "Performing " + DiameterFailureConstants.DROP + " Failure Action. for Session-ID=" + failureAnswer
          .getAVPValue("0:263") + " HbH-ID=" + failureAnswer
          .getHop_by_hopIdentifier()); 
    return new FailureActionResult(FailureActionResultCodes.DROP, (DiameterPacket)failureAnswer);
  }
  
  public void init() {}
  
  public List<String> getWarnings() {
    return null;
  }
}