package com.diameter.commons;

import java.io.Serializable;

public class ConcurrentCounter implements Cloneable, Serializable {
  private static final long serialVersionUID = 1L;
  
  private transient Object counterLock = new Object();
  
  private long counter;
  
  private long maxVal;
  
  private long minVal;
  
  public ConcurrentCounter() {
    this(0L, Long.MAX_VALUE);
  }
  
  public ConcurrentCounter(long minVal, long maxVal) {
    this.maxVal = maxVal;
    this.minVal = minVal;
    this.counter = minVal;
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  public void incrementMaxVal() {
    synchronized (this.counterLock) {
      this.maxVal++;
    } 
  }
  
  public long incrementCounter() {
    long currentVal;
    synchronized (this.counterLock) {
      if (this.counter > this.maxVal)
        this.counter = this.minVal; 
      currentVal = this.counter++;
    } 
    return currentVal;
  }
  
  public void decrementMaxVal() {
    synchronized (this.counterLock) {
      this.maxVal--;
    } 
  }
  
  public long decrementCounter() {
    long currentVal;
    synchronized (this.counterLock) {
      if (this.counter < this.minVal)
        this.counter = this.maxVal; 
      currentVal = this.counter--;
    } 
    return currentVal;
  }
  
  public void setCounter(long counter) {
    synchronized (this.counterLock) {
      this.counter = counter;
    } 
  }
  
  public long getMinValue() {
    return this.minVal;
  }
  
  public long getMaxValue() {
    return this.maxVal;
  }
  
  public long getCounter() {
    return this.counter;
  }
}