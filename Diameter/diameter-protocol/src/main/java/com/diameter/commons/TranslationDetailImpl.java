package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class TranslationDetailImpl implements TranslationDetail {
  private String inRequestType = "";
  
  private String outRequestType = "";
  
  private List<MappingDataImpl> requestMappingDataList;
  
  private List<MappingDataImpl> responseMappingDataList;
  
  private boolean isDummyResponse = false;
  
  private String mappingName = "";
  
  public TranslationDetailImpl() {
    this.requestMappingDataList = new ArrayList<>();
    this.responseMappingDataList = new ArrayList<>();
  }
  
  @XmlElement(name = "in-request-type", type = String.class)
  public String getInRequestType() {
    return this.inRequestType;
  }
  
  public void setInRequestType(String inRequestType) {
    this.inRequestType = inRequestType;
  }
  
  @XmlElement(name = "out-request-type", type = String.class)
  public String getOutRequestType() {
    return this.outRequestType;
  }
  
  public void setOutRequestType(String outRequestType) {
    this.outRequestType = outRequestType;
  }
  
  @XmlElementWrapper(name = "request-mappings")
  @XmlElement(name = "request-mapping")
  public List<MappingDataImpl> getRequestMappingDataList() {
    return this.requestMappingDataList;
  }
  
  public void setRequestMappingDataList(List<MappingDataImpl> requestMappingDataList) {
    this.requestMappingDataList = requestMappingDataList;
  }
  
  @XmlElementWrapper(name = "response-mappings")
  @XmlElement(name = "response-mapping")
  public List<MappingDataImpl> getResponseMappingDataList() {
    return this.responseMappingDataList;
  }
  
  public void setResponseMappingDataList(List<MappingDataImpl> responseMappingDataList) {
    this.responseMappingDataList = responseMappingDataList;
  }
  
  public String toString() {
    StringWriter stringBuffer = new StringWriter();
    PrintWriter out = new PrintWriter(stringBuffer);
    out.println();
    out.println("    -- Translation Detail Configuration -- ");
    out.println("    In Request Type  \t\t= " + this.inRequestType);
    out.println("    Out Request Type \t\t= " + this.outRequestType);
    out.println("    Dummy Response Enabled = " + getIsDummyResponse());
    out.println();
    out.println("     Request Mapping:");
    int requestMappingDataSize = this.requestMappingDataList.size();
    for (int i = 0; i < requestMappingDataSize; i++)
      out.println(this.requestMappingDataList.get(i)); 
    out.println("     Response Mapping:");
    int responseMappingDataSize = this.responseMappingDataList.size();
    for (int j = 0; j < responseMappingDataSize; j++)
      out.println(this.responseMappingDataList.get(j)); 
    out.close();
    return stringBuffer.toString();
  }
  
  @XmlElement(name = "dummy-response-enabled", type = boolean.class)
  public boolean getIsDummyResponse() {
    return this.isDummyResponse;
  }
  
  public void setIsDummyResponse(boolean isDummyResponse) {
    this.isDummyResponse = isDummyResponse;
  }
  
  @XmlElement(name = "mapping-name", type = String.class)
  public String getMappingName() {
    return this.mappingName;
  }
  
  public void setMappingName(String mappingName) {
    this.mappingName = mappingName;
  }
}
