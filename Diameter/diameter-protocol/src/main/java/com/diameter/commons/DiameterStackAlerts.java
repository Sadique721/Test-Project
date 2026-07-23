package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum DiameterStackAlerts implements IStackAlertEnum {
  DIAMETER_STACK_UP("DMS000001"),
  DIAMETER_STACK_DOWN("DMS000002"),
  DIAMETER_PEER_UP("DMS000003"),
  DIAMETER_PEER_DOWN("DMS000004"),
  DIAMETER_HIGH_RESPONSE_TIME("DMS000005"),
  CCSTATISTICSNOTFOUND("DMS000006"),
  BASESTATISTICSNOTFOUND("DMS000007"),
  PEER_CONNECTION_REJECTED("DMS000008"),
  DIAMETER_PEER_HIGH_RESPONSE_TIME("DMS000009");
  
  public final String alertId;
  
  private static final Map<String, DiameterStackAlerts> map;
  
  private static final DiameterStackAlerts[] DIAMETER_STACK_ALERTS;
  
  static {
    DIAMETER_STACK_ALERTS = values();
    map = new HashMap<>();
    for (DiameterStackAlerts type : DIAMETER_STACK_ALERTS)
      map.put(type.alertId, type); 
  }
  
  DiameterStackAlerts(String oid) {
    this.alertId = oid;
  }
  
  public static DiameterStackAlerts fromAlertId(String alertId) {
    return map.get(alertId);
  }
  
  public static boolean isValid(String value) {
    return map.containsKey(value);
  }
  
  public String id() {
    return this.alertId;
  }
}
