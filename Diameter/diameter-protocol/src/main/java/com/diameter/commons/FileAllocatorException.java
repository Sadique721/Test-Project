package com.diameter.commons;

public class FileAllocatorException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public FileAllocatorException() {
    super("Error in initialization of Module.");
  }
  
  public FileAllocatorException(String message) {
    super(message);
  }
  
  public FileAllocatorException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public FileAllocatorException(Throwable cause) {
    super(cause);
  }
}
