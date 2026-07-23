package com.diameter.commons;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class AttributeData {
  private String attributeId;
  
  private String name;
  
  private String mandatory;
  
  private String protectedValue;
  
  private String encryption;
  
  private AVPType type;
  
  private String status;
  
  private String dictionaryType;
  
  private AttributeData childAttributeData;
  
  private String minimum;
  
  private String maximum;
  
  private String attributeVendorId;
  
  private String strAvpId = "";
  
  private Map<String, Integer> supportedValueToValue;
  
  private Map<Integer, String> idToSupportedValue;
  
  private Set<AttributeSupportedValueModel> supportedValues = Collectionz.newHashSet();
  
  private static final String ATTRIBUTE_ID = "id";
  
  private static final String ATTRIBUTE_NAME = "name";
  
  private static final String ATTRIBUTE_TYPE = "type";
  
  private static final String ATTRIBUTE_SUPPORTED_VALUES = "supported-values";
  
  private static final String ATTRIBUTE_SUPPORTED_VALUE = "value";
  
  public AttributeData() {}
  
  public AttributeData(String vendorId, String attributeId, String name, String mandatory, String protectedValue, String encryption, AVPType type, String status, String dictionaryType, String minimum, String maximum, String attributeVendorId, Map<Integer, String> valueToSupportedValue) {
    this.attributeId = attributeId;
    this.name = name;
    this.mandatory = mandatory;
    this.protectedValue = protectedValue;
    this.encryption = encryption;
    this.type = type;
    this.status = status;
    this.dictionaryType = dictionaryType;
    this.minimum = minimum;
    this.maximum = maximum;
    this.attributeVendorId = attributeVendorId;
    this.strAvpId = vendorId + ":" + attributeId;
    if (valueToSupportedValue != null) {
      this.idToSupportedValue = valueToSupportedValue;
      this.supportedValueToValue = new HashMap<>();
      Iterator<Integer> iterator = valueToSupportedValue.keySet().iterator();
      while (iterator.hasNext()) {
        Integer key = iterator.next();
        String strVal = valueToSupportedValue.get(key);
        this.supportedValueToValue.put(strVal, key);
        AttributeSupportedValueModel attributeSupportedValueModel = new AttributeSupportedValueModel(key.intValue(), strVal);
        this.supportedValues.add(attributeSupportedValueModel);
      } 
    } 
  }
  
  @XmlAttribute(name = "id")
  public String getAttributeId() {
    return this.attributeId;
  }
  
  public void setAttributeId(String attributeId) {
    this.attributeId = attributeId;
  }
  
  @XmlAttribute(name = "name")
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  @XmlAttribute(name = "mandatory")
  public String getMandatory() {
    return this.mandatory;
  }
  
  public void setMandatory(String mandatory) {
    this.mandatory = mandatory;
  }
  
  @XmlAttribute(name = "protected")
  public String getProtectedValue() {
    return this.protectedValue;
  }
  
  public void setProtectedValue(String protectedValue) {
    this.protectedValue = protectedValue;
  }
  
  @XmlAttribute(name = "encryption")
  public String getEncryption() {
    return this.encryption;
  }
  
  public void setEncryption(String encryption) {
    this.encryption = encryption;
  }
  
  @XmlAttribute(name = "type")
  public AVPType getType() {
    return this.type;
  }
  
  public void setType(AVPType type) {
    if (type != null)
      this.type = type; 
  }
  
  public String getStatus() {
    return this.status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
  
  public String getDictionaryType() {
    return this.dictionaryType;
  }
  
  public void setDictionaryType(String dictionaryType) {
    this.dictionaryType = dictionaryType;
  }
  
  public AttributeData getAttributeData() {
    return this.childAttributeData;
  }
  
  public void setAttributeData(AttributeData attributeData) {
    this.childAttributeData = attributeData;
  }
  
  @XmlAttribute(name = "minimum")
  public String getMinimum() {
    return this.minimum;
  }
  
  public void setMinimum(String minimum) {
    this.minimum = minimum;
  }
  
  @XmlAttribute(name = "maximum")
  public String getMaximum() {
    return this.maximum;
  }
  
  public void setMaximum(String maximum) {
    this.maximum = maximum;
  }
  
  @XmlAttribute(name = "vendor-id")
  public String getAttributeVendorId() {
    return this.attributeVendorId;
  }
  
  public void setAttributeVendorId(String attributeVendorId) {
    this.attributeVendorId = attributeVendorId;
  }
  
  public String getAVPId() {
    return this.strAvpId;
  }
  
  public void setAVPId(String vendorId, String attributeId) {
    this.strAvpId = vendorId + ":" + attributeId;
  }
  
  public Map<Integer, String> getIdToSupportedValues() {
    return this.idToSupportedValue;
  }
  
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(stringWriter);
    indentingPrintWriter.println("Attribute Id: " + this.attributeId);
    indentingPrintWriter.println("Name: " + this.name);
    indentingPrintWriter.println("Mandatory: " + this.mandatory);
    indentingPrintWriter.println("Protected Value: " + this.protectedValue);
    indentingPrintWriter.println("Type: " + this.type);
    indentingPrintWriter.println("Status: " + this.status);
    indentingPrintWriter.println("DictionaryType: " + this.dictionaryType);
    if (this.type == AVPType.GROUPED && Objects.nonNull(this.childAttributeData)) {
      indentingPrintWriter.println("AttributeData: ");
      indentingPrintWriter.incrementIndentation();
      indentingPrintWriter.println(this.childAttributeData.toString());
      indentingPrintWriter.decrementIndentation();
    } 
    indentingPrintWriter.println("Minimum: " + this.minimum);
    indentingPrintWriter.println("Maximum: " + this.maximum);
    indentingPrintWriter.println("Attribute Vendor Id: " + this.attributeVendorId);
    indentingPrintWriter.println("AVP Id: " + this.strAvpId);
    indentingPrintWriter.close();
    return stringWriter.toString();
  }
  
  public boolean isGrouped() {
    return false;
  }
  
  public long getKeyForValue(String val) {
    if (this.supportedValueToValue == null)
      return -1L; 
    Integer key = this.supportedValueToValue.get(val);
    if (key == null)
      return -1L; 
    return key.longValue();
  }
  
  @XmlElementWrapper(name = "supported-values")
  @XmlElement(name = "value")
  public Set<AttributeSupportedValueModel> getSupportedValues() {
    return this.supportedValues;
  }
}
