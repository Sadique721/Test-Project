package com.diameter.commons;

public abstract class Optional<T> {
  public static <T> Optional<T> absent() {
    return Absent.withType();
  }
  
  public static <T> Optional<T> of(T reference) {
    return (reference == null) ? Absent.absent() : new Present<>(reference);
  }
  
  public abstract boolean isPresent();
  
  public abstract T get();
  
  public abstract T orElse(T paramT);
  
  public abstract T orNull();
  
  public abstract Optional<T> or(Optional<T> paramOptional);
}
