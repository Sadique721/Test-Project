package com.diameter.commons;

public class InvalidTypeCastException extends Exception {
  private static final long serialVersionUID = 3641986468612017706L;
  
  public InvalidTypeCastException(String message) {
    super(message);
  }
  
  public InvalidTypeCastException(Exception e) {
    super(e);
  }
  
  public InvalidTypeCastException(String message, Exception e) {
    super(message, e);
  }
}
