package com.diameter.commons;

public class SwitchOverLoadBalancer<T extends ESCommunicator> extends RoundRobinLoadBalancer<T> {
  public SwitchOverLoadBalancer() {
    super(true);
  }
  
  public SwitchOverLoadBalancer(boolean checkAlive) {
    super(checkAlive);
  }
  
  public T getCommunicator() {
    ESCommunicator eSCommunicator = null;
    T esCommunicator = null;
    if (this.communicatorList.size() > 0)
      try {
        eSCommunicator = (ESCommunicator)this.communicatorList.get(0);
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {} 
    return (T)eSCommunicator;
  }
  
  public T getSecondaryCommunicator(T... ignoreCommunicators) {
    return null;
  }
}
