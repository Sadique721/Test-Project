package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public class DiameterRequest extends DiameterPacket {
  public static final String LOCATED_SESSION_DATA = "Located-Session-Data";
  
  private List<String> failedPeerList;
  
  private String requestingHost;
  
  private RoutingActions routingAction = RoutingActions.LOCAL;
  
  public DiameterRequest(boolean isLocal) {
    setRequestBit();
    if (isLocal) {
      setHop_by_hopIdentifier(HopByHopPool.get());
      setEnd_to_endIdentifier(EndToEndPool.get());
      IDiameterAVP originHostAVP = DiameterDictionary.getInstance().getAttribute("0:264");
      originHostAVP.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
      addAvp(originHostAVP);
      IDiameterAVP originRealm = DiameterDictionary.getInstance().getAttribute("0:296");
      originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
      addAvp(originRealm);
    } 
  }
  
  public DiameterRequest() {
    this(true);
  }
  
  public DiameterRequest getAsDiameterRequest() {
    return this;
  }
  
  public DiameterAnswer getAsDiameterAnswer() {
    return null;
  }
  
  public void parsePacketHeaderBytes(byte[] headerBytes) {
    super.parsePacketHeaderBytes(headerBytes);
  }
  
  public void addFailedPeer(String hostIdentity) {
    if (this.failedPeerList == null)
      this.failedPeerList = new ArrayList<>(1); 
    this.failedPeerList.add(hostIdentity);
  }
  
  public List<String> getFailedPeerList() {
    return this.failedPeerList;
  }
  
  public void setLocatedSessionData(List<SessionData> locatedSessionData) {
    setParameter("Located-Session-Data", locatedSessionData);
  }
  
  public List<SessionData> getLocatedSessionData() {
    return (List<SessionData>)getParameter("Located-Session-Data");
  }
  
  public String getRequestingHost() {
    return this.requestingHost;
  }
  
  public void setRequestingHost(String requestingHost) {
    this.requestingHost = requestingHost;
  }
  
  public RoutingActions getRoutingAction() {
    return this.routingAction;
  }
  
  public void setRoutingAction(RoutingActions routingAction) {
    this.routingAction = routingAction;
  }
}
