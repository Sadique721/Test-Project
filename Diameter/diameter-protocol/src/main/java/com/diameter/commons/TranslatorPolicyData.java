package com.diameter.commons;

import java.util.List;
import java.util.Map;

public interface TranslatorPolicyData extends UserDefined {
  String getTransMapConfId();
  
  String getName();
  
  String getFromTranslatorId();
  
  String getToTranslatorId();
  
  List<TranslationDetailImpl> getTranslationDetailList();
  
  boolean getIsDummyResponse();
  
  Map<String, String> getDummyResponseMap();
  
  String getBaseTranslationMappingId();
  
  String getScript();
}
