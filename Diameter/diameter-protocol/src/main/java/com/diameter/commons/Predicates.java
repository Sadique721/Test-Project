package com.diameter.commons;

public class Predicates {
  public static <T> Predicate<T> nonNull() {
    return ObjectPredicate.NON_NULL.withNarrowedType();
  }
  
  public static <T> Predicate<T> alwaysTrue() {
    return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
  }
  
  public static <T> Predicate<T> alwaysFalse() {
    return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
  }
  
  enum ObjectPredicate implements Predicate<Object> {
    ALWAYS_TRUE {
      public boolean apply(Object object) {
        return true;
      }
    },
    ALWAYS_FALSE {
      public boolean apply(Object object) {
        return false;
      }
    },
    NON_NULL {
      public boolean apply(Object input) {
        return (input != null);
      }
    };
    
    <T> Predicate<T> withNarrowedType() {
      return (Predicate<T>) this;
    }
  }
  
  public static <T> Predicate<T> and(Predicate<? super T> first, Predicate<T> second) {
    return new AndPredicate<>(first, second);
  }
  
  private static class AndPredicate<T> implements Predicate<T> {
    private final Predicate<? super T> first;
    
    private final Predicate<? super T> second;
    
    public AndPredicate(Predicate<? super T> first, Predicate<? super T> second) {
      this.first = first;
      this.second = second;
    }
    
    public boolean apply(T input) {
      return (this.first.apply(input) && this.second.apply(input));
    }
  }
}
