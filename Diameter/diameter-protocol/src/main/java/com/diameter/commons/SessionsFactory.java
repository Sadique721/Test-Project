package com.diameter.commons;

import java.util.Collection;

public interface SessionsFactory extends SessionEventListener {
  boolean hasSession(String paramString);
  
  int removeAllSessions();
  
  long removeAllSessions(String paramString);
  
  boolean removeSession(String paramString);
  
  int getSessionCount();
  
  Collection<Session> getAllSessions();
  
  int removeIdleSession(long paramLong);
  
  ISession readOnlySession(String paramString);
  
  Session getOrCreateSession(String paramString);
}
