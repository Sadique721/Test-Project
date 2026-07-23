package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum TLSVersion {
  TLS1_0("TLSv1"),
  TLS1_1("TLSv1.1"),
  TLS1_2("TLSv1.2");
  
  public final String version;
  
  private static final Map<String, TLSVersion> versionToTLSVersion;
  
  TLSVersion(String name) {
    this.version = name;
  }
  
  static {
    versionToTLSVersion = new HashMap<>(14);
    for (TLSVersion type : values())
      versionToTLSVersion.put(type.version, type); 
  }
  
  public static TLSVersion fromVersion(String version) {
    return versionToTLSVersion.get(version);
  }
}
