package com.diameter.commons;

public class ElementRegistrationFailedException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public ElementRegistrationFailedException() {}
  
  public ElementRegistrationFailedException(String message) {
    super(message);
  }
  
  public ElementRegistrationFailedException(Throwable cause) {
    super(cause);
  }
  
  public ElementRegistrationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
