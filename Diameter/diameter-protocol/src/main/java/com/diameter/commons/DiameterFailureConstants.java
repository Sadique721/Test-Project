package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum DiameterFailureConstants {
  DROP(1, "Drop"),
  FAILOVER(2, "Failover"),
  REDIRECT(3, "Redirect"),
  PASSTHROUGH(4, "Passthrough"),
  TRANSLATE(5, "Translate"),
  RECORD(6, "Record");
  
  public final int failureAction;
  
  public final String failureActionStr;
  
  private static final Map<Integer, DiameterFailureConstants> map;
  
  protected static final DiameterFailureConstants[] DIAMETER_FAILURE_CONSTANTS;
  
  static {
    DIAMETER_FAILURE_CONSTANTS = values();
    map = new HashMap<>();
    for (DiameterFailureConstants type : DIAMETER_FAILURE_CONSTANTS)
      map.put(Integer.valueOf(type.failureAction), type); 
  }
  
  DiameterFailureConstants(int failureAction, String failureActionStr) {
    this.failureAction = failureAction;
    this.failureActionStr = failureActionStr;
  }
  
  public static DiameterFailureConstants fromDiameterFailureAction(int failureAction) {
    return map.get(Integer.valueOf(failureAction));
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static String getFailureString(int action) {
    DiameterFailureConstants failureAction = map.get(Integer.valueOf(action));
    if (failureAction != null)
      return failureAction.failureActionStr; 
    return "INVALID ACTION";
  }
}
