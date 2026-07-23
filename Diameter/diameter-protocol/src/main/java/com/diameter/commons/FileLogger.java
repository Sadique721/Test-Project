package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLogger implements ILogger {
	private static final Logger logger = LoggerFactory.getLogger("CORE");

	private ThreadLocal<SimpleDateFormat> sdfLocal = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss,SSS");
		}
	};

	public void error(String module, String strMessage) {
		logger.error("[{}]: {}", module, strMessage);
	}

	public void error(String module, String strMessage, Exception e) {
		// Pass the throwable as the trailing argument (no matching '{}') so SLF4J renders
		// the full stack trace, including class / file / line numbers.
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
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		logger.trace("[{}]: {}", module, stringWriter);
	}

	protected String dateToString(Date date) {
		return (this.sdfLocal.get()).format(date);
	}

	public int getCurrentLogLevel() {
		return LogLevel.ALL.level;
	}

	public boolean isLogLevel(LogLevel level) {
		return true;
	}

	public void addThreadName(String threadName) {
	}

	public void removeThreadName(String threadName) {
	}

	public boolean isErrorLogLevel() {
		return true;
	}

	public boolean isWarnLogLevel() {
		return true;
	}

	public boolean isInfoLogLevel() {
		return true;
	}

	public boolean isDebugLogLevel() {
		return true;
	}
}
