package com.diameter.commons;

public abstract class BaseAVPBuilder {
  protected int intAVPCode;
  
  protected int intVendorId;
  
  protected String strAvpId = "";
  
  protected String strAVPEncryption;
  
  protected byte bAVPFlag;
  
  public abstract IDiameterAVP createAVP();
  
  public void setAVPCode(int code) {
    this.intAVPCode = code;
  }
  
  public void setVendorId(int vendorId) {
    this.intVendorId = vendorId;
  }
  
  public void setVendorBit() {
    this.bAVPFlag = (byte)(this.bAVPFlag | Byte.MIN_VALUE);
  }
  
  public void setMandatoryBit() {
    this.bAVPFlag = (byte)(this.bAVPFlag | 0x40);
  }
  
  public void setProtectedBit() {
    this.bAVPFlag = (byte)(this.bAVPFlag | 0x20);
  }
  
  public void setAVPEncryption(String encryption) {
    this.strAVPEncryption = encryption;
  }
  
  public void setAVPId(int vendorId, int AVPCode) {
    this.strAvpId = vendorId + ":" + AVPCode;
  }
}
