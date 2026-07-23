package com.diameter.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Collectionz {
  public static boolean isNullOrEmpty(Collection<?> collection) {
    return (collection == null) ? true : collection.isEmpty();
  }
  
  public static <T> ArrayList<T> newArrayList() {
    return new ArrayList<>();
  }
  
  public static <T> LinkedList<T> newLinkedList() {
    return new LinkedList<>();
  }
  
  public static <T> HashSet<T> newHashSet() {
    return new HashSet<>();
  }
  
  public static <T> LinkedHashSet<T> newLinkedHashSet() {
    return new LinkedHashSet<>();
  }
  
  public static <K, V> Map<K, V> asHashMap(Collection<V> collection, Function<V, K> valueToKeyFunction) {
    Preconditions.checkNotNull(collection, "collection is null");
    Preconditions.checkNotNull(valueToKeyFunction, "valueToKeyFunction is null");
    Map<K, V> map = new HashMap<>();
    for (V element : collection)
      map.put(valueToKeyFunction.apply(element), element); 
    return map;
  }
  
  public static <K, V> Map<K, V> asLinkedHashMap(Collection<V> collection, Function<V, K> valueToKeyFunction) {
    Preconditions.checkNotNull(collection, "collection is null");
    Preconditions.checkNotNull(valueToKeyFunction, "valueToKeyFunction is null");
    Map<K, V> map = new LinkedHashMap<>();
    for (V element : collection)
      map.put(valueToKeyFunction.apply(element), element); 
    return map;
  }
  
  public static <T extends Collection<?>> Optional<T> firstNonEmpty(T... collections) {
    for (T collection : collections) {
      if (!isNullOrEmpty((Collection<?>)collection))
        return Optional.of(collection); 
    } 
    return Optional.absent();
  }
  
  public static <X, Y> List<Y> map(Collection<X> coll, Function<X, Y> mapper) {
    Preconditions.checkNotNull(mapper, "mapper function is null");
    if (isNullOrEmpty(coll))
      return new ArrayList<>(0); 
    List<Y> result = new ArrayList<>();
    for (X element : coll)
      result.add(mapper.apply(element)); 
    return result;
  }
  
  public static <T> void filter(Collection<T> unfiltered, Predicate<? super T> predicate) {
    Preconditions.checkNotNull(unfiltered, "collection is null");
    Preconditions.checkNotNull(predicate, "predicate is null");
    Iterator<? extends T> iterator = unfiltered.iterator();
    while (iterator.hasNext()) {
      if (!predicate.apply(iterator.next()))
        iterator.remove(); 
    } 
  }
}
