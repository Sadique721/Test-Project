package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class NullDiameterSession extends DiameterSession {
  private static final String NULL_DIAMETER_SESSION = "Null Diameter Session";
  
  public NullDiameterSession(String sessionId, SessionEventListener eventListener, TimeSource timeSource) {
    super(sessionId, eventListener, timeSource);
  }
  
  public long getCreationTime() {
    return 0L;
  }
  
  protected void setCreationTime(long creationTime) {}
  
  public long getLastAccessedTime() {
    return 0L;
  }
  
  protected void setLastAccessedTime(long lastAccessedTime) {}
  
  public SessionState getSessionState() {
    return SessionState.Ok;
  }
  
  protected void setSessionState(SessionState sessionState) {}
  
  protected boolean isReleased() {
    return false;
  }
  
  public void release() {}
  
  public void addAppSession(AppSession appSession) {}
  
  public AppSession getAppSession() {
    return null;
  }
  
  public Object setParameter(String key, Object parameterValue) {
    return null;
  }
  
  public Object getParameter(String key) {
    return null;
  }
  
  public Object removeParameter(String key) {
    return null;
  }
  
  public String toString() {
    return "Null Diameter Session";
  }
  
  protected Map<String, Object> getParameters() {
    return new HashMap<>();
  }
}
