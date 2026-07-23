package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class MappingDataImpl implements MappingData {
  private String checkExpression = "";
  
  private String mappingExpression = "";
  
  private String defaultValue = "";
  
  private String valueMapping = "";
  
  @XmlElement(name = "checked-expression", type = String.class)
  public String getCheckExpression() {
    return this.checkExpression;
  }
  
  public void setCheckExpression(String checkExpression) {
    this.checkExpression = checkExpression;
  }
  
  @XmlElement(name = "default-value", type = String.class)
  public String getDefaultValue() {
    return this.defaultValue;
  }
  
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
  
  @XmlElement(name = "mapping-expression", type = String.class)
  public String getMappingExpression() {
    return this.mappingExpression;
  }
  
  public void setMappingExpression(String mappingExpression) {
    this.mappingExpression = mappingExpression;
  }
  
  @XmlElement(name = "value-mapping", type = String.class)
  public String getValueMapping() {
    return this.valueMapping;
  }
  
  public void setValueMapping(String valueMapping) {
    this.valueMapping = valueMapping;
  }
  
  public String toString() {
    StringWriter stringBuffer = new StringWriter();
    PrintWriter out = new PrintWriter(stringBuffer);
    out.println("      -- Mapping Data Configuration -- ");
    out.println("      CheckExpression    = " + this.checkExpression);
    out.println("      MappingExpression  = " + this.mappingExpression);
    out.println("      DefaultValue       = " + this.defaultValue);
    out.println("      ValueMapping       = " + this.valueMapping);
    out.close();
    return stringBuffer.toString();
  }
}
