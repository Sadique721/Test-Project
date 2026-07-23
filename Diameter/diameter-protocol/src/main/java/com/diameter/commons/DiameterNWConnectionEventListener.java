package com.diameter.commons;

import java.util.Map;

public class DiameterNWConnectionEventListener implements NetworkConnectionEventListener {
  private final String MODULE = "DIA-NW-LIST";
  
  private IPeerListener peerListener;
  
  public DiameterNWConnectionEventListener(IPeerListener peerListener) {
    this.peerListener = peerListener;
  }
  
  public void connectionBreak(NetworkConnectionHandler connectionHandler, ConnectionEvents event) {
    LogManager.getLogger().warn("DIA-NW-LIST", "Connection has Broken, Event: " + event);
    try {
      if (connectionHandler.isResponder()) {
        this.peerListener.handleEvent((IEventEnum)DiameterPeerEvent.RPeerDisc, event);
      } else {
        this.peerListener.handleEvent((IEventEnum)DiameterPeerEvent.IPeerDisc, event);
      } 
    } catch (UnhandledTransitionException e) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("DIA-NW-LIST", e.getMessage()); 
    } 
  }
  
  public void connectionEstablished() {}
  
  public void connectionDPR(Map<PeerDataCode, String> eventParam, ConnectionEvents event) {
    try {
      this.peerListener.handleEvent((IEventEnum)DiameterPeerEvent.Stop, event, eventParam);
    } catch (UnhandledTransitionException e) {
      LogManager.getLogger().trace("DIA-NW-LIST", (Throwable)e);
    } 
  }
  
  public void connectionFailure(NetworkConnectionHandler connectionHandler) {
    LogManager.getLogger().warn("DIA-NW-LIST", "Connection Creation Failed");
    try {
      this.peerListener.handleEvent((IEventEnum)DiameterPeerEvent.IRcvConnNack, ConnectionEvents.CONNECTION_FAILURE);
    } catch (UnhandledTransitionException e) {
      LogManager.getLogger().trace("DIA-NW-LIST", (Throwable)e);
    } 
  }
  
  public void connectionCreated(NetworkConnectionHandler connectionHandler) {
    LogManager.getLogger().warn("DIA-NW-LIST", "Connection Created");
    try {
      this.peerListener.handleEvent((IEventEnum)DiameterPeerEvent.IRcvConnAck, ConnectionEvents.CONNECTION_CREATED);
    } catch (UnhandledTransitionException e) {
      LogManager.getLogger().trace("DIA-NW-LIST", (Throwable)e);
    } 
  }
}
