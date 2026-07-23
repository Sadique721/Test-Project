package com.diameter.commons;

import java.util.List;

public class PassthroughFailureAction implements RoutingFailureAction {
  private static final String MODULE = "PASSTHR-FLR-ACT";
  
  public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession, DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity, String originPeerName) {
    String sessionId = failureAnswer.getAVPValue("0:263");
    int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("PASSTHR-FLR-ACT", "Performing " + DiameterFailureConstants.PASSTHROUGH + " Failure Action. for Session-ID=" + sessionId + " HbH-ID=" + hopByHopKey); 
    return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, (DiameterPacket)failureAnswer);
  }
  
  public void init() {}
  
  public List<String> getWarnings() {
    return null;
  }
}
