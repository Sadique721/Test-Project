package com.diameter.commons;

public interface DiameterFailoverConfiguration {
  DiameterFailureConstants getFailoverAction();
  
  String getFailoverArguments();
  
  String getErrorCodes();
}
