package com.diameter.commons;

public class UnhandledTransitionException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public UnhandledTransitionException() {}
  
  public UnhandledTransitionException(String message) {
    super(message);
  }
  
  public UnhandledTransitionException(Throwable cause) {
    super(cause);
  }
  
  public UnhandledTransitionException(String message, Throwable cause) {
    super(message, cause);
  }
}
