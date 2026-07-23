package com.diameter.commons;

public class PolicyDataRegistrationFailedException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public PolicyDataRegistrationFailedException() {
    super("Failed Initialization.");
  }
  
  public PolicyDataRegistrationFailedException(String message) {
    super(message);
  }
  
  public PolicyDataRegistrationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public PolicyDataRegistrationFailedException(Throwable cause) {
    super(cause);
  }
}
