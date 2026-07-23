package com.diameter.commons;

public interface ILogger {
  void error(String paramString1, String paramString2);
  
  void error(String paramString1, String paramString2, Exception paramException);
  
  void debug(String paramString1, String paramString2);
  
  void info(String paramString1, String paramString2);
  
  void warn(String paramString1, String paramString2);
  
  void trace(String paramString1, String paramString2);
  
  void trace(Throwable paramThrowable);
  
  void trace(String paramString, Throwable paramThrowable);
  
  int getCurrentLogLevel();
  
  boolean isLogLevel(LogLevel paramLogLevel);
  
  boolean isErrorLogLevel();
  
  boolean isWarnLogLevel();
  
  boolean isInfoLogLevel();
  
  boolean isDebugLogLevel();
  
  void addThreadName(String paramString);
  
  void removeThreadName(String paramString);
}
