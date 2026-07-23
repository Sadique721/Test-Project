package com.diameter.commons;

public class DriverProcessFailedException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public DriverProcessFailedException() {
    super("Driver level operation failed.");
  }
  
  public DriverProcessFailedException(String message) {
    super(message);
  }
  
  public DriverProcessFailedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public DriverProcessFailedException(Throwable cause) {
    super(cause);
  }
}
