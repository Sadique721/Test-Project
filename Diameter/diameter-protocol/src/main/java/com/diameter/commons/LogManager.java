package com.diameter.commons;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager {
  public static final String DEFAULT_LOGGER_KEY = "DEFAULT_LOGGER";
  
  private static Map<String, ILogger> keyToLogger = new ConcurrentHashMap<>();
  
  static ThreadLocal<ILogger> loggerLocal = new LoggerThreadLocal();
  
  private static volatile LogKeyResolver logKeyResolver = new ThreadNameBasedReslover(10);
  
  static {
    keyToLogger.put("DEFAULT_LOGGER", new FileLogger());
  }
  
  public static void setDefaultLogger(ILogger defaultLogger) {
    Preconditions.checkNotNull(defaultLogger, "defaultLogger is null");
    keyToLogger.put("DEFAULT_LOGGER", defaultLogger);
    resetLogger();
  }
  
  public static void setLogKeyResolver(LogKeyResolver logKeyResolver) {
    Preconditions.checkNotNull(logKeyResolver, "logKeyResolver is null");
    LogManager.logKeyResolver = logKeyResolver;
  }
  
  public static ILogger getLogger() {
    return loggerLocal.get();
  }
  
  public static void setLogger(String key, ILogger logger) {
    Preconditions.checkNotNull(key, "key for logger is null");
    Preconditions.checkNotNull(logger, "logger is null");
    keyToLogger.put(key, logger);
  }
  
  private static class LoggerThreadLocal extends ThreadLocal<ILogger> {
    private LoggerThreadLocal() {}
    
    protected ILogger initialValue() {
      ILogger logger = LogManager.get(LogManager.logKeyResolver.resloveKey());
      if (logger == null)
        logger = LogManager.get("DEFAULT_LOGGER"); 
      return logger;
    }
  }
  
  @VisibleForTesting
  static ILogger get(String key) {
    return keyToLogger.get(key);
  }
  
  private static void resetLogger() {
    loggerLocal.remove();
  }
  
  public static void ignoreTrace(Exception e) {}
  
  public static interface LogKeyResolver {
    String resloveKey();
  }
}
