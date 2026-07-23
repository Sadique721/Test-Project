package com.diameter.commons;

public enum Application {
  BASE("BASE"),
  NASREQ("NAS"),
  MOBILE_IPV4("MOBILEIPv4"),
  BASEACCOUNTING("BASE-ACCT"),
  CC("CC"),
  EAP("EAP"),
  SIP("SIP"),
  TGPP_CX_PX("3GPP-CX-PX"),
  TGPP_SH_PH("3GPP-SH-PH"),
  TGPP_RE("3GPP-RE"),
  TGPP_WX("3GPP-WX"),
  TGPP_ZN("3GPP-ZN"),
  TGPP_ZH("3GPP-ZH"),
  TGPP_GQ("3GPP-GQ"),
  TGPP_GMB("3GPP-GMB"),
  TGPP_GX_29_210_15("3GPP-GX"),
  TGPP_GX_OVER_GY("3GPP-GX-OVER-GY"),
  TGPP_MM10("3GPP-MM10"),
  TGPP_RX_29_211_17("3GPP-Rx"),
  TGPP_PR("3GPP-PR"),
  TGPP_RX_29_214_18("3GPP-RX"),
  TGPP_GX_29_212_18("3GPP-GX"),
  TGPP_STA("3GPP-STA"),
  TGPP_S6A("3GPP-S6A"),
  TGPP_S13_S13("3GPP-S13/S13'"),
  TGPP_SLG("3GPP-SLG"),
  TGPP_SWM("3GPP-SWM"),
  TGPP_SWX("3GPP-SWX"),
  TGPP_GXX("3GPP-GXX"),
  TGPP_S9("3GPP-S9"),
  TGPP_ZPN("3GPP-ZPN"),
  TGPP_S6B("3GPP-S6B"),
  TGPP_SLH("3GPP-SLH"),
  TGPP_SGMB("3GPP-SGMB"),
  TGPP_SY("3GPP-SY"),
  UNKNOWN("UNKNOWN"),
  TGPP_GY("3GPP-GY");
  
  private final String displayName;
  
  Application(String displayName) {
    this.displayName = displayName;
  }
  
  public String getDisplayName() {
    return this.displayName;
  }
}
