package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

public class SessionFactoryManagerImpl implements SessionFactoryManager {
  private static final String MODULE = "SESSION_FACTORY_MANAGER";
  
  private Map<Long, SessionsFactory> appIdToFactory;
  
  private IDiameterStackContext diameterStackContext;
  
  private SessionsFactory inMemorySessionFactory;
  
  private SessionsFactory hazelCastSessionFactory;
  
  private SessionsFactory nullSessionFactory;
  
  private TimeSource timesource;
  
  public SessionFactoryManagerImpl(@Nonnull IDiameterStackContext diameterStackContext) {
    this(diameterStackContext, TimeSource.systemTimeSource());
  }
  
  SessionFactoryManagerImpl(@Nonnull IDiameterStackContext diameterStackContext, TimeSource timesource) {
    Preconditions.checkNotNull(diameterStackContext, "Diameter Stack Context is null.");
    this.timesource = timesource;
    this.appIdToFactory = new HashMap<>();
    this.diameterStackContext = diameterStackContext;
    this.nullSessionFactory = (SessionsFactory)new NullSessionFactory(this.timesource);
    this.inMemorySessionFactory = (SessionsFactory)new InMemorySessionFactory(this.diameterStackContext, this.timesource);
  }
  
  public void register(long appId, SessionFactoryType sessionFactory, Optional<HazelcastImdgInstance> optional) throws InitializationFailedException {
    switch (sessionFactory) {
      case INMEMORY:
        this.appIdToFactory.put(Long.valueOf(appId), this.inMemorySessionFactory);
        return;
      case NULLSESSION:
        this.appIdToFactory.put(Long.valueOf(appId), this.nullSessionFactory);
        return;
    } 
    throw new InitializationFailedException("Invalid Session Factory type");
  }
  
  public SessionsFactory getSessionFactory(long appId) {
    if (this.appIdToFactory.containsKey(Long.valueOf(appId)))
      return this.appIdToFactory.get(Long.valueOf(appId)); 
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("SESSION_FACTORY_MANAGER", "Using default In-Memory session factory  for Application-Id: " + appId + ", Reason: " + appId + " is unregisterd."); 
    return this.appIdToFactory.getOrDefault(Long.valueOf(appId), this.inMemorySessionFactory);
  }
  
  public int getSessionCount() {
    return this.appIdToFactory.values().stream().distinct().mapToInt(sessionsFactory -> sessionsFactory.getSessionCount()).sum();
  }
  
  public int removeIdleSession(long sessionTimeOut) {
    return this.appIdToFactory.entrySet().stream().mapToInt(o -> ((SessionsFactory)o.getValue()).removeIdleSession(sessionTimeOut)).sum();
  }
  
  public boolean hasSession(String sessionId) {
    for (Map.Entry<Long, SessionsFactory> entry : this.appIdToFactory.entrySet()) {
      if (((SessionsFactory)entry.getValue()).hasSession(sessionId))
        return true; 
    } 
    return false;
  }
  
  public int removeAllSessions() {
    return this.appIdToFactory.entrySet().stream().mapToInt(o -> ((SessionsFactory)o.getValue()).removeAllSessions()).sum();
  }
  
  public void release(String sessionId) {
    this.appIdToFactory.entrySet().forEach(entry -> {
          if (((SessionsFactory)entry.getValue()).hasSession(sessionId)) {
            ((SessionsFactory)entry.getValue()).getOrCreateSession(sessionId).release();
            return;
          } 
        });
  }
  
  public boolean hasSession(String sessionId, long applicationId) {
    SessionsFactory sessionsFactory = this.appIdToFactory.get(Long.valueOf(applicationId));
    if (sessionsFactory != null)
      return sessionsFactory.hasSession(sessionId); 
    return false;
  }
}