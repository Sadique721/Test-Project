package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum RowStatus {
  ACTIVE(1, "Active"),
  NO_IN_SERVICE(2, "NotInService"),
  NOT_READY(3, "NotReady"),
  CREATE_AND_GO(4, "CreateAndGo"),
  CREATE_AND_WAIT(5, "CreateAndWait"),
  DESTROY(6, "Destroy");
  
  public final int code;
  
  public final String statusStr;
  
  private static final Map<Integer, RowStatus> map;
  
  private static final RowStatus[] ROW_STATUS_LIST;
  
  static {
    ROW_STATUS_LIST = values();
    map = new HashMap<>();
    for (RowStatus type : ROW_STATUS_LIST)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  RowStatus(int code, String statusStr) {
    this.code = code;
    this.statusStr = statusStr;
  }
  
  public static RowStatus fromRowStatusTypeCode(int statusTypeCode) {
    return map.get(Integer.valueOf(statusTypeCode));
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static String getRowStatusTypeString(int statusTypeCode) {
    RowStatus rowStatus = map.get(Integer.valueOf(statusTypeCode));
    if (rowStatus != null)
      return rowStatus.statusStr; 
    return "INVALID ROW-STATUS TYPE";
  }
}
