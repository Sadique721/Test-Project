package com.diameter.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ILogger implementation that delegates to SLF4J / Log4j2 instead of writing to
 * System.out. Kept for backwards compatibility with any code that may register it
 * as the stack logger; output now flows through the configured (async) appenders.
 */
public class ConsoleLogger implements ILogger {

  private static final Logger logger = LoggerFactory.getLogger("CORE");

  public void error(String module, String strMessage) {
    logger.error("[{}]: {}", module, strMessage);
  }

  public void error(String module, String strMessage, Exception e) {
    // Throwable passed as trailing argument so the full stack trace (with line numbers) is logged.
    logger.error("[{}]: {}", module, strMessage, e);
  }

  public void debug(String module, String strMessage) {
    logger.debug("[{}]: {}", module, strMessage);
  }

  public void info(String module, String strMessage) {
    logger.info("[{}]: {}", module, strMessage);
  }

  public void warn(String module, String strMessage) {
    logger.warn("[{}]: {}", module, strMessage);
  }

  public void trace(String module, String strMessage) {
    logger.trace("[{}]: {}", module, strMessage);
  }

  public void trace(Throwable exception) {
    trace("", exception);
  }

  public void trace(String module, Throwable exception) {
    logger.trace("[{}]: ", module, exception);
  }

  public int getCurrentLogLevel() {
    return LogLevel.ALL.level;
  }

  public boolean isLogLevel(LogLevel level) {
    return true;
  }

  public void addThreadName(String threadName) {}

  public void removeThreadName(String threadName) {}

  public boolean isErrorLogLevel() {
    return logger.isErrorEnabled();
  }

  public boolean isWarnLogLevel() {
    return logger.isWarnEnabled();
  }

  public boolean isInfoLogLevel() {
    return logger.isInfoEnabled();
  }

  public boolean isDebugLogLevel() {
    return logger.isDebugEnabled();
  }
}
