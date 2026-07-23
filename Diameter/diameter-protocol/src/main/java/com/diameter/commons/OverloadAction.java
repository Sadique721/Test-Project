package com.diameter.commons;

public enum OverloadAction {
  DROP("DROP"),
  REJECT("REJECT");
  
  public final String val;
  
  OverloadAction(String val) {
    this.val = val;
  }
  
  public static OverloadAction fromVal(String val) {
    if (REJECT.val.equalsIgnoreCase(val))
      return REJECT; 
    if (DROP.val.equalsIgnoreCase(val))
      return DROP; 
    return null;
  }
}
