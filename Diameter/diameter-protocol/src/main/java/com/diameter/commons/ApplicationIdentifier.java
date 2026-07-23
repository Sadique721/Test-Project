package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public enum ApplicationIdentifier implements ApplicationEnum {
  BASE(0L, 0L, ServiceTypes.BOTH, Application.BASE),
  NASREQ(1L, 0L, ServiceTypes.BOTH, Application.NASREQ),
  MOBILE_IPV4(2L, 0L, ServiceTypes.BOTH, Application.MOBILE_IPV4),
  BASEACCOUNTING(3L, 0L, ServiceTypes.ACCT, Application.BASEACCOUNTING),
  RELAY(4294967295L, 0L, ServiceTypes.BOTH, Application.BASE),
  CC(4L, 0L, ServiceTypes.AUTH, Application.CC),
  EAP(5L, 0L, ServiceTypes.AUTH, Application.EAP),
  SIP(6L, 0L, ServiceTypes.BOTH, Application.SIP),
  TGPP_CX_PX(16777216L, 10415L, ServiceTypes.BOTH, Application.TGPP_CX_PX),
  TGPP_SH_PH(16777217L, 10415L, ServiceTypes.BOTH, Application.TGPP_SH_PH),
  TGPP_RE(16777218L, 10415L, ServiceTypes.BOTH, Application.TGPP_RE),
  TGPP_WX(16777219L, 10415L, ServiceTypes.BOTH, Application.TGPP_WX),
  TGPP_ZN(16777220L, 10415L, ServiceTypes.BOTH, Application.TGPP_ZN),
  TGPP_ZH(16777221L, 10415L, ServiceTypes.BOTH, Application.TGPP_ZH),
  TGPP_GQ(16777222L, 10415L, ServiceTypes.BOTH, Application.TGPP_GQ),
  TGPP_GMB(16777223L, 10415L, ServiceTypes.BOTH, Application.TGPP_GMB),
  TGPP_GX_29_210_15(16777224L, 10415L, ServiceTypes.BOTH, Application.TGPP_GX_29_210_15),
  TGPP_GX_OVER_GY(16777225L, 10415L, ServiceTypes.BOTH, Application.TGPP_GX_OVER_GY),
  TGPP_MM10(16777226L, 10415L, ServiceTypes.BOTH, Application.TGPP_MM10),
  TGPP_RX_29_211_17(16777229L, 10415L, ServiceTypes.BOTH, Application.TGPP_RX_29_211_17),
  TGPP_PR(16777230L, 10415L, ServiceTypes.BOTH, Application.TGPP_PR),
  TGPP_RX_29_214_18(16777236L, 10415L, ServiceTypes.BOTH, Application.TGPP_RX_29_214_18),
  TGPP_GX_29_212_18(16777238L, 10415L, ServiceTypes.BOTH, Application.TGPP_GX_29_212_18),
  TGPP_STA(16777250L, 10415L, ServiceTypes.BOTH, Application.TGPP_STA),
  TGPP_S6A(16777251L, 10415L, ServiceTypes.BOTH, Application.TGPP_S6A),
  TGPP_S13_S13(16777252L, 10415L, ServiceTypes.BOTH, Application.TGPP_S13_S13),
  TGPP_SLG(16777255L, 10415L, ServiceTypes.BOTH, Application.TGPP_SLG),
  TGPP_SWM(16777264L, 10415L, ServiceTypes.BOTH, Application.TGPP_SWM),
  TGPP_SWX(16777265L, 10415L, ServiceTypes.AUTH, Application.TGPP_SWX),
  TGPP_GXX(16777266L, 10415L, ServiceTypes.BOTH, Application.TGPP_GXX),
  TGPP_S9(16777267L, 10415L, ServiceTypes.BOTH, Application.TGPP_S9),
  TGPP_ZPN(16777268L, 10415L, ServiceTypes.BOTH, Application.TGPP_ZPN),
  TGPP_S6B(16777272L, 10415L, ServiceTypes.BOTH, Application.TGPP_S6B),
  TGPP_SLH(16777291L, 10415L, ServiceTypes.BOTH, Application.TGPP_SLH),
  TGPP_SGMB(16777292L, 10415L, ServiceTypes.BOTH, Application.TGPP_SGMB),
  TGPP_SY(16777302L, 10415L, ServiceTypes.BOTH, Application.TGPP_SY);
  
  public final long applicationId;
  
  public final long vendorId;
  
  public final Application application;
  
  public final ServiceTypes serviceType;
  
  private static final Map<Long, ApplicationIdentifier> map;
  
  private static final ApplicationIdentifier[] APPLICATION_IDENTIFIERS;
  
  static {
    APPLICATION_IDENTIFIERS = values();
    map = new HashMap<>();
    for (ApplicationIdentifier type : APPLICATION_IDENTIFIERS)
      map.put(Long.valueOf(type.applicationId), type); 
  }
  
  ApplicationIdentifier(@Nonnull long applicationId, @Nonnull long vendorId, ServiceTypes appType, Application application) {
    this.applicationId = applicationId;
    this.vendorId = vendorId;
    this.serviceType = (ServiceTypes)Preconditions.checkNotNull(appType, "serviceType is null");
    this.application = (Application)Preconditions.checkNotNull(application, "application is null");
  }
  
  public static ApplicationIdentifier fromApplicationIdentifiers(long applicationId) {
    return map.get(Long.valueOf(applicationId));
  }
  
  public long getVendorId() {
    return this.vendorId;
  }
  
  public Application getApplication() {
    return this.application;
  }
  
  public long getApplicationId() {
    return this.applicationId;
  }
  
  public ServiceTypes getApplicationType() {
    return this.serviceType;
  }
  
  public static String getDisplayName(long applicationId) {
    ApplicationIdentifier applicationIdentifier = fromApplicationIdentifiers(applicationId);
    if (applicationIdentifier == null)
      return String.valueOf(applicationId); 
    return applicationIdentifier.getApplication().getDisplayName();
  }
  
  public String toString() {
    return 
      getVendorId() + ":" + 
      
      getApplicationId() + " [" + 
      getApplication().getDisplayName() + "]";
  }
}
