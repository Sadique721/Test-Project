package com.diameter.commons;

import java.io.Closeable;
import java.io.Flushable;

public interface IndentingWriter extends Appendable, Closeable, Flushable {
  void incrementIndentation();
  
  void decrementIndentation();
  
  void print(String paramString);
  
  void print(boolean paramBoolean);
  
  void print(char paramChar);
  
  void print(char[] paramArrayOfchar);
  
  void print(double paramDouble);
  
  void print(float paramFloat);
  
  void print(int paramInt);
  
  void print(long paramLong);
  
  void print(Object paramObject);
  
  void println();
  
  void println(String paramString);
  
  void println(boolean paramBoolean);
  
  void println(char paramChar);
  
  void println(char[] paramArrayOfchar);
  
  void println(double paramDouble);
  
  void println(float paramFloat);
  
  void println(int paramInt);
  
  void println(long paramLong);
  
  void println(Object paramObject);
  
  void close();
  
  IndentingWriter append(CharSequence paramCharSequence);
}