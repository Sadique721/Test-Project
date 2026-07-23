package com.diameter.commons;

public interface ISession {
  public static final ISession NO_SESSION = new ISession() {
      public Object setParameter(String key, Object parameterValue) {
        return null;
      }
      
      public Object removeParameter(String key) {
        return null;
      }
      
      public void release() {}
      
      public String getSessionId() {
        return null;
      }
      
      public Object getParameter(String str) {
        return null;
      }
      
      public long getLastAccessedTime() {
        return 0L;
      }
      
      public long getCreationTime() {
        return 0L;
      }
      
      public void update(ValueProvider valueProvider) {}
    };
  
  String getSessionId();
  
  long getCreationTime();
  
  long getLastAccessedTime();
  
  Object setParameter(String paramString, Object paramObject);
  
  Object getParameter(String paramString);
  
  Object removeParameter(String paramString);
  
  void update(ValueProvider paramValueProvider);
  
  void release();
}
