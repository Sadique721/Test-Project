package com.diameter.commons;

public class ProxyAgent extends RelayAgent {
  private static final String MODULE = "PROXY-AGNT";
  
  private ITranslationAgent translationAgent;
  
  public ProxyAgent(RouterContext routerContext, ITranslationAgent translationAgent, IDiameterSessionManager diameterSessionManager) {
    super(routerContext, diameterSessionManager);
    this.translationAgent = translationAgent;
  }
  
  protected DiameterRequest buildRequest(DiameterSession diameterSession, DiameterRequest originRequest, RoutingEntry routingEntry) throws RoutingFailedException {
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("PROXY-AGNT", "Building Diameter Request for Session-Id=" + originRequest
          .getAVPValue("0:263")); 
    String translationName = routingEntry.getTranslationMapping();
    if (translationName != null)
      return translateRequest(originRequest, diameterSession, translationName); 
    return super.buildRequest(diameterSession, originRequest, routingEntry);
  }
  
  protected DiameterRequest translateRequest(DiameterRequest diameterRequest, DiameterSession diameterSession, String translatorName) throws RoutingFailedException {
    DiameterRequest translatedRequest = null;
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("PROXY-AGNT", "Translating packet before proxy using translation policy: " + translatorName); 
    translatedRequest = new DiameterRequest();
    translatedRequest.setCommandCode(diameterRequest.getCommandCode());
    translatedRequest.setApplicationID(diameterRequest.getApplicationID());
    translatedRequest.setEnd_to_endIdentifier(diameterRequest.getEnd_to_endIdentifier());
    if (diameterRequest.isProxiable())
      translatedRequest.setProxiableBit(); 
    if (diameterRequest.isError())
      translatedRequest.setErrorBit(); 
    if (diameterRequest.isReTransmitted())
      translatedRequest.setReTransmittedBit(); 
    TranslatorParamsImpl translatorParamsImpl = new TranslatorParamsImpl(diameterRequest, translatedRequest);
    translatorParamsImpl.setParam("DIAMETER_SESSION", diameterSession);
    try {
      this.translationAgent.translate(translatorName, (TranslatorParams)translatorParamsImpl, true);
      translatedRequest = (DiameterRequest)translatorParamsImpl.getParam("TO_PACKET");
      translatedRequest.setHop_by_hopIdentifier(HopByHopPool.get());
    } catch (TranslationFailedException e) {
      throw new RoutingFailedException(RoutingActions.PROXY, 
          DiameterErrorMessageConstants.translationFailed(translatorName));
    } 
    if (Boolean.parseBoolean(String.valueOf(translatorParamsImpl.getParam("DUMMY_MAPPING")))) {
      translatedRequest.setParameter("DUMMY_MAPPING", Boolean.TRUE);
      translatedRequest.setParameter("SELECTED_TRANSLATION_POLICY", translatorName);
    } 
    diameterRequest.setParameter("SELECTED_REQUEST_MAPPING", translatorParamsImpl
        .getParam("SELECTED_REQUEST_MAPPING"));
    return translatedRequest;
  }
  
  protected DiameterAnswer buildAnswer(DiameterRequest originRequest, DiameterAnswer diameterAnswer, RoutingEntry routingEntry, DiameterSession routingSession, DiameterRequest translatedRequest) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PROXY-AGNT", "Building Diameter Answer for Session-ID=" + diameterAnswer
          .getAVPValue("0:263")); 
    String translationName = routingEntry.getTranslationMapping();
    if (translationName != null)
      diameterAnswer = translateAnswer(translationName, diameterAnswer, routingSession, originRequest, translatedRequest); 
    return diameterAnswer;
  }
  
  protected DiameterAnswer translateAnswer(String translatorName, DiameterAnswer diameterAnswer, DiameterSession session, DiameterRequest originRequest, DiameterRequest translatedRequest) {
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("PROXY-AGNT", "Translating packet before routing Answer using translation policy: " + translatorName); 
    DiameterAnswer translatedAnswer = new DiameterAnswer(originRequest);
    if (diameterAnswer.isProxiable())
      translatedAnswer.setProxiableBit(); 
    if (diameterAnswer.isError())
      translatedAnswer.setErrorBit(); 
    if (diameterAnswer.isReTransmitted())
      translatedAnswer.setReTransmittedBit(); 
    TranslatorParamsImpl translatorParamsImpl = new TranslatorParamsImpl(diameterAnswer, translatedAnswer, originRequest, translatedRequest);
    translatorParamsImpl.setParam("DIAMETER_SESSION", session);
    translatorParamsImpl.setParam("SELECTED_REQUEST_MAPPING", originRequest
        .getParameter("SELECTED_REQUEST_MAPPING"));
    try {
      this.translationAgent.translate(translatorName, (TranslatorParams)translatorParamsImpl, false);
      diameterAnswer = (DiameterAnswer)translatorParamsImpl.getParam("TO_PACKET");
    } catch (TranslationFailedException e) {
      LogManager.getLogger().error("PROXY-AGNT", "Error while translating Diameter Answer with HbH-ID=" + diameterAnswer
          .getHop_by_hopIdentifier() + ". Passthrough DiameterAnswer without Traslation");
    } 
    return diameterAnswer;
  }
}
