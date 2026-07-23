package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IDiameterPacket {
  int getCommandCode();
  
  void setCommandCode(int paramInt);
  
  void setVersion(int paramInt);
  
  int getVersion();
  
  long getApplicationID();
  
  void setApplicationID(long paramLong);
  
  int getCommandFlag();
  
  void setRequestBit();
  
  void setProxiableBit();
  
  void setErrorBit();
  
  void setReTransmittedBit();
  
  void resetRequestBit();
  
  void resetProxiableBit();
  
  void resetErrorBit();
  
  void resetReTransmittedBit();
  
  int getLength();
  
  int getInfoLength();
  
  void setLength(int paramInt);
  
  int getHop_by_hopIdentifier();
  
  int getEnd_to_endIdentifier();
  
  Map<String, ArrayList<IDiameterAVP>> getAvpmap();
  
  ArrayList<IDiameterAVP> getAVPList();
  
  IDiameterAVP getAVP(String paramString);
  
  IDiameterAVP getInfoAVP(String paramString);
  
  List<IDiameterAVP> getInfoAVPList(String paramString);
  
  IDiameterAVP getAVP(String paramString, boolean paramBoolean);
  
  boolean isRequest();
  
  boolean isProxiable();
  
  boolean isError();
  
  boolean isReTransmitted();
  
  void setResponsePacketHeader(IDiameterPacket paramIDiameterPacket);
  
  void addAvp(IDiameterAVP paramIDiameterAVP);
  
  void addInfoAvp(IDiameterAVP paramIDiameterAVP);
  
  List<IDiameterAVP> getVendorSpeficAvps(long paramLong, int paramInt);
  
  byte[] getBytes();
  
  byte[] getBytes(boolean paramBoolean);
  
  void setBytes(byte[] paramArrayOfbyte);
  
  void refreshPacketHeader();
  
  void refreshInfoPacketHeader();
  
  ArrayList<IDiameterAVP> getAVPList(String paramString);
  
  String getInfoAVPValue(String paramString);
}
