package com.diameter.commons;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum AVPType {
  UNSIGNED32("Unsigned32") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpUnsigned32Builder();
    }
  },
  UNSIGNED64("Unsigned64") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpUnsigned64Builder();
    }
  },
  INTEGER32("Integer32") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpInteger32Builder();
    }
  },
  INTEGER64("Integer64") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpInteger64Builder();
    }
  },
  FLOAT32("Float32") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpFloat32Builder();
    }
  },
  FLOAT64("Float64") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpFloat64Builder();
    }
  },
  GROUPED("Grouped") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpGroupedBuilder();
    }
  },
  DIAMETERIDENTITY("DiameterIdentity") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpDiameterIdentityBuilder();
    }
  },
  DIAMETERURI("DiameterURI") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpDiameterURIBuilder();
    }
  },
  TIME("Time") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpTimeBuilder();
    }
  },
  UTF8STRING("UTF8String") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpUTF8StringBuilder();
    }
  },
  IPADDRESS("IPAddress") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpAddressBuilder();
    }
  },
  IPV4ADDRESS("IPv4Address") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpIpv4AddressBuilder();
    }
  },
  ENUMERATED("Enumerated") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpEnumeratedBuilder();
    }
  },
  USERLOCATIONINFO("UserLocationInfo") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpUserLocationInfoAvpBuilder();
    }
  },
  USEREQUIPMENTINFOVALUE("UserEquipmentInfoValue") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.UserEquipmentInfoValueAvpBuilder();
    }
  },
  USEREQUIPMENTINFO("UserEquipmentInfo") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.UserEquipmentInfoAvpBuilder();
    }
  },
  OCTETS("Octets") {
    public BaseAVPBuilder baseAVPBuilder() {
      return (BaseAVPBuilder)new DiameterDictionary.AvpOctetStringBuilder();
    }
  };
  
  private final String avpType;
  
  AVPType(String avpType) {
    this.avpType = avpType;
  }
  
  public String getAVPType() {
    return this.avpType;
  }
  
  public abstract BaseAVPBuilder baseAVPBuilder();
}
