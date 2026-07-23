package com.diameter.commons;

import java.io.PrintWriter;
import java.io.Writer;

public class IndentingPrintWriter extends PrintWriter implements IndentingWriter {
  private int indentationCount = 0;
  
  public IndentingPrintWriter(Writer writer) {
    super(writer);
  }
  
  public void incrementIndentation() {
    this.indentationCount++;
  }
  
  public void decrementIndentation() {
    if (this.indentationCount == 0)
      throw new IllegalStateException("indentation cannot be negative"); 
    this.indentationCount--;
  }
  
  private void appendTabs() {
    super.print(Strings.repeat("\t", this.indentationCount));
  }
  
  public void print(String s) {
    appendTabs();
    super.print(s);
  }
  
  public void print(boolean b) {
    appendTabs();
    super.print(b);
  }
  
  public void print(char c) {
    appendTabs();
    super.print(c);
  }
  
  public void print(char[] s) {
    appendTabs();
    super.print(s);
  }
  
  public void print(double d) {
    appendTabs();
    super.print(d);
  }
  
  public void print(float f) {
    appendTabs();
    super.print(f);
  }
  
  public void print(int i) {
    appendTabs();
    super.print(i);
  }
  
  public void print(long l) {
    appendTabs();
    super.print(l);
  }
  
  public void print(Object obj) {
    appendTabs();
    super.print(obj);
  }
  
  public IndentingPrintWriter append(CharSequence csq) {
    super.append(csq);
    return this;
  }
}
