package com.diameter.commons;

public class ExplicitRoutingFailedException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private ResultCode resultCode = ResultCode.DIAMETER_ER_NOT_AVAILABLE;
  
  public ExplicitRoutingFailedException() {}
  
  public ExplicitRoutingFailedException(ResultCode resultCode, String message) {
    super(message);
    this.resultCode = resultCode;
  }
  
  public ExplicitRoutingFailedException(String message) {
    super(message);
  }
  
  public ExplicitRoutingFailedException(Throwable cause) {
    super(cause);
  }
  
  public ExplicitRoutingFailedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public ExplicitRoutingFailedException(ResultCode resultCode, Throwable e) {
    super(e);
    this.resultCode = resultCode;
  }
  
  public ResultCode getResultCode() {
    return this.resultCode;
  }
}
