package com.diameter.commons;

public enum StackAlertSeverity {
  CLEAR(0),
  CRITICAL(1),
  ERROR(2),
  WARN(3),
  INFO(4);
  
  public final int code;
  
  StackAlertSeverity(int level) {
    this.code = level;
  }
}
