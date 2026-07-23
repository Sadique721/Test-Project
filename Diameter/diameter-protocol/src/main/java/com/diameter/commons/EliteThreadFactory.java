package com.diameter.commons;

import java.util.concurrent.ThreadFactory;

public class EliteThreadFactory implements ThreadFactory {
  private final String threadKey;
  
  private int threadPriority;
  
  private long threadCounter;
  
  private final String threadNamePrefix;
  
  private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
  
  public EliteThreadFactory(String threadKey, String threadNamePrefix, int priority) {
    this.threadKey = (String)Preconditions.checkNotNull(threadKey, "threadKey is null");
    this.threadNamePrefix = (String)Preconditions.checkNotNull(threadNamePrefix, "threadNamePrefix is null");
    if (priority >= 1 && priority <= 10) {
      this.threadPriority = priority;
    } else {
      this.threadPriority = 5;
    } 
  }
  
  public EliteThreadFactory(String threadKey, String threadNamePrefix, int priority, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    this(threadKey, threadNamePrefix, priority);
    this.uncaughtExceptionHandler = (Thread.UncaughtExceptionHandler)Preconditions.checkNotNull(uncaughtExceptionHandler, "uncaughtExceptionHandler is null");
  }
  
  public Thread newThread(Runnable r) {
    Thread thread = new EliteThread(r, formName(), this.threadKey);
    if (this.threadPriority != 5)
      thread.setPriority(this.threadPriority); 
    thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
    return thread;
  }
  
  private String formName() {
    return this.threadNamePrefix + "-" + ++this.threadCounter;
  }
  
  public static class EliteThread extends Thread {
    private final String key;
    
    public EliteThread(Runnable r, String name, String key) {
      super(r, name);
      this.key = (String)Preconditions.checkNotNull(key, "key is null");
    }
    
    public String getKey() {
      return this.key;
    }
  }
}
