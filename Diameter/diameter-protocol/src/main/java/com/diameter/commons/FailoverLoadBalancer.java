package com.diameter.commons;

import java.util.LinkedList;
import java.util.Objects;

public class FailoverLoadBalancer<T extends ESCommunicator> implements LoadBalancer<T> {
  private Object communicatorListLock;
  
  private LinkedList<OrderedESCommunicator> aliveCommunicatorList;
  
  private int nextOrderId = 1;
  
  public FailoverLoadBalancer() {
    this.aliveCommunicatorList = new LinkedList<>();
    this.communicatorListLock = new Object();
  }
  
  public T getCommunicator() {
    T esCommunicator = null;
    int size = this.aliveCommunicatorList.size();
    for (int i = 0; i < size; i++) {
      if (this.aliveCommunicatorList.get(i) != null && ((OrderedESCommunicator)this.aliveCommunicatorList.get(i)).isAlive()) {
        esCommunicator = ((OrderedESCommunicator)this.aliveCommunicatorList.get(i)).get();
        return esCommunicator;
      } 
    } 
    return esCommunicator;
  }
  
  public T getSecondaryCommunicator(T... ignoreCommunicators) {
    if (ignoreCommunicators == null && this.aliveCommunicatorList.size() > 0)
      return ((OrderedESCommunicator)this.aliveCommunicatorList.get(0)).get(); 
    for (int i = 0; i < this.aliveCommunicatorList.size(); i++) {
      try {
        boolean communicatorFound = false;
        if (Objects.nonNull(ignoreCommunicators))
          for (int j = 0; j < ignoreCommunicators.length; j++) {
            if (ignoreCommunicators[j] == ((OrderedESCommunicator)this.aliveCommunicatorList.get(i)).get()) {
              communicatorFound = true;
              break;
            } 
          }  
        if (!communicatorFound && ((OrderedESCommunicator)this.aliveCommunicatorList.get(i)).isAlive())
          return ((OrderedESCommunicator)this.aliveCommunicatorList.get(i)).get(); 
      } catch (ArrayIndexOutOfBoundsException e) {
        return null;
      } catch (IndexOutOfBoundsException e) {
        return null;
      } 
    } 
    return null;
  }
  
  private void addCommunicator(T esCommunicator) {
    final OrderedESCommunicator orderedCommunicator = new OrderedESCommunicator(esCommunicator, this.nextOrderId++);
    orderedCommunicator.addESIEventListener(new ESIEventListener<ESCommunicator>() {
          public void alive(ESCommunicator esCommunicator) {
            addInternal(orderedCommunicator);
          }
          
          public void dead(ESCommunicator esCommunicator) {
            removeInternal(orderedCommunicator);
          }
          
          private void addInternal(FailoverLoadBalancer<T>.OrderedESCommunicator communicator) {
            synchronized (FailoverLoadBalancer.this.communicatorListLock) {
              //FailoverLoadBalancer.access$100(FailoverLoadBalancer.this).add(communicator);
              //Collections.sort(FailoverLoadBalancer.access$100(FailoverLoadBalancer.this));
            } 
          }
          
          private void removeInternal(FailoverLoadBalancer<T>.OrderedESCommunicator communicator) {
            synchronized (FailoverLoadBalancer.this.communicatorListLock) {
              //FailoverLoadBalancer.access$100(FailoverLoadBalancer.this).remove(communicator);
            } 
          }
        });
    if (!esCommunicator.isAlive())
      return; 
    synchronized (this.communicatorListLock) {
      this.aliveCommunicatorList.add(orderedCommunicator);
    } 
  }
  
  public void addCommunicator(T esCommunicator, int weightage) {
    addCommunicator(esCommunicator);
  }
  
  public boolean isAlive() {
    return !this.aliveCommunicatorList.isEmpty();
  }
  
  private class OrderedESCommunicator implements ESCommunicator, Comparable<OrderedESCommunicator> {
    private final T externalSystem;
    
    private final Integer orderNo;
    
    public OrderedESCommunicator(T externalSystem, int orderNo) {
      this.externalSystem = externalSystem;
      this.orderNo = Integer.valueOf(orderNo);
    }
    
    public void reInit() throws InitializationFailedException {
      this.externalSystem.reInit();
    }
    
    public void init() throws InitializationFailedException {
      this.externalSystem.init();
    }
    
    public boolean isAlive() {
      return this.externalSystem.isAlive();
    }
    
    public void scan() {
      this.externalSystem.scan();
    }
    
    public void addESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
      this.externalSystem.addESIEventListener(eventListener);
    }
    
    public void removeESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
      this.externalSystem.removeESIEventListener(eventListener);
    }
    
    public void stop() {
      this.externalSystem.stop();
    }
    
    public String getName() {
      return this.externalSystem.getName();
    }
    
    public String getTypeName() {
      return this.externalSystem.getTypeName();
    }
    
    public ESIStatistics getStatistics() {
      return this.externalSystem.getStatistics();
    }
    
    public void registerAlertListener(AlertListener alertListener) {
      this.externalSystem.registerAlertListener(alertListener);
    }
    
    public T get() {
      return this.externalSystem;
    }
    
    public int compareTo(OrderedESCommunicator that) {
      return this.orderNo.compareTo(that.orderNo);
    }
  }
}
