package com.diameter.commons;

import java.util.Collection;

public class NullSessionFactory implements SessionsFactory {
  private static final String NULL_SESSION_ID = "NULL_SESSION_ID";
  
  private NullDiameterSession nullDiameterSession;
  
  public NullSessionFactory(TimeSource timesource) {
    this.nullDiameterSession = new NullDiameterSession("NULL_SESSION_ID", new SessionEventListener() {
          public void update(Session session) {}
          
          public boolean removeSession(Session session) {
            return false;
          }
        },  timesource);
  }
  
  public boolean removeSession(Session session) {
    return false;
  }
  
  public void update(Session session) {}
  
  public boolean hasSession(String sessionId) {
    return false;
  }
  
  public int removeAllSessions() {
    return 0;
  }
  
  public long removeAllSessions(String sessionReleaseKey) {
    return 0L;
  }
  
  public boolean removeSession(String sessionId) {
    return false;
  }
  
  public int getSessionCount() {
    return 0;
  }
  
  public int removeIdleSession(long idleTime) {
    return 0;
  }
  
  public ISession readOnlySession(String sessionId) {
    return (ISession)getOrCreateSession(sessionId);
  }
  
  public Session getOrCreateSession(String sessionId) {
    return this.nullDiameterSession;
  }
  
	@Override
	public Collection<Session> getAllSessions() {
		return null;
	}
}
