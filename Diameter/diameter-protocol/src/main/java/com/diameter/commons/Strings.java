package com.diameter.commons;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Strings {
  public static final Function<Object, String> WITHIN_SINGLE_QUOTES = new WithinSingleQuoteFunction();
  
  private static final Function<Object, String> TO_STRING_FUNCTION = new ToStringFunction();
  
  public static final String NOT_APPLICABLE = "-NA-";
  
  public static boolean isNullOrEmpty(String inputString) {
    return (inputString == null || inputString.length() == 0);
  }
  
  public static boolean isAbsentOrEmpty(Optional<String> optionalInputString) {
    return (!optionalInputString.isPresent() || ((String)optionalInputString
      .get()).length() == 0);
  }
  
  public static boolean isNullOrBlank(String inputString) {
    return (inputString == null || inputString.trim().length() == 0);
  }
  
  public static boolean isAbsentOrBlank(Optional<String> optionalInputString) {
    return (!optionalInputString.isPresent() || ((String)optionalInputString
      .get()).trim().length() == 0);
  }
  
  public static String repeat(String string, int count) {
    Preconditions.checkNotNull(string, "string is null");
    if (count <= 1) {
      Preconditions.checkArgument((count >= 0), "invalid count: " + count);
      return (count == 0) ? "" : string;
    } 
    int len = string.length();
    long longSize = len * count;
    int size = (int)longSize;
    if (size != longSize)
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize); 
    char[] array = new char[size];
    string.getChars(0, len, array, 0);
    int n;
    for (n = len; n < size - n; n <<= 1)
      System.arraycopy(array, 0, array, n, n); 
    System.arraycopy(array, 0, array, n, size - n);
    return new String(array);
  }
  
  public static String join(String separator, Object[] parts) {
    return join(separator, parts, TO_STRING_FUNCTION);
  }
  
  public static <T> String join(String separator, T[] parts, Function<? super T, String> partFunction) {
    Preconditions.checkNotNull(parts, "parts are null");
    return join(separator, Arrays.asList(parts), partFunction);
  }
  
  public static <T> String join(String string, Iterable<T> parts) {
    return join(string, parts, (Function)TO_STRING_FUNCTION);
  }
  
  public static <T> String join(String string, Iterable<T> parts, Function<? super T, String> partFunction) {
    Preconditions.checkNotNull(parts, "parts are null");
    return join(string, parts.iterator(), partFunction);
  }
  
  private static <T> String join(String separator, Iterator<T> parts, Function<? super T, String> partFunction) {
    Preconditions.checkNotNull(separator, "separator is null");
    Preconditions.checkNotNull(partFunction, "partFunction is null");
    StringBuilder builder = new StringBuilder();
    while (parts.hasNext()) {
      T part = parts.next();
      if (part != null) {
        builder.append(partFunction.apply(part));
        break;
      } 
    } 
    while (parts.hasNext()) {
      T part = parts.next();
      if (part != null) {
        builder.append(separator);
        builder.append(partFunction.apply(part));
      } 
    } 
    return builder.toString();
  }
  
  public static String padStart(String string, int minLength, char padChar) {
    Preconditions.checkNotNull(string, "string is null");
    if (string.length() >= minLength)
      return string; 
    int padCount = minLength - string.length();
    StringBuilder builder = new StringBuilder(minLength);
    for (int i = 0; i < padCount; i++)
      builder.append(padChar); 
    builder.append(string);
    return builder.toString();
  }
  
  public static String padEnd(String string, int minLength, char padChar) {
    Preconditions.checkNotNull(string, "string is null");
    if (string.length() >= minLength)
      return string; 
    int padCount = minLength - string.length();
    StringBuilder builder = new StringBuilder(minLength);
    builder.append(string);
    for (int i = 0; i < padCount; i++)
      builder.append(padChar); 
    return builder.toString();
  }
  
  static class WithinSingleQuoteFunction implements Function<Object, String> {
    private static final String SINGLE_QUOTE = "'";
    
    public String apply(Object input) {
      return "'" + (String)Strings.TO_STRING_FUNCTION.apply(input) + "'";
    }
  }
  
  static class ToStringFunction implements Function<Object, String> {
    public String apply(Object input) {
      return String.valueOf(input);
    }
  }
  
  public static Splitter splitter(char separator) {
    return Splitter.on(separator);
  }
  
  public static Predicate<String> nonNullAndNonEmpty() {
    return NonNullAndNonEmpty.INSTANCE;
  }
  
  private enum NonNullAndNonEmpty implements Predicate<String> {
    INSTANCE;
    
    public boolean apply(String input) {
      return !Strings.isNullOrEmpty(input);
    }
  }
  
  public static Predicate<String> nonNullAndNonBlank() {
    return NonNullAndNonBlank.INSTANCE;
  }
  
  private enum NonNullAndNonBlank implements Predicate<String> {
    INSTANCE;
    
    public boolean apply(String input) {
      return !Strings.isNullOrBlank(input);
    }
  }
  
  public static void filterNullOrEmpty(Collection<String> collection) {
    Collectionz.filter(collection, nonNullAndNonEmpty());
  }
  
  public static void filterNullOrBlank(Collection<String> collection) {
    Collectionz.filter(collection, nonNullAndNonBlank());
  }
  
  public static boolean toBoolean(String booleanInString) {
    if (booleanInString == null)
      return false; 
    String trimmedString = booleanInString.trim();
    return ("true".equalsIgnoreCase(trimmedString) || "yes"
      .equalsIgnoreCase(trimmedString));
  }
  
  public static Function<String, Integer> toInt() {
    return ToIntFunction.INSTANCE;
  }
  
  private enum ToIntFunction implements Function<String, Integer> {
    INSTANCE;
    
    public Integer apply(String input) {
      return Integer.valueOf(Integer.parseInt(input));
    }
  }
  
  public static Function<String, Long> toLong() {
    return ToLongFunction.INSTANCE;
  }
  
  private enum ToLongFunction implements Function<String, Long> {
    INSTANCE;
    
    public Long apply(String input) {
      return Long.valueOf(Long.parseLong(input));
    }
  }
  
  public static String valueOf(Object input) {
    return valueOf(input, "");
  }
  
  public static String valueOf(Object input, Object placeholder) {
    return (input == null) ? placeholder.toString() : input.toString();
  }
}
