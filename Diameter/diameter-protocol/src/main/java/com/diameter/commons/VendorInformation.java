package com.diameter.commons;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "attribute-list")
public class VendorInformation {
  private String vendorId;
  
  private String name;
  
  private String status;
  
  private List<AttributeData> attributeData;
  
  public VendorInformation() {
    this.attributeData = new ArrayList<>();
  }
  
  public VendorInformation(String vendorId, String name, String status) {
    this.vendorId = vendorId;
    this.name = name;
    this.status = status;
    this.attributeData = new ArrayList<>();
  }
  
  @XmlAttribute(name = "vendorid", required = true)
  public String getVendorId() {
    return this.vendorId;
  }
  
  public void setVendorId(String vendorId) {
    this.vendorId = vendorId;
  }
  
  @XmlAttribute(name = "vendor-name", required = true)
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getStatus() {
    return this.status;
  }
  
  public void setStatus(String status) {
	this.status = status;
  }
  
  @XmlElement(name = "attribute")
  public List<AttributeData> getAttributeData() {
    return this.attributeData;
  }
  
  public void setAttributeData(List<AttributeData> attributeData) {
    this.attributeData = attributeData;
  }
  
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(stringWriter);
    indentingPrintWriter.println("Vendor: " + this.vendorId);
    indentingPrintWriter.println("Name: " + this.name);
    indentingPrintWriter.println("Status: " + this.status);
    indentingPrintWriter.println("AttributeData: ");
    indentingPrintWriter.incrementIndentation();
    for (AttributeData attributeDetails : this.attributeData)
      indentingPrintWriter.println(attributeDetails.toString()); 
    indentingPrintWriter.decrementIndentation();
    indentingPrintWriter.println("Application Map: ");
    indentingPrintWriter.incrementIndentation();
    indentingPrintWriter.decrementIndentation();
    indentingPrintWriter.close();
    return stringWriter.toString();
  }
  
  public void addAttribute(AttributeData attribute) {
    this.attributeData.add(attribute);
  }
  
  public int hashCode() {
    return getName().hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof VendorInformation))
      return false; 
    VendorInformation that = (VendorInformation)obj;
    return (Equality.areEqual(getVendorId(), that.getVendorId()) && 
      Equality.areEqual(getName(), that.getName()));
  }
}
