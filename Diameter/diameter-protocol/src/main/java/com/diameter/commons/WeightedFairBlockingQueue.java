package com.diameter.commons;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WeightedFairBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
  private static final boolean FAIR = true;
  
  private static final String MODULE = "WEIGHTED-FAIR-BLOCKING-QUEUE";
  
  private Optional<BoundedQueue<E>>[] priortyQueues;
  
  private final ReentrantLock lock;
  
  private final Condition empty;
  
  private final Condition full;
  
  private int queuePointer;
  
  private int times;
  
  private int capacity;
  
  private FairnessPolicy<E> fairnessPolicy;
  
  public WeightedFairBlockingQueue(FairnessPolicy<E> fairnessPolicy) {
    this(2147483647, fairnessPolicy);
  }
  
  public WeightedFairBlockingQueue(int capacity, FairnessPolicy<E> fairness) {
    if (capacity <= 0)
      throw new IllegalArgumentException("Capacity can not be zero"); 
    this.fairnessPolicy = fairness;
    this.capacity = capacity;
    int[] individualCapacities = distributeCapacity(capacity, fairness.range());
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("WEIGHTED-FAIR-BLOCKING-QUEUE", "Total Capacity: " + capacity + " Capacity Distribution: " + 
          Arrays.toString(individualCapacities)); 
    this.priortyQueues = (Optional<BoundedQueue<E>>[])new Optional[individualCapacities.length];
    for (int i = 0; i < individualCapacities.length; i++) {
      BoundedQueue<E> boundedQueue = null;
      if (individualCapacities[i] > 0)
        boundedQueue = new LinkedBoundedQueue<>(individualCapacities[i]); 
      this.priortyQueues[i] = Optional.of(boundedQueue);
    } 
    this.lock = new ReentrantLock(true);
    this.empty = this.lock.newCondition();
    this.full = this.lock.newCondition();
    changeQueuePointers();
  }
  
  private int[] distributeCapacity(int capacity, Range range) {
    int[] distributesCapacities = new int[3];
    switch (range) {
      case LOW_NORMAL:
        distributesCapacities[0] = capacity / 4;
        distributesCapacities[1] = capacity - distributesCapacities[0];
        distributesCapacities[2] = 0;
        return distributesCapacities;
      case LOW_TO_HIGH:
        distributesCapacities[0] = capacity / 5;
        distributesCapacities[1] = capacity - distributesCapacities[0] - distributesCapacities[0];
        distributesCapacities[2] = distributesCapacities[0];
        return distributesCapacities;
      case NORMAL_HIGH:
        distributesCapacities[0] = 0;
        distributesCapacities[2] = capacity / 4;
        distributesCapacities[1] = capacity - distributesCapacities[2];
        return distributesCapacities;
    } 
    throw new IllegalArgumentException();
  }
  
  public E poll() {
    BoundedQueue<E> boundedQueue = null;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (size() == 0)
        return null; 
      boundedQueue = selectQueueForFetch();
      if (boundedQueue == null)
        return null; 
      E e = boundedQueue.poll();
      if (e != null)
        this.full.signal(); 
      return e;
    } finally {
      lock.unlock();
    } 
  }
  
  private BoundedQueue<E> selectQueueForFetch() {
    int count = this.priortyQueues.length;
    do {
      int queuePointer = this.queuePointer;
      if (this.priortyQueues[queuePointer].isPresent() && ((BoundedQueue)this.priortyQueues[queuePointer]
        .get()).size() > 0) {
        updateQueuePointers();
        return (BoundedQueue<E>)this.priortyQueues[queuePointer].get();
      } 
      changeQueuePointers();
    } while (--count > 0);
    return null;
  }
  
  private void changeQueuePointers() {
    this.times = 0;
    this.queuePointer--;
    if (this.queuePointer < 0)
      this.queuePointer = this.priortyQueues.length - 1; 
  }
  
  private void updateQueuePointers() {
    this.times++;
    if (this.times > this.queuePointer)
      changeQueuePointers(); 
  }
  
  public E peek() {
    BoundedQueue<E> boundedQueue = null;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      boundedQueue = selectQueueForFetch();
      if (boundedQueue == null)
        return null; 
      return boundedQueue.peek();
    } finally {
      lock.unlock();
    } 
  }
  
  public boolean offer(E e) {
    if (e == null)
      throw new NullPointerException(); 
    Queue<E> boundedQueue = null;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      boundedQueue = selectQueueForInsert(e);
      if (boundedQueue == null)
        return false; 
      if (boundedQueue.offer(e)) {
        this.empty.signal();
        return true;
      } 
      return false;
    } finally {
      lock.unlock();
    } 
  }
  
  private BoundedQueue<E> selectQueueForInsert(E e) {
    int index = this.fairnessPolicy.prioritize(e) - 1;
    while (true) {
      if (!this.priortyQueues[index].isPresent())
        throw new IllegalPriorityException("Queue is not allocated for Priority: " + (index + 1) + ", Kindly verify Capacity or Range provided."); 
      if (((BoundedQueue)this.priortyQueues[index].get()).remainingCapacity() > 0)
        return (BoundedQueue<E>)this.priortyQueues[index].get(); 
      index--;
      if (index < 0)
        return null; 
    } 
  }
  
  public void put(E e) throws InterruptedException {
    if (e == null)
      throw new NullPointerException(); 
    BoundedQueue<E> boundedQueue = null;
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      while ((boundedQueue = selectQueueForInsert(e)) == null) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("WEIGHTED-FAIR-BLOCKING-QUEUE", "Waiting for Queue to get empty"); 
        this.full.await();
      } 
      if (boundedQueue.offer(e))
        this.empty.signal(); 
    } finally {
      lock.unlock();
    } 
  }
  
  public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
    if (e == null)
      throw new NullPointerException(); 
    long nanos = unit.toNanos(timeout);
    BoundedQueue<E> boundedQueue = null;
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      while ((boundedQueue = selectQueueForInsert(e)) == null) {
        if (nanos <= 0L)
          return false; 
        nanos = this.full.awaitNanos(nanos);
      } 
      if (boundedQueue.offer(e)) {
        this.empty.signal();
        return true;
      } 
      return false;
    } finally {
      lock.unlock();
    } 
  }
  
  public E take() throws InterruptedException {
    BoundedQueue<E> boundedQueue = null;
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      while (size() == 0) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("WEIGHTED-FAIR-BLOCKING-QUEUE", "Waiting for Elements"); 
        this.empty.await();
      } 
      boundedQueue = selectQueueForFetch();
      E e = null;
      if (Objects.nonNull(boundedQueue))
        e = boundedQueue.poll(); 
      this.full.signal();
      return e;
    } finally {
      lock.unlock();
    } 
  }
  
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    long nanos = unit.toNanos(timeout);
    BoundedQueue<E> boundedQueue = null;
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      while ((boundedQueue = selectQueueForFetch()) == null) {
        if (nanos <= 0L)
          return null; 
        nanos = this.empty.awaitNanos(nanos);
      } 
      E e = boundedQueue.poll();
      this.full.signal();
      return e;
    } finally {
      lock.unlock();
    } 
  }
  
  public int remainingCapacity() {
    return this.capacity - size();
  }
  
  public int drainTo(Collection<? super E> c) {
    if (c == null)
      throw new NullPointerException(); 
    if (c == this)
      throw new IllegalArgumentException(); 
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int n = 0;
      int queuePointer = this.priortyQueues.length;
      while (--queuePointer >= 0) {
        int times = 0;
        while (times <= queuePointer && 
          this.priortyQueues[queuePointer].isPresent()) {
          E e = ((BoundedQueue<E>)this.priortyQueues[queuePointer].get()).poll();
          if (e == null)
            break; 
          c.add(e);
          times++;
          n++;
        } 
      } 
      if (n > 0)
        this.full.signalAll(); 
      return n;
    } finally {
      lock.unlock();
    } 
  }
  
  public int drainTo(Collection<? super E> c, int maxElements) {
    if (c == null)
      throw new NullPointerException(); 
    if (c == this)
      throw new IllegalArgumentException(); 
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int n = 0;
      int queuePointer = this.priortyQueues.length;
      while (--queuePointer >= 0) {
        int times = 0;
        while (times <= queuePointer && n < maxElements) {
          if (!this.priortyQueues[queuePointer].isPresent())
            break; 
          E e = ((BoundedQueue<E>)this.priortyQueues[queuePointer].get()).poll();
          if (e == null)
            break; 
          c.add(e);
          times++;
          n++;
        } 
      } 
      if (n > 0)
        this.full.signalAll(); 
      return n;
    } finally {
      lock.unlock();
    } 
  }
  
  public Iterator<E> iterator() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Optional[] arrayOfOptional = new Optional[this.priortyQueues.length];
      for (int i = 0; i < this.priortyQueues.length; i++) {
        Iterator<E> iterator = null;
        if (this.priortyQueues[i].isPresent())
          iterator = ((BoundedQueue<E>)this.priortyQueues[i].get()).iterator(); 
        arrayOfOptional[i] = Optional.of(iterator);
      } 
      return new Itr((Optional<Iterator<E>>[])arrayOfOptional);
    } finally {
      lock.unlock();
    } 
  }
  
  private class Itr implements Iterator<E> {
    private Optional<Iterator<E>>[] iterators;
    
    private int iteratorPonitor;
    
    private int times;
    
    public Itr(Optional<Iterator<E>>[] iterators) {
      this.iterators = iterators;
      this.iteratorPonitor = iterators.length - 1;
      this.times = 0;
    }
    
    public boolean hasNext() {
      ReentrantLock lock = WeightedFairBlockingQueue.this.lock;
      lock.lock();
      try {
        int count = WeightedFairBlockingQueue.this.priortyQueues.length;
        while (true) {
          if (this.iterators[this.iteratorPonitor].isPresent() && ((Iterator)this.iterators[this.iteratorPonitor]
            .get()).hasNext())
            return true; 
          changeQueuePointers();
          if (--count <= 0)
            return false; 
        } 
      } finally {
        lock.unlock();
      } 
    }
    
    private void changeQueuePointers() {
      this.times = 0;
      this.iteratorPonitor--;
      if (this.iteratorPonitor < 0)
        this.iteratorPonitor = this.iterators.length - 1; 
    }
    
    private void updateQueuePointers() {
      this.times++;
      if (this.times > this.iteratorPonitor)
        changeQueuePointers(); 
    }
    
    public E next() {
      ReentrantLock lock = WeightedFairBlockingQueue.this.lock;
      lock.lock();
      try {
        E e = ((Iterator<E>)this.iterators[this.iteratorPonitor].get()).next();
        updateQueuePointers();
        return e;
      } finally {
        lock.unlock();
      } 
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  public int size() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int size = 0;
      int i;
      for (i = 0; i < this.priortyQueues.length; i++)
        size += this.priortyQueues[i].isPresent() ? ((BoundedQueue)this.priortyQueues[i].get()).size() : 0; 
      i = size;
      return i;
    } finally {
      lock.unlock();
    } 
  }
  
  public enum Range {
    LOW_TO_HIGH, LOW_NORMAL, NORMAL_HIGH;
  }
}
