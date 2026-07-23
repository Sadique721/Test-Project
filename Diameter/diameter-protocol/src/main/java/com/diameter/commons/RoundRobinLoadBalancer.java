package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoundRobinLoadBalancer<T extends ESCommunicator> implements LoadBalancer<T> {
  protected List<T> communicatorList;
  
  private Object communicatorListLock;
  
  private ConcurrentCounter counter;
  
  private ESIEventListener<T> statusListner;
  
  private boolean checkAlive;
  
  public RoundRobinLoadBalancer(boolean checkAlive) {
    this.communicatorList = new ArrayList<>();
    this.communicatorListLock = new Object();
    this.checkAlive = checkAlive;
    this.counter = new ConcurrentCounter(0L, -1L);
    this.statusListner = new ESIEventListener<T>() {
        public void alive(T esCommunicator) {
          RoundRobinLoadBalancer.this.addCommunicator(esCommunicator, 1, false);
        }
        
        public void dead(T esCommunicator) {
          RoundRobinLoadBalancer.this.removeCommunicator(esCommunicator);
        }
      };
  }
  
  public T getCommunicator() {
    try {
      return this.communicatorList.get((int)this.counter.incrementCounter());
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      return null;
    } 
  }
  
  public T getSecondaryCommunicator(T... ignoreCommunicators) {
    if (ignoreCommunicators == null && this.communicatorList.size() > 0)
      return this.communicatorList.get(0); 
    for (int i = 0; i < this.communicatorList.size(); i++) {
      try {
        boolean communicatorFound = false;
        if (Objects.nonNull(ignoreCommunicators))
          for (int j = 0; j < ignoreCommunicators.length; j++) {
            if (ignoreCommunicators[j] == this.communicatorList.get(i)) {
              communicatorFound = true;
              break;
            } 
          }  
        if (!communicatorFound && (
          !this.checkAlive || ((ESCommunicator)this.communicatorList.get(i)).isAlive()))
          return this.communicatorList.get(i); 
      } catch (ArrayIndexOutOfBoundsException e) {
        return null;
      } catch (IndexOutOfBoundsException e) {
        return null;
      } 
    } 
    return null;
  }
  
  public void addCommunicator(T esCommunicator, int weight) {
    addCommunicator(esCommunicator, weight, true);
  }
  
  private void addCommunicator(T esCommunicator, int weight, boolean addListener) {
    synchronized (this.communicatorListLock) {
      for (int i = 0; i < weight; i++) {
        if (!this.checkAlive || esCommunicator.isAlive()) {
          this.communicatorList.add(esCommunicator);
          this.counter.incrementMaxVal();
        } 
        if (this.checkAlive && addListener)
          esCommunicator.addESIEventListener((ESIEventListener<ESCommunicator>) this.statusListner); 
      } 
    } 
  }
  
  private void removeCommunicator(T esCommunicator) {
    synchronized (this.communicatorListLock) {
      if (this.communicatorList.remove(esCommunicator))
        this.counter.decrementMaxVal(); 
    } 
  }
  
  public boolean isAlive() {
    return (this.communicatorList.size() > 0);
  }
}
