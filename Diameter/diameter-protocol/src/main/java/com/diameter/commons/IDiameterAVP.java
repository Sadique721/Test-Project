package com.diameter.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public interface IDiameterAVP extends Cloneable {
  Object clone() throws CloneNotSupportedException;
  
  int getLength();
  
  int getPaddingLength();
  
  int getFlag();
  
  int getVendorId();
  
  int getAVPCode();
  
  byte[] getValueBytes();
  
  byte[] getBytes();
  
  void writeTo(OutputStream paramOutputStream) throws IOException;
  
  boolean isGrouped();
  
  long getInteger();
  
  double getFloat();
  
  ArrayList<IDiameterAVP> getGroupedAvp();
  
  void setTime(Date paramDate);
  
  long getTime();
  
  void setLength(int paramInt);
  
  void setValueBytes(byte[] paramArrayOfbyte);
  
  void setInteger(long paramLong);
  
  void setFloat(double paramDouble);
  
  void setFlag(int paramInt);
  
  void setGroupedAvp(ArrayList<IDiameterAVP> paramArrayList);
  
  int readFlagOnwardsFrom(InputStream paramInputStream);
  
  String getStringValue();
  
  String getStringValue(boolean paramBoolean);
  
  String getKeyStringValue(String paramString);
  
  Set<String> getKeySet();
  
  void setKeyStringValue(String paramString1, String paramString2);
  
  boolean isMandatory();
  
  boolean isVendorSpecificAttribute();
  
  boolean isProtected();
  
  void setStringValue(String paramString);
  
  void refreshAVPHeader();
  
  boolean hasValue();
  
  String getAVPId();
  
  String getLogValue();
  
  void doPlus(String paramString);
}
