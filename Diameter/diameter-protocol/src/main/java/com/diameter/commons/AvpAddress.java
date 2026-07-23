package com.diameter.commons;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class AvpAddress extends BaseDiameterAVP {
  private static final byte IPv4 = 1;
  
  private static final byte IPv6 = 2;
  
  private static final String MODULE = "AVP Address";
  
  public AvpAddress(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setStringValue(String strAddress) {
    InetAddress address = null;
    try {
      address = InetAddress.getByName(strAddress);
    } catch (UnknownHostException e) {
      LogManager.getLogger().warn("AVP Address", "Invalid IPAddress Value: " + strAddress + ".Provide valid IPv4 or IPv6 syntax. " + e.getMessage());
      return;
    } 
    if (address != null) {
        byte[] ipBytes = address.getAddress();

        short family;
        if (ipBytes.length == 4) {
            family = 1; // IPv4
        } else if (ipBytes.length == 16) {
            family = 2; // IPv6
        } else {
            throw new IllegalArgumentException("Invalid IP address");
        }

        ByteBuffer buffer = ByteBuffer.allocate(2 + ipBytes.length);
        buffer.putShort(family);           // Network byte order (big-endian)
        buffer.put(ipBytes);

        setValueBytes(buffer.array());
    }
  }
  
  public String getStringValue() {
    byte[] data = getValueBytes();
    if (data == null)
      return null; 
    byte[] address = data;
    if (data.length == 6 || data.length == 18) {
      address = new byte[data.length - 2];
      System.arraycopy(data, 2, address, 0, address.length);
    } 
    try {
      return InetAddress.getByAddress(address).getHostAddress();
    } catch (UnknownHostException e) {
      LogManager.getLogger().warn("AVP Address", "Invalid IPAddress Value: " + e.getMessage());
      return null;
    } 
  }
  
  public void doPlus(String value) {
    if (value != null)
      setStringValue(value); 
  }
}