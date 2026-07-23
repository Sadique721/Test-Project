package com.diameter.commons;

public class IllegalPriorityException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public IllegalPriorityException(String message) {
    super(message);
  }
}
