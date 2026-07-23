package com.diameter.commons;

public interface SessionEventListener {
  boolean removeSession(Session paramSession);
  
  void update(Session paramSession);
}
