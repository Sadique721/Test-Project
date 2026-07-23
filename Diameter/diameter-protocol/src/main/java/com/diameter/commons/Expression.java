package com.diameter.commons;

import java.io.Serializable;
import java.util.List;

public interface Expression extends Serializable {
  public static final int LogicalExpression = 1;
  
  public static final int ComparisionExpression = 2;
  
  public static final int FunctionExpression = 3;
  
  public static final int IdentifierExpression = 4;
  
  public static final int LiteralExpression = 5;
  
  int getExpressionType();
  
  String getStringValue(ValueProvider paramValueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException;
  
  long getLongValue(ValueProvider paramValueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException;
  
  List<String> getStringValues(ValueProvider paramValueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException;
  
  List<Long> getLongValues(ValueProvider paramValueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException;
  
  String getName();
  
  boolean returnsMutipleValue();
  
  boolean isRegularExpression();
  
  boolean hasWildCardCharacter();
}