package com.diameter.commons;

public class InvalidRoutingPacketException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  private ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
  
  public InvalidRoutingPacketException() {}
  
  public InvalidRoutingPacketException(ResultCode resultCode, String message) {
    super(message);
    this.resultCode = resultCode;
  }
  
  public InvalidRoutingPacketException(String message) {
    super(message);
  }
  
  public InvalidRoutingPacketException(Throwable cause) {
    super(cause);
  }
  
  public InvalidRoutingPacketException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public ResultCode getResultCode() {
    return this.resultCode;
  }
}