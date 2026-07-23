package com.diameter.commons;

public abstract class BasePCBState extends StateBase {
  protected PCBActionExecutor actionExecutor;
  
  public BasePCBState(PCBActionExecutor actionExecutor) {
    this.actionExecutor = actionExecutor;
  }
}

