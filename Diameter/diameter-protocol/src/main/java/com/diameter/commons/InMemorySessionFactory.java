package com.diameter.commons;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionFactory implements SessionsFactory {
  private static final String MODULE = "SESSN-FACTORY";
  
  private TimeSource timeSource;
  
  protected final ConcurrentHashMap<String, Session> sessionIdToSession;
  
  public InMemorySessionFactory(IDiameterStackContext diameterStackContext, TimeSource timeSource) {
    this.timeSource = timeSource;
    this.sessionIdToSession = new ConcurrentHashMap<>(1024, 0.75F, diameterStackContext.getMaxWorkerThreads());
  }
  
  public int removeAllSessions() {
    int removedSessionCount = 0;
    for (Session session : this.sessionIdToSession.values()) {
      session.release();
      removedSessionCount++;
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("SESSN-FACTORY", "Removed All Sessions. No of Sessions removed: " + removedSessionCount); 
    return removedSessionCount;
  }
  
  public long removeAllSessions(String sessionReleaseKey) {
    if (sessionReleaseKey == null)
      return 0L; 
    long releaseCnt = 0L;
    for (Session session : this.sessionIdToSession.values()) {
      if (sessionReleaseKey.equals(session.getParameter("SESSION_RELEASE_KEY"))) {
        session.release();
        releaseCnt++;
      } 
    } 
    return releaseCnt;
  }
  
  public boolean removeSession(Session session) {
    return removeSession(session.getSessionId());
  }
  
  public boolean removeSession(String sessionId) {
    return (this.sessionIdToSession.remove(sessionId) != null);
  }
  
  public int getSessionCount() {
    return this.sessionIdToSession.size();
  }
  
  public int removeIdleSession(long idleTime) {
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("SESSN-FACTORY", "Session Cleanup Started. Sessions idle for " + (idleTime / 1000L) + " Sec. will be released."); 
    int noOfSessions = 0;
    LogManager.getLogger().warn("SESSN-FACTORY", "Total no of active Sessions are: " + this.sessionIdToSession.size());
    Long currentTimeMs = Long.valueOf(this.timeSource.currentTimeInMillis());
    for (Session sessionData : this.sessionIdToSession.values()) {
      long sessionElapsedTime = currentTimeMs.longValue() - sessionData.getLastAccessedTime();
      if (sessionElapsedTime > idleTime) {
        sessionData.release();
        noOfSessions++;
      } 
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("SESSN-FACTORY", "Number of sessions removed older than " + (idleTime / 1000L) + " Sec. = " + noOfSessions); 
    return noOfSessions;
  }
  
  public Session getOrCreateSession(String sessionId) {
    Session session = this.sessionIdToSession.get(sessionId);
    if (session == null) {
      session = new DiameterSession(sessionId, (SessionEventListener)this, this.timeSource);
      this.sessionIdToSession.put(sessionId, session);
    } 
    return session;
  }
  
  public void update(Session session) {}
  
  public boolean hasSession(String sessionId) {
    return this.sessionIdToSession.containsKey(sessionId);
  }
  
  public ISession readOnlySession(String sessionId) {
    ISession session = null;
    if (hasSession(sessionId))
      session = createReadOnlySession(this.sessionIdToSession.get(sessionId)); 
    return session;
  }
  
  private ISession createReadOnlySession(final Session session) {
    return new ISession() {
        public Object setParameter(String key, Object parameterValue) {
          throw new UnsupportedOperationException("Cannot set parameters using read only session");
        }
        
        public Object removeParameter(String key) {
          throw new UnsupportedOperationException("Cannot remove parameters using read only session");
        }
        
        public void release() {
          throw new UnsupportedOperationException("Cannot release using read only session");
        }
        
        public String getSessionId() {
          return session.getSessionId();
        }
        
        public Object getParameter(String str) {
          return session.getParameter(str);
        }
        
        public long getLastAccessedTime() {
          return session.getLastAccessedTime();
        }
        
        public long getCreationTime() {
          return session.getCreationTime();
        }
        
        public void update(ValueProvider valueProvider) {
          throw new UnsupportedOperationException("Cannot update using read only session");
        }
      };
  }

	@Override
	public Collection<Session> getAllSessions() {
		return this.sessionIdToSession.values();
	}
}
