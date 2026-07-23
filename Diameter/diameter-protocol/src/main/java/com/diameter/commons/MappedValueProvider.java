package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public abstract class MappedValueProvider implements ValueProvider {
  private Map<String, Object> parameters = new HashMap<>();
  
  public MappedValueProvider(Map<String, Object> parameters) {
    this.parameters = parameters;
  }
  
  public Object getValue(String key) {
    return this.parameters.get(key);
  }
  
  public void setValue(String key, Object value) {
    this.parameters.put(key, value);
  }
}
