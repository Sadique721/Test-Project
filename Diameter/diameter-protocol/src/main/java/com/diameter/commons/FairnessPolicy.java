package com.diameter.commons;

public interface FairnessPolicy<E> {
  int prioritize(E paramE);
  
  WeightedFairBlockingQueue.Range range();
}
