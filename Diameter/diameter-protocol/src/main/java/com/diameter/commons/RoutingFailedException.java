package com.diameter.commons;

public class RoutingFailedException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
  
  private final RoutingActions routeAction;
  
  private transient DiameterPacket diameterPacket;
  
  public RoutingFailedException(ResultCode resultCode, RoutingActions routingActions, DiameterPacket diameterPacket, String message) {
    this(resultCode, routingActions, message);
    this.diameterPacket = diameterPacket;
  }
  
  public RoutingFailedException(RoutingActions routingAction, String message) {
    super(message);
    this.routeAction = routingAction;
  }
  
  public RoutingFailedException(ResultCode resultCode, RoutingActions routingAction, String message) {
    super(message);
    this.resultCode = resultCode;
    this.routeAction = routingAction;
  }
  
  public RoutingFailedException(ResultCode resultCode, RoutingActions routingAction) {
    this.resultCode = resultCode;
    this.routeAction = routingAction;
  }
  
  public ResultCode getResultCode() {
    return this.resultCode;
  }
  
  public RoutingActions getRoutingAction() {
    return this.routeAction;
  }
  
  public DiameterPacket getDiameterPacket() {
    return this.diameterPacket;
  }
}
