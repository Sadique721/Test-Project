package com.diameter.commons;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HopByHopPool {
  private static AtomicInteger nextNumber = new AtomicInteger();
  
  static {
	  Random random = new Random();
    try {
      nextNumber.set(random.nextInt((int)(System.currentTimeMillis() / 1000L)));
    } catch (Exception e) {
      nextNumber.set(random.nextInt());
    } 
  }
  
  public static int get() {
    nextNumber.compareAndSet(2147483647, 0);
    return nextNumber.incrementAndGet();
  }
}
