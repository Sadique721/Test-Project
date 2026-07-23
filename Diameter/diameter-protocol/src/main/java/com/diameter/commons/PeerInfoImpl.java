package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class PeerInfoImpl implements PeerInfo {
  private String peerName;
  
  private int loadFactor = 1;
  
  public void setPeerName(String peerName) {
    this.peerName = peerName;
  }
  
  public void setLoadFactor(Integer loadFactor) {
    this.loadFactor = loadFactor.intValue();
  }
  
  @XmlElement(name = "peer-name", type = String.class)
  public String getPeerName() {
    return this.peerName;
  }
  
  @XmlElement(name = "load-factor", type = int.class, defaultValue = "1")
  public int getLoadFactor() {
    return this.loadFactor;
  }
  
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter out = new PrintWriter(stringWriter);
    out.print(this.peerName + " -W- " + this.loadFactor);
    return stringWriter.toString();
  }
}
