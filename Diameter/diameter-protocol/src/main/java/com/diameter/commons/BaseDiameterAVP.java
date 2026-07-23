package com.diameter.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class BaseDiameterAVP implements IDiameterAVP, Cloneable {
  public static final int STANDARD_AVP_HEADER_LENGTH = 8;
  
  public static final int VS_AVP_HEADER_LENGTH = 12;
  
  private String strAvpId = "";
  
  private byte[] bValueBuffer = new byte[0];
  
  private int paddingSize = 0;
  
  private String strAVPEncryption;
  
  private ByteBuffer header;
  
  private int intAVPLength;
  
  private int intVendorId;
  
  public static final int STANDARD_VENDOR_ID = 0;
  
  public BaseDiameterAVP() {}
  
  public BaseDiameterAVP(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    this.strAvpId = strAvpId;
    this.strAVPEncryption = strAVPEncryption;
    this.intVendorId = intVendorId;
    if (intVendorId != 0) {
      this.header = ByteBuffer.allocate(12);
      this.header.putInt(8, intVendorId);
      setLength(12);
    } else {
      this.header = ByteBuffer.allocate(8);
      setLength(8);
    } 
    this.header.putInt(0, intAVPCode);
    this.header.put(4, (byte)(bAVPFlag & 0xFF));
  }
  
  public void setValueBytes(byte[] valueBuffer) {
    this.bValueBuffer = valueBuffer;
    int remainder = 0;
    if (valueBuffer != null)
      remainder = valueBuffer.length % 4; 
    this.paddingSize = (remainder == 0) ? 0 : (4 - remainder);
    if (this.bValueBuffer != null)
      setLength(this.header.limit() + this.bValueBuffer.length); 
  }
  
  public byte[] getValueBytes() {
    return this.bValueBuffer;
  }
  
  public int getAVPCode() {
    return this.header.getInt(0);
  }
  
  public int getLength() {
    return this.intAVPLength;
  }
  
  public int getPaddingLength() {
    return this.paddingSize;
  }
  
  public void setLength(int length) {
    this.intAVPLength = length;
    DiameterUtility.intToByteArray(this.header, 5, length, 3);
  }
  
  public int getFlag() {
    return this.header.get(4);
  }
  
  public void setFlag(int flag) {
    this.header.put(4, (byte)(flag & 0xFF));
  }
  
  public int getVendorId() {
    return this.intVendorId;
  }
  
  public void setTime(Date date) {}
  
  public long getTime() {
    return -1L;
  }
  
  public long getInteger() {
    return -1L;
  }
  
  public void setInteger(long lValue) {}
  
  public double getFloat() {
    return -1.0D;
  }
  
  public void setFloat(double lValue) {}
  
  public ArrayList<IDiameterAVP> getGroupedAvp() {
    return null;
  }
  
  public void setGroupedAvp(ArrayList<IDiameterAVP> childAttr) {}
  
  public int readFlagOnwardsFrom(InputStream sourceStream) {
    int valueBytes = 0;
    int totalByte = 0;
    try {
      if (isVendorSpecificAttribute()) {
        valueBytes = this.intAVPLength - 12;
      } else {
        valueBytes = this.intAVPLength - 8;
      } 
      byte[] bValueBuffer = new byte[valueBytes];
      sourceStream.read(bValueBuffer);
      totalByte += valueBytes;
      setValueBytes(bValueBuffer);
      int remainder = this.intAVPLength % 4;
      if (remainder > 0) {
        remainder = 4 - remainder;
        sourceStream.skip(remainder);
        totalByte += remainder;
      } 
      return totalByte;
    } catch (Exception e) {
      return totalByte;
    } 
  }
  
  public Object clone() throws CloneNotSupportedException {
    BaseDiameterAVP result = null;
    result = (BaseDiameterAVP)super.clone();
    if (this.header != null) {
      byte[] headerBytes = this.header.array();
      result.header = ByteBuffer.wrap(Arrays.copyOf(headerBytes, headerBytes.length));
    } 
    if (this.bValueBuffer != null) {
      result.bValueBuffer = new byte[this.bValueBuffer.length];
      System.arraycopy(this.bValueBuffer, 0, result.bValueBuffer, 0, this.bValueBuffer.length);
    } 
    return result;
  }
  
  public String getAVPEncryption() {
    return this.strAVPEncryption;
  }
  
  public byte[] getBytes() {
    ByteArrayOutputStream temp = new ByteArrayOutputStream(getLength());
    try {
      writeTo(temp);
    } catch (IOException e) {
      LogManager.ignoreTrace(e);
      throw new AssertionError(e.getMessage());
    } 
    return temp.toByteArray();
  }
  
  public void writeTo(OutputStream out) throws IOException {
    out.write(this.header.array());
    out.write(getValueBytes());
    if (getPaddingLength() > 0)
      out.write(new byte[getPaddingLength()]); 
  }
  
  public boolean isMandatory() {
    return ((getFlag() & 0xFF & 0x40) > 0);
  }
  
  public boolean isVendorSpecificAttribute() {
    return ((getFlag() & 0xFF & 0xFFFFFF80) > 0);
  }
  
  public boolean isProtected() {
    return ((getFlag() & 0xFF & 0x20) > 0);
  }
  
  public boolean isGrouped() {
    return false;
  }
  
  public String getStringValue(boolean bUseDictionaryValue) {
    return getStringValue();
  }
  
  public String getStringValue() {
    try {
      return new String(getValueBytes(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return new String(getValueBytes());
    } 
  }
  
  public String getLogValue() {
    return getStringValue(true);
  }
  
  public String toString() {
    if (!isVendorSpecificAttribute()) {
      if (getAVPCode() == 2)
        return "\t\t" + DiameterDictionary.getInstance().getAttributeName(getAVPCode()) + "(" + getAVPCode() + ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? ("[V-" + getVendorId() + "]") : "") + (isProtected() ? "[P]" : "") + " = *******"; 
      return "\t\t" + DiameterDictionary.getInstance().getAttributeName(getAVPCode()) + "(" + getAVPCode() + ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? ("[V-" + getVendorId() + "]") : "") + (isProtected() ? "[P]" : "") + " = " + getLogValue();
    } 
    return "\t\t" + DiameterDictionary.getInstance().getAttributeName(getVendorId(), getAVPCode()) + "(" + getAVPCode() + ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? ("[V-" + getVendorId() + "]") : "") + (isProtected() ? "[P]" : "") + " = " + getLogValue();
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Integer.valueOf(this.intAVPLength) });
  }
  
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass())
      return false; 
    try {
      BaseDiameterAVP avp = (BaseDiameterAVP)obj;
      return (getAVPCode() == avp.getAVPCode() && 
        getVendorId() == avp.getVendorId() && 
        getLength() == avp.getLength() && 
        Arrays.equals(getValueBytes(), avp.getValueBytes()));
    } catch (ClassCastException e) {
      LogManager.ignoreTrace(e);
      return false;
    } 
  }
  
  public void refreshAVPHeader() {}
  
  public boolean hasValue() {
    if (isVendorSpecificAttribute())
      return (getLength() > 12); 
    return (getLength() > 8);
  }
  
  public void setAVPId(int vendorId, int AVPCode) {
    this.strAvpId = vendorId + ":" + AVPCode;
  }
  
  public String getAVPId() {
    return this.strAvpId;
  }
  
  public String getKeyStringValue(String key) {
    return null;
  }
  
  public void setKeyStringValue(String key, String value) {}
  
  public Set<String> getKeySet() {
    return null;
  }
  
  public void doPlus(String value) {
    if (value != null)
      setStringValue(getStringValue() + value); 
  }
}
