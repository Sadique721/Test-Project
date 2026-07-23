package com.diameter.commons;

public class TranslationFailedException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public TranslationFailedException() {
    super("Failed Translation.");
  }
  
  public TranslationFailedException(String message) {
    super(message);
  }
  
  public TranslationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public TranslationFailedException(Throwable cause) {
    super(cause);
  }
}
