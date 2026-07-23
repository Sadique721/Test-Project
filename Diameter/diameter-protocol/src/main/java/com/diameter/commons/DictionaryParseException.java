package com.diameter.commons;

public class DictionaryParseException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public DictionaryParseException() {}
  
  public DictionaryParseException(String message) {
    super(message);
  }
  
  public DictionaryParseException(Throwable cause) {
    super(cause);
  }
  
  public DictionaryParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
