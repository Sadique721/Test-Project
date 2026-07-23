package com.diameter.commons;

import org.springframework.scheduling.TaskScheduler;

import com.diameter.stack.DiameterStack;

public abstract class DiameterScriptContext {
  public abstract PeerData getPeerData(String paramString);
  
  public abstract TaskScheduler getTaskSchedular();
  
  public abstract DiameterStack.DiameterStackContext getStackContext();
}
