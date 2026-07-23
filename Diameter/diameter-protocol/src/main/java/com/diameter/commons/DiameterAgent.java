package com.diameter.commons;

import javax.annotation.Nonnull;

public abstract class DiameterAgent {
  public static final String ROUTING_ENTRY = "ROUTING_ENTRY";
  
  protected RouterContext routerContext;
  
  protected DiameterAgent(RouterContext routerContext) {
    this.routerContext = routerContext;
  }
  
  public abstract void routeRequest(DiameterRequest paramDiameterRequest, DiameterSession paramDiameterSession, RoutingEntry paramRoutingEntry) throws RoutingFailedException;
  
  protected void sendClientInitiatedRequest(DiameterSession session, DiameterRequest request, @Nonnull ResponseListener listener, String destinationHost, RoutingActions routeAction) throws CommunicationException {
    DiameterPeerCommunicator peerCommunicator = this.routerContext.getPeerCommunicator(destinationHost);
    if (peerCommunicator == null)
      throw new CommunicationException(destinationHost + " not found"); 
    peerCommunicator.sendClientInitiatedRequest(session, request, listener);
    this.routerContext.updateRealmOutputStatistics((DiameterPacket)request, this.routerContext
        .getPeerData(destinationHost).getRealmName(), routeAction);
  }
  
  protected void sendServerInitiatedRequest(DiameterSession session, DiameterRequest request, @Nonnull ResponseListener listener, String destinationHost, RoutingActions routeAction) throws CommunicationException {
    DiameterPeerCommunicator peerCommunicator = this.routerContext.getPeerCommunicator(destinationHost);
    if (peerCommunicator == null)
      throw new CommunicationException(destinationHost + " not found"); 
    peerCommunicator.sendServerInitiatedRequest(session, request, listener);
    this.routerContext.updateRealmOutputStatistics((DiameterPacket)request, this.routerContext
        .getPeerData(destinationHost).getRealmName(), routeAction);
  }
  
  protected void sendAnswer(Session session, DiameterRequest request, DiameterAnswer answer, String destinationHost, RoutingActions routeAction) throws CommunicationException {
    IDiameterAVP resultCodeAVP = answer.getAVP("0:268");
    if (resultCodeAVP != null) {
      int resultCode = (int)resultCodeAVP.getInteger();
      if ((ResultCode.fromCode(resultCode)).vendorId == 21067L)
        resultCodeAVP.setInteger(ResultCode.DIAMETER_UNABLE_TO_DELIVER.code); 
    } 
    DiameterPeerCommunicator peerCommunicator = this.routerContext.getPeerCommunicator(destinationHost);
    if (peerCommunicator == null)
      throw new CommunicationException(destinationHost + " not found"); 
    peerCommunicator.sendAnswer(request, answer);
    this.routerContext.updateRealmOutputStatistics((DiameterPacket)answer, this.routerContext
        .getPeerData(destinationHost).getRealmName(), routeAction);
  }
}