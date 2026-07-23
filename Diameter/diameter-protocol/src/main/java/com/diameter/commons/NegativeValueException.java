package com.diameter.commons;

public class NegativeValueException extends RuntimeException {
  static final long serialVersionUID = 1L;
  
  public NegativeValueException() {
    super("Value is Negative");
  }
  
  public NegativeValueException(String strMessage) {
    super(strMessage);
  }
}
