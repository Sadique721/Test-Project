package com.diameter.commons;

import java.io.IOException;

public class DiameterVirtualPeer extends DiameterPeer {
  private NetworkConnectionHandler virtualConnectionHandler;
  
  private IDiameterStackContext stackContext;
  
  public DiameterVirtualPeer(PeerData peerData, NetworkConnectionHandler virtualConnectionHandler, IDiameterStackContext stackContext, DiameterRouter diameterRouter, SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler appMessageHandler, ExplicitRoutingHandler explicitRoutingHandler, DuplicateDetectionHandler duplicateMessageHandler) {
    super(peerData, stackContext, diameterRouter, sessionFactoryManager, appMessageHandler, explicitRoutingHandler, duplicateMessageHandler);
    this.stackContext = stackContext;
    this.virtualConnectionHandler = virtualConnectionHandler;
  }
  
  protected void writeToStream(DiameterPacket packet) throws IOException {
    boolean destHostReplaced = false, destRealmReplaced = false;
    IDiameterAVP destHost = packet.getAVP("0:293");
    IDiameterAVP destRealm = packet.getAVP("0:283");
    if (destHost != null && "*".equals(destHost.getStringValue())) {
      destHost.setStringValue(getPeerData().getHostIdentity());
      destHostReplaced = true;
    } 
    if (destRealm != null && "*".equals(destRealm.getStringValue())) {
      destRealm.setStringValue(getPeerData().getRealmName());
      destRealmReplaced = true;
    } 
    packet.setSendTime(System.currentTimeMillis());
    this.virtualConnectionHandler.send((Packet)packet);
    this.stackContext.updateOutputStatistics(packet, getHostIdentity());
    if (destHostReplaced)
      destHost.setStringValue("*"); 
    if (destRealmReplaced)
      destRealm.setStringValue("*"); 
  }
  
  public void start() {
    super.start();
    getPeerStateMachine().switchCurrentStateTo((IStateEnum)DiameterPeerState.fromStateOrdinal(getPeerStateMachine().getCurrentState()), (IStateEnum)DiameterPeerState.R_Open);
    setConnectionListener(this.virtualConnectionHandler);
    getPeerStateMachine().onConnectionUp();
  }
  
  public boolean stop() {
    getPeerStateMachine().switchCurrentStateTo((IStateEnum)DiameterPeerState.fromStateOrdinal(getPeerStateMachine().getCurrentState()), (IStateEnum)DiameterPeerState.Closed);
    closeConnection(ConnectionEvents.SHUTDOWN);
    return super.stop();
  }
}
