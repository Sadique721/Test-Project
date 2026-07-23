package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum PluginType {
  UNIVERSAL_AUTH(1, "UNIVERSAL_AUTH_PLUGIN"),
  UNIVERSAL_ACCT(2, "UNIVERSAL_ACCT_PLUGIN"),
  RADIUS_GROOVY_PLUGIN(3, "RADIUS_GROOVY_PLUGIN"),
  UNIVERSAL_DIAMETER_PLUGIN(4, "UNIVERSAL_DIAMETER_PLUGIN"),
  DIAMETER_GROOVY_PLUGIN(5, "DIAMETER_GROOVY_PLUGIN"),
  RADIUS_TRANSACTION_LOGGER(6, "RADIUS_TRANSACTION_LOGGER"),
  DIAMETER_TRANSACTION_LOGGER(7, "DIAMETER_TRANSACTION_LOGGER"),
  QUOTA_MANAGEMENT(8, "QUOTA_MANAGEMENT"),
  USER_STATISTICS_POST_AUTH_PLUGIN(9, "USER_STATISTICS_POST_AUTH_PLUGIN");
  
  private int typeId;
  
  private String typeName;
  
  private static final Map<Integer, PluginType> map;
  
  protected static final PluginType[] PLUGIN_TYPES;
  
  PluginType(int typeId, String typeName) {
    this.typeId = typeId;
    this.typeName = typeName;
  }
  
  public String getTypeName() {
    return this.typeName;
  }
  
  static {
    PLUGIN_TYPES = values();
    map = new HashMap<>();
    for (PluginType type : PLUGIN_TYPES)
      map.put(Integer.valueOf(type.typeId), type); 
  }
  
  public static PluginType from(int name) {
    return map.get(Integer.valueOf(name));
  }
}
