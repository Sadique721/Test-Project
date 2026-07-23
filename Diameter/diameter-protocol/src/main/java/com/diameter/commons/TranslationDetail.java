package com.diameter.commons;

import java.util.List;

public interface TranslationDetail {
  String getInRequestType();
  
  String getOutRequestType();
  
  List<MappingDataImpl> getRequestMappingDataList();
  
  List<MappingDataImpl> getResponseMappingDataList();
  
  boolean getIsDummyResponse();
  
  String getMappingName();
}
