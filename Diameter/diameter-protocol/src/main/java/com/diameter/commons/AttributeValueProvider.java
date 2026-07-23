package com.diameter.commons;

public interface AttributeValueProvider extends ValueProvider {
  public static final boolean USE_DICTIONARY_VALUE = true;
  
  public static final boolean USE_BASE_VALUE = false;
  
  String getDictionaryKey(String paramString) throws InvalidTypeCastException, MissingIdentifierException;
}
