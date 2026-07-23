package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum PeerDataCode implements IStateTransitionDataCode {
  DIAMETER_RECEIVED_PACKET(0),
  DIAMETER_PACKET_TO_SEND(1),
  CONNECTION(2),
  PEER_EVENT(3),
  PEER_STATE(4),
  USER_SESSION(6),
  DISCONNECT_REASON(7),
  RESPONSE_LISTENER(8);
  
  private static final Map<Integer, PeerDataCode> map;
  
  protected static final PeerDataCode[] PEER_DATA_CODES;
  
  public final int code;
  
  static {
    PEER_DATA_CODES = values();
    map = new HashMap<>();
    for (PeerDataCode type : PEER_DATA_CODES)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  PeerDataCode(int code) {
    this.code = code;
  }
  
  public int getCode() {
    return this.code;
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static PeerDataCode fromCode(int value) {
    return map.get(Integer.valueOf(value));
  }
  
  public int getStateTransitionDataCode() {
    return ordinal();
  }
}
