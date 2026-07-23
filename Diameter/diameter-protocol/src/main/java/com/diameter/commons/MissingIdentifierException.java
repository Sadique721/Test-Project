package com.diameter.commons;

public class MissingIdentifierException extends Exception {
  private static final long serialVersionUID = 3641986468612017706L;
  
  private boolean isOptional;
  
  public MissingIdentifierException(String message) {
    super(message);
  }
  
  public MissingIdentifierException(String message, boolean isIdentifierOptional) {
    super(message);
    this.isOptional = isIdentifierOptional;
  }
  
  public void setIsOptional(boolean isOptional) {
    this.isOptional = isOptional;
  }
  
  public boolean isOptional() {
    return this.isOptional;
  }
}
