package com.diameter.commons;

import java.util.Map;

public interface CopyPacketTranslator {
  String getName();
  
  void init() throws InitializationFailedException;
  
  void postTranslateRequest(TranslatorParams paramTranslatorParams);
  
  void postTranslateResponse(TranslatorParams paramTranslatorParams);
  
  void translateRequest(TranslatorParams paramTranslatorParams) throws TranslationFailedException;
  
  void translateResponse(TranslatorParams paramTranslatorParams) throws TranslationFailedException;
  
  Map<String, String> getDummyMappings();
}