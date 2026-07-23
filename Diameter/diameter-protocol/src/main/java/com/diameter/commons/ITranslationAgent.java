package com.diameter.commons;

import java.util.Map;

public interface ITranslationAgent {
  void translate(String paramString, TranslatorParams paramTranslatorParams, boolean paramBoolean) throws TranslationFailedException;
  
  boolean isExists(String paramString);
  
  Map<String, String> getDummyResponseMap(String paramString);
}
