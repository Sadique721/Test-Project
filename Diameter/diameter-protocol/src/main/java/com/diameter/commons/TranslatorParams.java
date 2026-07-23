package com.diameter.commons;

public interface TranslatorParams {
  Object getParam(String paramString);
  
  void setParam(String paramString, Object paramObject);
}
