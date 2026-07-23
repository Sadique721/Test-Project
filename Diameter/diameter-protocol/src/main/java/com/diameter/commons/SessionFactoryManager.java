package com.diameter.commons;

import java.util.Optional;

public interface SessionFactoryManager {
  SessionsFactory getSessionFactory(long paramLong);
  
  void register(long paramLong, SessionFactoryType paramSessionFactoryType, Optional<HazelcastImdgInstance> paramOptional) throws InitializationFailedException;
  
  int getSessionCount();
  
  int removeIdleSession(long paramLong);
  
  boolean hasSession(String paramString);
  
  boolean hasSession(String paramString, long paramLong);
  
  int removeAllSessions();
  
  void release(String paramString);
}