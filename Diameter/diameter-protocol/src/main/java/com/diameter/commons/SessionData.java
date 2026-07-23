package com.diameter.commons;

import java.util.Date;
import java.util.Set;

public interface SessionData extends Comparable<SessionData> {
  Date getCreationTime();
  
  Date getLastUpdateTime();
  
  String getSessionId();
  
  String getSchemaName();
  
  String getValue(String paramString);
  
  Date getDateValue(String paramString);
  
  void addValue(String paramString1, String paramString2);
  
  void addDateValue(String paramString, Date paramDate);
  
  Object getObjectValue(String paramString);
  
  void addObjectValue(String paramString, Object paramObject);
  
  Set<String> getKeySet();
  
  void setSessionLoadTime(long paramLong);
  
  long getSessionLoadTime();
  
  byte[] getBytesValue(String paramString);
}
