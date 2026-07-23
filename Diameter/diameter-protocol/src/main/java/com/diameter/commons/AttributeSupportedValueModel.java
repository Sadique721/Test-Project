package com.diameter.commons;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class AttributeSupportedValueModel {
  private int id;
  
  private String name;
  
  private AttributeData parentAttributeModel;
  
  public AttributeSupportedValueModel() {}
  
  public AttributeSupportedValueModel(int id, String name) {
    this.id = id;
    this.name = name;
  }
  
  @XmlAttribute(name = "id")
  public int getId() {
    return this.id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  @XmlAttribute(name = "name")
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof AttributeSupportedValueModel))
      return false; 
    AttributeSupportedValueModel other = (AttributeSupportedValueModel)obj;
    return (Equality.areEqual(getId(), other.getId()) && 
      Equality.areEqual(getName(), other.getName()));
  }
  
  public int hashCode() {
    return this.name.hashCode();
  }
  
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(stringWriter);
    indentingPrintWriter.println("Supported Value Id: " + this.id);
    indentingPrintWriter.println("Name: " + this.name);
    indentingPrintWriter.close();
    return stringWriter.toString();
  }
  
  @XmlTransient
  public AttributeData getParent() {
    return this.parentAttributeModel;
  }
  
  public void setParent(AttributeData parentAttributeModel) {
    this.parentAttributeModel = parentAttributeModel;
  }
}
