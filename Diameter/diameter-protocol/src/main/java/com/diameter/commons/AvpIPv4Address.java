package com.diameter.commons;

import java.net.InetAddress;

public class AvpIPv4Address extends AvpOctetString {
  private String MODULE = "IPv4AdressAVP";
  
  public AvpIPv4Address(int intAVPCode, int intVendorId, byte flag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, flag, strAvpId, strAVPEncryption);
  }
  
  public void setStringValue(String strAddress) {
    if (strAddress != null) {
      byte[] valueBytes = null;
      try {
        valueBytes = InetAddress.getByName(strAddress).getAddress();
      } catch (Exception e) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn(this.MODULE, "Failed to interpret IP Addrees from value(" + strAddress + "). Reason: " + e.getMessage()); 
        valueBytes = new byte[4];
      } 
      setValueBytes(valueBytes);
    } 
  }
  
  public String getStringValue() {
    String address = null;
    byte[] valueBytes = null;
    try {
      valueBytes = getValueBytes();
      if (valueBytes.length != 0) {
        address = InetAddress.getByAddress(getValueBytes()).getHostAddress();
      } else {
        return null;
      } 
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn(this.MODULE, "Failed to interpret ip addrees." + e.getMessage()); 
      return null;
    } 
    return address;
  }
}
