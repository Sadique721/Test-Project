package com.diameter.commons;

public interface Translator {
  String getFromId();
  
  String getToId();
  
  void init(TranslatorPolicyData paramTranslatorPolicyData) throws InitializationFailedException;
  
  void postTranslateRequest(String paramString, TranslatorParams paramTranslatorParams);
  
  void translateRequest(String paramString, TranslatorParams paramTranslatorParams) throws TranslationFailedException;
  
  void translateResponse(String paramString, TranslatorParams paramTranslatorParams) throws TranslationFailedException;
  
  void postTranslateResponse(String paramString, TranslatorParams paramTranslatorParams);
}
