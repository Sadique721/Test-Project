package com.diameter.commons;

import javax.annotation.Nonnull;

public enum ResultCodeCategory {
  RC1XXX(1000, "Informational", "1XXX", false),
  RC2XXX(2000, "Success", "2XXX", false),
  RC3XXX(3000, "Protocol Errors", "3XXX", true),
  RC4XXX(4000, "Transient Errors", "4XXX", true),
  RC5XXX(5000, "Permanent Failures", "5XXX", true),
  RC6XXX(6000, "Unknown", "6XXX", true);
  
  public final int value;
  
  public final String category;
  
  public final String categoryType;
  
  public final boolean isFailureCategory;
  
  ResultCodeCategory(int val, String category, String categoryType, boolean isFailureCategory) {
    this.categoryType = categoryType;
    this.value = val;
    this.category = category;
    this.isFailureCategory = isFailureCategory;
  }
  
  public static boolean isSessionRemovableCategory(ResultCodeCategory category) {
    switch (category) {
      case RC3XXX:
      case RC5XXX:
        return true;
    } 
    return false;
  }
  
  @Nonnull
  public static ResultCodeCategory getResultCodeCategory(long resultCode) {
    if (resultCode > 999L && resultCode < 2000L)
      return RC1XXX; 
    if (resultCode > 1999L && resultCode < 3000L)
      return RC2XXX; 
    if (resultCode > 2999L && resultCode < 4000L)
      return RC3XXX; 
    if (resultCode > 3999L && resultCode < 5000L)
      return RC4XXX; 
    if (resultCode > 4999L && resultCode < 6000L)
      return RC5XXX; 
    return RC6XXX;
  }
}
