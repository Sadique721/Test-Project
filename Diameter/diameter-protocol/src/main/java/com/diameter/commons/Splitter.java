package com.diameter.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Splitter {
  private final char separator;
  
  private final boolean trimTokens;
  
  private final boolean preserveTokens;
  
  private Splitter(char separator, boolean trimTokens, boolean preserveTokens) {
    this.separator = separator;
    this.preserveTokens = preserveTokens;
    this.trimTokens = trimTokens;
  }
  
  public static Splitter on(char separator) {
    return new Splitter(separator, false, false);
  }
  
  public Splitter trimTokens() {
    return new Splitter(this.separator, true, this.preserveTokens);
  }
  
  public Splitter preserveTokens() {
    return new Splitter(this.separator, this.trimTokens, true);
  }
  
  public List<String> split(String input) {
    if (Strings.isNullOrEmpty(input))
      return Collections.emptyList(); 
    List<String> list = new ArrayList<>();
    int i = 0, start = 0;
    boolean hasSeparator = false;
    while (i < input.length()) {
      if (input.charAt(i) == this.separator) {
        String str = input.substring(start, i);
        if (this.trimTokens)
          str = str.trim(); 
        if (!str.isEmpty() || this.preserveTokens)
          list.add(str); 
        hasSeparator = true;
        start = i + 1;
      } 
      i++;
    } 
    String token = input.substring(start, i);
    if (this.trimTokens)
      token = token.trim(); 
    if (!token.isEmpty() || (hasSeparator && this.preserveTokens))
      list.add(token); 
    return list;
  }
  
  public List<Integer> splitInteger(String input) {
    if (Strings.isNullOrEmpty(input))
      return Collections.emptyList(); 
    List<Integer> list = new ArrayList<>();
    int i = 0;
    int start = 0;
    boolean hasSeparator = false;
    while (i < input.length()) {
      if (input.charAt(i) == this.separator) {
        Integer integer = Integer.valueOf(Integer.parseInt(input.substring(start, i)));
        if (!Objects.isNull(integer) || this.preserveTokens)
          list.add(integer); 
        hasSeparator = true;
        start = i + 1;
      } 
      i++;
    } 
    Integer token = Integer.valueOf(Integer.parseInt(input.substring(start, i)));
    if (!Objects.isNull(token) || (hasSeparator && this.preserveTokens))
      list.add(token); 
    return list;
  }
  
  public String[] splitToArray(String input) {
    return split(input).<String>toArray(new String[0]);
  }
  
  public Integer[] splitToIntegerArray(String input) {
    return splitInteger(input).<Integer>toArray(new Integer[0]);
  }
}
