package com.diameter.commons;

public class ParseException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public ParseException() {
    this("Radius Request Parse Error");
  }
  
  public ParseException(String message) {
    super(message);
  }
  
  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
