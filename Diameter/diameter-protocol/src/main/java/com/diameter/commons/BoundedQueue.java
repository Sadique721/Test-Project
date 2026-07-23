package com.diameter.commons;

import java.util.Queue;

public interface BoundedQueue<E> extends Queue<E> {
  int remainingCapacity();
}
