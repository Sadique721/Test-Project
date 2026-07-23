package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public enum RoutingActions {
  LOCAL(1, "Local"),
  RELAY(2, "Relay"),
  PROXY(3, "Proxy"),
  REDIRECT(4, "Redirect"),
  VIRTUAL(5, "Virtual");
  
  public final int routingAction;
  
  public final String routingActionStr;
  
  private static final Map<Integer, RoutingActions> actionToRoutingAction;
  
  private static final Map<String, RoutingActions> actionStrToRoutingAction;
  
  static {
    actionToRoutingAction = new HashMap<>();
    for (RoutingActions type : values())
      actionToRoutingAction.put(Integer.valueOf(type.routingAction), type); 
    actionStrToRoutingAction = new HashMap<>();
    for (RoutingActions type : values())
      actionStrToRoutingAction.put(type.routingActionStr, type); 
  }
  
  RoutingActions(int routingAction, String routingActionStr) {
    this.routingAction = routingAction;
    this.routingActionStr = routingActionStr;
  }
  
  @Nonnull
  public static RoutingActions fromRoutingAction(int routingAction) {
    RoutingActions routingActions = actionToRoutingAction.get(Integer.valueOf(routingAction));
    if (routingActions == null)
      return LOCAL; 
    return routingActions;
  }
  
  @Nonnull
  public static RoutingActions fromRoutingActionString(String routingAction) {
    RoutingActions routingActions = actionStrToRoutingAction.get(routingAction);
    if (routingActions == null)
      return LOCAL; 
    return routingActions;
  }
  
  public static boolean isValid(int value) {
    return actionToRoutingAction.containsKey(Integer.valueOf(value));
  }
  
  public static String getActionString(int action) {
    RoutingActions routingAction = actionToRoutingAction.get(Integer.valueOf(action));
    if (routingAction != null)
      return routingAction.routingActionStr; 
    return "INVALID ACTION";
  }
}
