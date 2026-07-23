package com.diameter.commons;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiameterAppMessageHandler {
  private final String MODULE = "DIAMETER-APP-HANDLER";
  
  private final Map<Long, IApplicationListener> appListeners;
  
  private final IStackContext stackContext;
  
  public DiameterAppMessageHandler(IStackContext stackContext) {
    this.appListeners = new ConcurrentHashMap<>();
    this.stackContext = stackContext;
  }
  
  public void addApplicationListener(ApplicationListener diameterApplicationListener) {
    for (ApplicationEnum applicationEnum : diameterApplicationListener.getApplicationEnum())
      this.appListeners.put(Long.valueOf(applicationEnum.getApplicationId()), diameterApplicationListener); 
  }
  
  public void handleReceivedRequest(DiameterRequest request, Session diameterSession) throws UnsupportedApplicationException {
    IApplicationListener diameterApplicationListener = this.appListeners.get(Long.valueOf(request.getApplicationID()));
    if (diameterApplicationListener == null) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("DIAMETER-APP-HANDLER", "Application Listener not registered for App-ID=" + request
            .getApplicationID()); 
      throw new UnsupportedApplicationException(request.getApplicationID());
    } 
    try {
      diameterApplicationListener.handleApplicationRequest(diameterSession, request);
    } catch (Exception e) {
      LogManager.getLogger().error("DIAMETER-APP-HANDLER", "Unknown Exception, While processing request of Application : " + request.getApplicationID() + " Error: " + e.getMessage());
      LogManager.getLogger().trace("DIAMETER-APP-HANDLER", e);
      DiameterAnswer answer = new DiameterAnswer(request, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
      try {
        sendDiameterAnswer(request, answer);
      } catch (CommunicationException ex) {
        LogManager.getLogger().error("DIAMETER-APP-HANDLER", "Could not send Error-Answer, Reason: " + ex.getMessage());
        LogManager.getLogger().trace("DIAMETER-APP-HANDLER", (Throwable)ex);
      } 
    } 
  }
  
  private void sendDiameterAnswer(DiameterRequest request, DiameterAnswer answer) throws CommunicationException {
    this.stackContext.getPeerCommunicator(request.getRequestingHost()).sendAnswer(request, answer);
  }
}
