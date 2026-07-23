package com.diameter.commons;

import java.util.Map;

public interface TGPPField {
  Map<String, Integer> getFieldValueMap(byte[] paramArrayOfbyte);
  
  String getName();
}
