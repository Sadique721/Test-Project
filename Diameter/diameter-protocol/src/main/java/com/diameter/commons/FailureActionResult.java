package com.diameter.commons;

public class FailureActionResult {
  private FailureActionResultCodes action;
  
  private DiameterPacket diameterPacket;
  
  private String peerName;
  
  public FailureActionResult(FailureActionResultCodes action, DiameterPacket diameterPacket) {
    this(action, diameterPacket, null);
  }
  
  public FailureActionResult(FailureActionResultCodes action, DiameterPacket diameterPacket, String peerName) {
    this.action = action;
    this.diameterPacket = diameterPacket;
    this.peerName = peerName;
  }
  
  public FailureActionResultCodes getAction() {
    return this.action;
  }
  
  public DiameterPacket getDiameterPacket() {
    return this.diameterPacket;
  }
  
  public String getPeerName() {
    return this.peerName;
  }
}
