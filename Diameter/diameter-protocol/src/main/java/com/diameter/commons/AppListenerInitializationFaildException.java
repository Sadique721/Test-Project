package com.diameter.commons;

public class AppListenerInitializationFaildException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public AppListenerInitializationFaildException() {}
  
  public AppListenerInitializationFaildException(String message) {
    super(message);
  }
  
  public AppListenerInitializationFaildException(Throwable cause) {
    super(cause);
  }
  
  public AppListenerInitializationFaildException(String message, Throwable cause) {
    super(message, cause);
  }
}