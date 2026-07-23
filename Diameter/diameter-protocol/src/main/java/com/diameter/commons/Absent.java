package com.diameter.commons;

class Absent<T> extends Optional<T> {
  static final Absent<Object> absent = new Absent();
  
  static <T> Absent<T> withType() {
    return (Absent)absent;
  }
  
  public boolean isPresent() {
    return false;
  }
  
  public T get() {
    throw new IllegalStateException("Optional.get() cannot be called on an absent value");
  }
  
  public String toString() {
    return "Optional.absent()";
  }
  
  public T orElse(T defaultValue) {
    return Preconditions.checkNotNull(defaultValue, "orElse reference should not be null. Use orNull instead of orElse");
  }
  
  public T orNull() {
    return null;
  }
  
  public Optional<T> or(Optional<T> other) {
    return other;
  }
}
