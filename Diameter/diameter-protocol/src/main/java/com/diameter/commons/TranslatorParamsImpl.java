package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public class TranslatorParamsImpl implements TranslatorParams {
  private Map<String, Object> paramsMap = new HashMap<>();
  
  public TranslatorParamsImpl(Object fromPacketType, Object toPacketType) {
    this.paramsMap.put("FROM_PACKET", fromPacketType);
    this.paramsMap.put("TO_PACKET", toPacketType);
  }
  
  public TranslatorParamsImpl(Object fromPacketType, Object toPacketType, Object sourceRequest, Object destinationRequest) {
    this.paramsMap.put("FROM_PACKET", fromPacketType);
    this.paramsMap.put("TO_PACKET", toPacketType);
    this.paramsMap.put("${SRCREQ}", sourceRequest);
    this.paramsMap.put("${DSTREQ}", destinationRequest);
  }
  
  public Object getParam(String key) {
    return this.paramsMap.get(key);
  }
  
  public void setParam(String key, Object value) {
    this.paramsMap.put(key, value);
  }
}
