package com.diameter.commons;

public interface ValueProvider {
  public static final ValueProvider NO_VALUE_PROVIDER = identifier -> null;
  
  String getStringValue(String paramString);
}
