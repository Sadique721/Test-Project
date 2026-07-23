package com.diameter.commons;

public abstract class ESCommunicatorGroupImpl<T extends ESCommunicator> implements ESCommunicatorGroup<T> {
  private static final boolean ALIVE = true;
  
  private LoadBalancer<T> primaryGroup;
  
  private T secondaryCommunicator;
  
  private boolean aliveness;
  
  private int groupSize = 0;
  
  public ESCommunicatorGroupImpl() {
    this(LoadBalancerType.ROUND_ROBIN);
  }
  
  public ESCommunicatorGroupImpl(LoadBalancerType loadBalancerType) {
    this(loadBalancerType, true);
  }
  
  @SuppressWarnings("unchecked")
public ESCommunicatorGroupImpl(LoadBalancerType loadBalancerType, boolean aliveness) {
    this.aliveness = aliveness;
    if (loadBalancerType == LoadBalancerType.FAIL_OVER) {
      this.primaryGroup = (LoadBalancer<T>)new FailoverLoadBalancer();
    } else if (loadBalancerType == LoadBalancerType.SWITCH_OVER) {
      this.primaryGroup = (LoadBalancer<T>)new SwitchOverLoadBalancer();
    } else {
      this.primaryGroup = (LoadBalancer<T>)new RoundRobinLoadBalancer(aliveness);
    } 
  }
  
  protected T getCommunicator() {
    T t = null;
    ESCommunicator eSCommunicator = this.primaryGroup.getCommunicator();
    if (eSCommunicator == null && isSecondaryCommAlive())
      t = this.secondaryCommunicator; 
    return t;
  }
  
  protected T getCommunicator(int serverInstanceSize) {
    ESCommunicator eSCommunicator = null;
    T communicator = null;
    for (int index = 0; index < serverInstanceSize; ) {
      eSCommunicator = this.primaryGroup.getCommunicator();
      if ((eSCommunicator == null || !eSCommunicator.isAlive()) && index != serverInstanceSize)
        index++; 
    } 
    return (T)eSCommunicator;
  }
  
  protected T getSecondaryCommunicator(T... ignoreCommunicators) {
    if (this.secondaryCommunicator == null || !isSecondaryCommAlive())
      return (T)this.primaryGroup.getSecondaryCommunicator((T[])ignoreCommunicators); 
    if (ignoreCommunicators == null)
      return this.secondaryCommunicator; 
    boolean communictorFound = false;
    for (int j = 0; j < ignoreCommunicators.length; j++) {
      if (ignoreCommunicators[j] == this.secondaryCommunicator) {
        communictorFound = true;
        break;
      } 
    } 
    if (!communictorFound)
      return this.secondaryCommunicator; 
    return null;
  }
  
  public void addCommunicator(T esCommunicator, int weightage) {
    if (esCommunicator != null) {
      if (weightage == 0) {
        this.secondaryCommunicator = esCommunicator;
      } else if (weightage > 0) {
        this.primaryGroup.addCommunicator((T)esCommunicator, weightage);
      } 
      this.groupSize++;
    } 
  }
  
  public void addCommunicator(T esCommunicator) {
    addCommunicator(esCommunicator, 1);
  }
  
  private boolean isSecondaryCommAlive() {
    return (!this.aliveness || (this.secondaryCommunicator != null && this.secondaryCommunicator.isAlive()));
  }
  
  public boolean isAlive() {
    return (this.primaryGroup.isAlive() || isSecondaryCommAlive());
  }
  
  protected int getGroupSize() {
    return this.groupSize;
  }
}
