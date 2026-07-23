package com.diameter.commons;

import java.util.List;

public class StringUtility {
  public static final String fillChar(String input, int length) {
    return fillChar(input, length, ' ');
  }
  
  public static final String fillChar(String input, int length, char chr) {
    if (input != null) {
      StringBuilder stringBuffer = new StringBuilder();
      stringBuffer.append(input);
      for (int i = input.length(); i <= length; i++)
        stringBuffer.append(chr); 
      return stringBuffer.toString();
    } 
    return "";
  }
  
  public static final String getCommaSeparated(String[] input) {
    String result = "";
    if (input != null && input.length > 0) {
      int size = input.length - 1;
      for (int i = 0; i < size; i++)
        result = result + input[i] + ","; 
      result = result + input[input.length - 1];
    } 
    return result;
  }
  
  public static final <T> String getDelimiterSeparatedString(T[] input, String delim) {
    StringBuilder builder = new StringBuilder();
    if (input != null && input.length > 0) {
      int size = input.length;
      for (int i = 0; i < size - 1; i++)
        builder.append(String.valueOf(input[i])).append(delim); 
      builder.append(String.valueOf(input[size - 1]));
    } 
    return builder.toString();
  }
  
  public static final <T> String getDelimitirSeparatedString(List<T> input, String delim) {
    StringBuilder builder = new StringBuilder();
    if (input != null && input.size() > 0) {
      int size = input.size();
      for (int i = 0; i < size - 1; i++)
        builder.append(String.valueOf(input.get(i))).append(delim); 
      builder.append(String.valueOf(input.get(size - 1)));
    } 
    return builder.toString();
  }
  
  public static String escapeSpecialChars(String value, char... specialChar) {
    if (value == null || value.trim().isEmpty())
      return null; 
    if (specialChar == null || specialChar.length == 0)
      return value; 
    StringBuilder stringBuilder = new StringBuilder();
    for (char currChar : value.toCharArray()) {
      if (ParserUtility.containsChar(specialChar, currChar)) {
        stringBuilder.append('\\').append(currChar);
      } else {
        stringBuilder.append(currChar);
      } 
    } 
    return stringBuilder.toString();
  }
  
  public static boolean matchesElitePattern(String sourceString, String elitePattern) {
    return matches(sourceString, elitePattern.toCharArray());
  }
  
  private static boolean matches(String sourceString, char[] pattern) {
    int stringOffset = 0;
    char[] stringCharArray = sourceString.toCharArray();
    int stringLen = stringCharArray.length;
    int patternLen = pattern.length;
    int currentPos = 0;
    try {
      for (currentPos = 0; currentPos < patternLen; currentPos++, stringOffset++) {
        if (stringOffset == stringLen) {
          while (currentPos < patternLen) {
            if (pattern[currentPos] != '*')
              return false; 
            currentPos++;
          } 
          return true;
        } 
        if (pattern[currentPos] != stringCharArray[stringOffset])
          if (pattern[currentPos] == '\\') {
            currentPos++;
            if (pattern[currentPos] != stringCharArray[stringOffset])
              return false; 
          } else if (pattern[currentPos] == '*') {
            boolean bStar = true;
            currentPos++;
            if (currentPos == patternLen)
              return true; 
            while (bStar) {
              int tmpCurrentPos = currentPos;
              while (stringCharArray[stringOffset] != pattern[tmpCurrentPos]) {
                stringOffset++;
                if (stringOffset == stringLen) {
                  while (tmpCurrentPos < patternLen) {
                    if (pattern[tmpCurrentPos] != '*')
                      return false; 
                    tmpCurrentPos++;
                  } 
                  return true;
                } 
              } 
              while (tmpCurrentPos < patternLen) {
                if (pattern[tmpCurrentPos] != stringCharArray[stringOffset]) {
                  if (pattern[tmpCurrentPos] == '*') {
                    bStar = false;
                    currentPos = tmpCurrentPos - 1;
                    stringOffset--;
                    break;
                  } 
                  if (pattern[tmpCurrentPos] != '?')
                    break; 
                } 
                tmpCurrentPos++;
                stringOffset++;
                if (stringOffset == stringLen) {
                  while (tmpCurrentPos < patternLen) {
                    if (pattern[tmpCurrentPos] != '*')
                      return false; 
                    tmpCurrentPos++;
                  } 
                  return true;
                } 
              } 
              if (stringOffset == stringLen && tmpCurrentPos == patternLen)
                return true; 
            } 
          } else if (pattern[currentPos] != '?') {
            return false;
          }  
      } 
    } catch (ArrayIndexOutOfBoundsException e) {
      return false;
    } 
    if (currentPos < patternLen)
      while (pattern[currentPos] == '*')
        currentPos++;  
    return (currentPos == patternLen && stringOffset == stringLen);
  }
}
