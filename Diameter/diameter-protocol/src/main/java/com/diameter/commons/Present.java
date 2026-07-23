package com.diameter.commons;

class Present<T> extends Optional<T> {
  private final T reference;
  
  public Present(T reference) {
    this.reference = reference;
  }
  
  public boolean isPresent() {
    return true;
  }
  
  public T get() {
    return this.reference;
  }
  
  public String toString() {
    return String.valueOf(this.reference);
  }
  
  public T orElse(T defaultValue) {
    return this.reference;
  }
  
  public T orNull() {
    return this.reference;
  }
  
  public Optional<T> or(Optional<T> other) {
    return this;
  }
}
