package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum DisconnectionCause {
  REBOOTING(0),
  BUSY(1),
  DO_NOT_WANT_TO_TALK_TO_YOU(2);
  
  private static final Map<Integer, DisconnectionCause> map;
  
  protected static final DisconnectionCause[] DISCONNECTION_CAUSES;
  
  public final int code;
  
  static {
    DISCONNECTION_CAUSES = values();
    map = new HashMap<>();
    for (DisconnectionCause type : DISCONNECTION_CAUSES)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  DisconnectionCause(int code) {
    this.code = code;
  }
  
  public int getCode() {
    return this.code;
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static DisconnectionCause fromCode(int value) {
    return map.get(Integer.valueOf(value));
  }
}
