package com.diameter.commons;

public class ThreadNameBasedReslover implements LogManager.LogKeyResolver {
  private final int keyLength;
  
  @VisibleForTesting
  ThreadNameResolver nameResolver = new ThreadNameResolver();
  
  public ThreadNameBasedReslover(int keyLength) {
    Preconditions.checkArgument((keyLength > 0), "key length cannot be <= 0");
    this.keyLength = keyLength;
  }
  
  public String resloveKey() {
    String threadName = this.nameResolver.resloveName();
    int threadNameLength = threadName.length();
    return (threadNameLength < this.keyLength) ? "DEFAULT_LOGGER" : threadName
      .substring(0, this.keyLength);
  }
  
  @VisibleForTesting
  static class ThreadNameResolver {
    public String resloveName() {
      return Thread.currentThread().getName();
    }
  }
}
