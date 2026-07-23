package com.diameter.commons;

public class Arrayz {
  public static <T> boolean isNullOrEmpty(T... array) {
    return (array == null || array.length == 0);
  }
}
