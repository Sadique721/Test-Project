package com.diameter.commons;

public interface Function<F, T> {
  T apply(F paramF);
}