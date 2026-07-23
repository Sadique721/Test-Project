package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum StorageTypes {
  OTHER(1, "other"),
  VOLATILE(2, "volatile"),
  NON_VOLATILE(3, "NonVolatile"),
  PARMANENT(4, "permanent"),
  READ_ONLY(5, "readOnly");
  
  public final int code;
  
  public final String storageTypeStr;
  
  private static final Map<Integer, StorageTypes> map;
  
  protected static final StorageTypes[] STORAGE_TYPES;
  
  static {
    STORAGE_TYPES = values();
    map = new HashMap<>();
    for (StorageTypes type : STORAGE_TYPES)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  StorageTypes(int code, String storageTypeStr) {
    this.code = code;
    this.storageTypeStr = storageTypeStr;
  }
  
  public int getRoutingAction() {
    return this.code;
  }
  
  public static StorageTypes fromStorageTypeCode(int storageTypeCode) {
    return map.get(Integer.valueOf(storageTypeCode));
  }
  
  public static boolean isValid(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public static String getStorageTypeString(int storageTypeCode) {
    StorageTypes storageType = map.get(Integer.valueOf(storageTypeCode));
    if (storageType != null)
      return storageType.storageTypeStr; 
    return "INVALID STORAGE TYPE";
  }
}
