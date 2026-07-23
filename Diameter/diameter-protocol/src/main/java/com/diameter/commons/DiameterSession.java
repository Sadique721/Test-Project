package com.diameter.commons;

public class DiameterSession extends Session {
	
  public DiameterSession(String sessionId, SessionEventListener eventListener) {
    this(sessionId, eventListener, TimeSource.systemTimeSource());
  }
  
  public DiameterSession(String sessionId, SessionEventListener eventListener, TimeSource timeSource) {
    super(sessionId, eventListener, timeSource);
  }
}
