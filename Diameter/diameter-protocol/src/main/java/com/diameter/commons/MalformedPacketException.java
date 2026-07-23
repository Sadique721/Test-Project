package com.diameter.commons;

public class MalformedPacketException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public MalformedPacketException() {
    super("Malformed Request Packet");
  }
  
  public MalformedPacketException(String message) {
    super(message);
  }
  
  public MalformedPacketException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public MalformedPacketException(Throwable cause) {
    super(cause);
  }
}
