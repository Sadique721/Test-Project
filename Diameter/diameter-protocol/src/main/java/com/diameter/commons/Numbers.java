package com.diameter.commons;

public class Numbers {
  public static final Predicate<Integer> POSITIVE_INT = new PositiveIntPredicate();
  
  public static final Predicate<Long> POSITIVE_LONG = new PositiveLongPredicate();
  
  public static byte[] toByteArray(int value, int noOfBytes) {
    Preconditions.checkArgument((noOfBytes >= 0), "noOfBytes cannot be negative");
    if (noOfBytes == 0)
      return new byte[0]; 
    byte[] intBytes = new byte[noOfBytes];
    for (int i = noOfBytes - 1; i >= 0; i--, value >>>= 8)
      intBytes[i] = (byte)value; 
    return intBytes;
  }
  
  public static byte[] toByteArray(long value, int noOfBytes) {
    Preconditions.checkArgument((noOfBytes >= 0), "noOfBytes cannot be negative");
    if (noOfBytes == 0)
      return new byte[0]; 
    byte[] longBytes = new byte[noOfBytes];
    for (int i = noOfBytes - 1; i >= 0; i--, value >>>= 8L)
      longBytes[i] = (byte)(int)value; 
    return longBytes;
  }
  
  public static int parseInt(String value, int otherwise) {
    return parseInt(value, Predicates.alwaysTrue(), otherwise);
  }
  
  public static Integer parseInt(String value, Integer otherwise) {
    return parseInt(value, Predicates.alwaysTrue(), otherwise);
  }
  
  public static long parseLong(String value, long otherwise) {
    long result = otherwise;
    try {
      result = Long.parseLong(value);
    } catch (NumberFormatException nfe) {
      LogManager.getLogger().trace(nfe);
    } 
    return result;
  }
  
  public static int parseInt(String value, Predicate<Integer> predicate, int otherwise) {
    int result = otherwise;
    try {
      result = Integer.parseInt(value);
      result = predicate.apply(Integer.valueOf(result)) ? result : otherwise;
    } catch (NumberFormatException nfe) {
      LogManager.getLogger().trace(nfe);
    } 
    return result;
  }
  
  public static Integer parseInt(String value, Predicate<Integer> predicate, Integer valueOtherwise) {
    Integer result = valueOtherwise;
    try {
      result = Integer.valueOf(Integer.parseInt(value));
      result = predicate.apply(result) ? result : valueOtherwise;
    } catch (NumberFormatException nfe) {
      LogManager.getLogger().trace(nfe);
    } 
    return result;
  }
  
  private static class PositiveIntPredicate implements Predicate<Integer> {
    private PositiveIntPredicate() {}
    
    public boolean apply(Integer input) {
      return (input != null) ? ((input.intValue() > 0)) : false;
    }
  }
  
  private static class PositiveLongPredicate implements Predicate<Long> {
    private PositiveLongPredicate() {}
    
    public boolean apply(Long input) {
      return (input != null) ? ((input.longValue() > 0L)) : false;
    }
  }
}
