package com.diameter.commons;

public class AvpRule implements Cloneable {
  private int vendorId;
  
  private int attrId;
  
  private String name = "";
  
  private String minimum = "0";
  
  private String maximum = "0xFFFFFFF";
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getMinimum() {
    return this.minimum;
  }
  
  public void setMinimum(String minimum) {
    this.minimum = minimum;
  }
  
  public String getMaximum() {
    return this.maximum;
  }
  
  public void setMaximum(String maximum) {
    this.maximum = maximum;
  }
  
  public Object clone() throws CloneNotSupportedException {
    AvpRule avpRule = new AvpRule();
    avpRule.attrId = this.attrId;
    avpRule.vendorId = this.vendorId;
    avpRule.setName(new String(this.name));
    avpRule.setMinimum(new String(this.minimum));
    avpRule.setMaximum(new String(this.maximum));
    return avpRule;
  }
  
  public int getVendorId() {
    return this.vendorId;
  }
  
  public int getAttrId() {
    return this.attrId;
  }
  
  public void setVendorId(int vendorId) {
    this.vendorId = vendorId;
  }
  
  public void setAttrId(int attrId) {
    this.attrId = attrId;
  }
}
