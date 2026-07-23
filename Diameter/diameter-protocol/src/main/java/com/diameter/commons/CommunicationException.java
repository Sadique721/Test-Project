package com.diameter.commons;

public class CommunicationException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public CommunicationException() {
    super("Communication error.");
  }
  
  public CommunicationException(String message) {
    super(message);
  }
  
  public CommunicationException(Throwable cause) {
    super(cause);
  }
  
  public CommunicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
