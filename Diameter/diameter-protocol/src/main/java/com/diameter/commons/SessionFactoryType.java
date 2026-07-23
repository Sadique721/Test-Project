package com.diameter.commons;

public enum SessionFactoryType {
  INMEMORY(1),
  HAZELCAST(2),
  NULLSESSION(3);
  
  private int factoryId;
  
  SessionFactoryType(int factoryId) {
    this.factoryId = factoryId;
  }
}
