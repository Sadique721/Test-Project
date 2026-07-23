package com.diameter.commons;

public interface AsyncTaskContext {
  void setAttribute(String paramString, Object paramObject);
  
  Object getAttribute(String paramString);
}