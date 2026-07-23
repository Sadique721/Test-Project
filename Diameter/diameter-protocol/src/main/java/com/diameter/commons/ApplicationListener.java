package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ApplicationListener implements IApplicationListener {
  protected IStackContext stackContext;
  
  private static final String MODULE = "APP-LSTNR";
  
  private CompositeSessionReleaseIndicator sessionReleaseIndicator;
  
  private ApplicationEnum[] applicationEnums;
  
  private boolean initialized;
  
  public ApplicationListener(IStackContext stackContext) {
    this.stackContext = stackContext;
    this.sessionReleaseIndicator = new CompositeSessionReleaseIndicator();
  }
  
  public ApplicationListener(IStackContext stackContext, ApplicationEnum... applicationEnums) {
    this.stackContext = stackContext;
    this.applicationEnums = (ApplicationEnum[])Preconditions.checkNotNull(applicationEnums, "applicationEnums is null");
    Preconditions.checkArgument((applicationEnums.length > 0), "at least one applicationEnum is required");
    this.sessionReleaseIndicator = new CompositeSessionReleaseIndicator();
  }
  
  public void init() throws AppListenerInitializationFaildException {
    if (this.initialized)
      return; 
    ApplicationEnum[] applicationEnums = getApplicationEnum();
    for (ApplicationEnum applicationEnum : applicationEnums) {
      if (applicationEnum.getApplication() == null) {
        this.sessionReleaseIndicator.addIndicator((SessionReleaseIndiactor)new AppDefaultSessionReleaseIndicator());
      } else {
        AppDefaultSessionReleaseIndicator appDefaultSessionReleaseIndicator = null;
        SessionReleaseIndiactor sessionReleaseIndicator = createSessionReleaseIndicator(applicationEnum);
        if (sessionReleaseIndicator == null)
          appDefaultSessionReleaseIndicator = new AppDefaultSessionReleaseIndicator(); 
        this.sessionReleaseIndicator.addIndicator((SessionReleaseIndiactor)appDefaultSessionReleaseIndicator);
      } 
    } 
    this.initialized = true;
  }
  
  public void preProcess(Session session, DiameterRequest diameterRequest) {}
  
  public void preProcess(DiameterSession session, DiameterAnswer diameterAnswer) {}
  
  public void postProcess(DiameterSession session, DiameterAnswer diameterAnswer) {}
  
  public abstract String getApplicationIdentifier();
  
  public void handleApplicationRequest(Session session, DiameterRequest diameterRequest) {
    this.stackContext.updateRealmInputStatistics((DiameterPacket)diameterRequest, diameterRequest.getPeerData().getRealmName(), RoutingActions.LOCAL);
    preProcess(session, diameterRequest);
    processApplicationRequest(session, diameterRequest);
  }
  
  protected abstract void processApplicationRequest(Session paramSession, DiameterRequest paramDiameterRequest);
  
  public ApplicationEnum[] getApplicationEnum() {
    return this.applicationEnums;
  }
  
  protected boolean sessionEligibleToRelease(DiameterPacket diameterPacket) {
    return this.sessionReleaseIndicator.isEligible(diameterPacket);
  }
  
  public boolean isSupportedApplication(long applicationId) {
    for (ApplicationEnum applicationEnum : this.applicationEnums) {
      if (applicationEnum.getApplicationId() == applicationId)
        return true; 
    } 
    return false;
  }
  
  @Nullable
  protected abstract SessionReleaseIndiactor createSessionReleaseIndicator(@Nonnull ApplicationEnum paramApplicationEnum);
  
  @Nonnull
  public SessionReleaseIndiactor getSessionReleaseIndicator() {
    return this.sessionReleaseIndicator;
  }
  
  class CompositeSessionReleaseIndicator implements SessionReleaseIndiactor {
    private List<SessionReleaseIndiactor> sessionReleaseIndicators = new ArrayList<>(1);
    
    void addIndicator(@Nonnull SessionReleaseIndiactor sessionReleaseIndicator) {
      this.sessionReleaseIndicators.add(sessionReleaseIndicator);
    }
    
    public boolean isEligible(DiameterPacket diameterPacket) {
      for (SessionReleaseIndiactor sessionReleaseIndicator : this.sessionReleaseIndicators) {
        if (sessionReleaseIndicator.isEligible(diameterPacket))
          return true; 
      } 
      return false;
    }
  }
  
  protected boolean sendDiameterAnswer(@Nonnull DiameterSession session, @Nonnull DiameterRequest request, @Nonnull DiameterAnswer answer) throws CommunicationException {
    preProcess(session, answer);
    copyProxyInfo(request, answer);
    DiameterPeerCommunicator requestingCommunicator = this.stackContext.getPeerCommunicator(request.getRequestingHost());
    if (requestingCommunicator == null)
      throw new CommunicationException("Diameter peer communicator not found for host: " + request.getRequestingHost()); 
    requestingCommunicator.sendAnswer(request, answer);
    postProcess(session, answer);
    return true;
  }
  
  private void copyProxyInfo(DiameterRequest request, DiameterAnswer answer) {
    List<IDiameterAVP> diameterAvps = answer.getAVPList("0:284");
    if (Collectionz.isNullOrEmpty(diameterAvps)) {
      List<IDiameterAVP> iDiameterAvpList = request.getAVPList("0:284");
      if (!Collectionz.isNullOrEmpty(iDiameterAvpList)) {
        answer.addAvps(iDiameterAvpList);
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("APP-LSTNR", "Original Proxy Info AVPs added back to the Answer."); 
      } 
    } else {
      LogManager.getLogger().warn("APP-LSTNR", "Diameter Request Not Found for the Answer with Hop-By-Hop-ID = " + answer
          .getHop_by_hopIdentifier());
    } 
  }
}
