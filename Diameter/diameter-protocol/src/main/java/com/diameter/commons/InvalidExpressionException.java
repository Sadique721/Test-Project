package com.diameter.commons;

public class InvalidExpressionException extends Exception {
  private static final long serialVersionUID = -2138012637601256364L;
  
  public InvalidExpressionException(String message) {
    super(message);
  }
  
  public InvalidExpressionException(Exception e) {
    super(e);
  }
  
  public InvalidExpressionException(String message, Exception e) {
    super(message, e);
  }
}

