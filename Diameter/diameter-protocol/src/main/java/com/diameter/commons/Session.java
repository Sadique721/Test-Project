package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Session implements ISession {
  public static final String SESSION_RELEASE_KEY = "SESSION_RELEASE_KEY";
  
  private String sessionId;
  
  private long creationTime;
  
  private final SessionEventListener sessionEventListener;
  
  private final ConcurrentHashMap<String, Object> parameters;
  
  private long lastAccessedTime;
  
  private volatile SessionState state;
  
  private AppSession appSession;
  
  private TimeSource timeSource;
  
  public Session(String strSessionId, SessionEventListener sessionEventListener) {
    this(strSessionId, sessionEventListener, TimeSource.systemTimeSource());
  }
  
  public Session(String strSessionId, SessionEventListener sessionEventListener, TimeSource timeSource) {
    this.sessionId = strSessionId;
    this.timeSource = timeSource;
    this.sessionEventListener = sessionEventListener;
    this.creationTime = timeSource.currentTimeInMillis();
    this.lastAccessedTime = this.creationTime;
    this.parameters = new ConcurrentHashMap<>(8, 0.75F, 4);
    this.state = SessionState.Ok;
  }
  
  public String getSessionId() {
    return this.sessionId;
  }
  
  protected void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  
  public long getCreationTime() {
    return this.creationTime;
  }
  
  protected void setCreationTime(long creationTime) {
    this.creationTime = creationTime;
  }
  
  public long getLastAccessedTime() {
    return this.lastAccessedTime;
  }
  
  protected void setLastAccessedTime(long lastAccessedTime) {
    this.lastAccessedTime = lastAccessedTime;
  }
  
  public SessionState getSessionState() {
    return this.state;
  }
  
  protected void setSessionState(SessionState sessionState) {
    this.state = sessionState;
  }
  
  protected boolean isReleased() {
    return (this.state == SessionState.Released);
  }
  
  public void release() {
    if (this.state == SessionState.Released) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("SESSION", "Can not remove Session: " + this.sessionId + " as session is already removed"); 
      return;
    } 
    this.state = SessionState.Released;
    this.sessionEventListener.removeSession(this);
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("SESSION", "Session: " + this.sessionId + " created on: " + new Timestamp(this.creationTime) + " and last updated on: " + new Timestamp(this.lastAccessedTime) + " is removed"); 
  }
  
  public void addAppSession(AppSession appSession) {
    touch();
    this.appSession = appSession;
  }
  
  public AppSession getAppSession() {
    touch();
    return this.appSession;
  }
  
  public Object setParameter(String key, Object parameterValue) {
    touch();
    return this.parameters.put(key, parameterValue);
  }
  
  public Object getParameter(String key) {
    touch();
    return this.parameters.get(key);
  }
  
  public Object removeParameter(String key) {
    touch();
    return this.parameters.remove(key);
  }
  
  private void touch() {
    this.lastAccessedTime = this.timeSource.currentTimeInMillis();
  }
  
  public String toString() {
    StringWriter stringBuffer = new StringWriter();
    PrintWriter out = new PrintWriter(stringBuffer);
    out.println("Session Id: " + this.sessionId);
    out.println("Session create-time: " + this.creationTime);
    out.println("Session Last accessed time: " + this.lastAccessedTime);
    out.close();
    return stringBuffer.toString();
  }
  
  protected Map<String, Object> getParameters() {
    return this.parameters;
  }
  
  public void update(ValueProvider noValueProvider) {
    if (!isReleased())
      this.sessionEventListener.update(this); 
  }
}
