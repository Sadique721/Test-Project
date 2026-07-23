package com.diameter.commons;

public class TypeNotSupportedException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public TypeNotSupportedException(String message) {
    super(message);
  }
  
  public TypeNotSupportedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public TypeNotSupportedException(Throwable cause) {
    super(cause);
  }
  
  public TypeNotSupportedException(String message, int type) {
    super(message + " Type: " + type + " is not supported.");
  }
}


