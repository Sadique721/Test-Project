package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public class TranslateFailureAction implements RoutingFailureAction {
  private static final String MODULE = "TRANSLATE-FLR-ACT";
  
  private String transMappName;
  
  private List<String> warnings;
  
  private ITranslationAgent translationAgent;
  
  public TranslateFailureAction(String failureArgs, ITranslationAgent translationAgent) {
    this.transMappName = failureArgs;
    this.warnings = new ArrayList<>();
    this.translationAgent = translationAgent;
  }
  
  public void init() {
    if (this.transMappName == null || this.transMappName.trim().length() == 0) {
      this.warnings.add("No Translation Mapping found for " + DiameterFailureConstants.TRANSLATE + "  Failure Action");
      return;
    } 
    this.transMappName = this.transMappName.trim();
    if (!this.translationAgent.isExists(this.transMappName))
      this.warnings.add("Translation Mapping: " + this.transMappName + " in Translate failure action is not registered"); 
  }
  
  public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession, DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity, String originPeerName) {
    String sessionId = failureAnswer.getAVPValue("0:263");
    int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("TRANSLATE-FLR-ACT", "Performing " + DiameterFailureConstants.TRANSLATE + " Failure Action with Failure Argument " + this.transMappName + " for Session-ID=" + sessionId + " HbH-ID=" + hopByHopKey); 
    DiameterAnswer translatedAnswer = new DiameterAnswer(originRequest);
    if (failureAnswer.isProxiable())
      translatedAnswer.setProxiableBit(); 
    if (failureAnswer.isError())
      translatedAnswer.setErrorBit(); 
    if (failureAnswer.isReTransmitted())
      translatedAnswer.setReTransmittedBit(); 
    TranslatorParamsImpl translatorParamsImpl = new TranslatorParamsImpl(failureAnswer, translatedAnswer, originRequest, remoteRequest);
    translatorParamsImpl.setParam("DIAMETER_SESSION", routingSession);
    try {
      this.translationAgent.translate(this.transMappName, (TranslatorParams)translatorParamsImpl, false);
      failureAnswer = (DiameterAnswer)translatorParamsImpl.getParam("TO_PACKET");
    } catch (TranslationFailedException e) {
      LogManager.getLogger().error("TRANSLATE-FLR-ACT", "Error while traslating diameterResponse with HbH-ID=" + hopByHopKey + ". Sending Diameter Answer without Traslation");
      LogManager.getLogger().trace("TRANSLATE-FLR-ACT", (Throwable)e);
    } 
    return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, (DiameterPacket)failureAnswer);
  }
  
  public List<String> getWarnings() {
    return this.warnings;
  }
}
