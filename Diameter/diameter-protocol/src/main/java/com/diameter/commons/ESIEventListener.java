package com.diameter.commons;

public interface ESIEventListener<T extends ESCommunicator> {
  void alive(T paramT);
  
  void dead(T paramT);
}
