package com.diameter.commons;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum CipherSuites {
  TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384(49188, 1, "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", (byte)4),
  TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384(49192, 1, "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", (byte)4),
  TLS_RSA_WITH_AES_256_CBC_SHA256(61, 1, "TLS_RSA_WITH_AES_256_CBC_SHA256", (byte)4),
  TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384(49190, 1, "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", (byte)4),
  TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384(49194, 1, "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", (byte)4),
  TLS_DHE_RSA_WITH_AES_256_CBC_SHA256(107, 1, "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", (byte)4),
  TLS_DHE_DSS_WITH_AES_256_CBC_SHA256(106, 1, "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", (byte)4),
  TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA(49162, 1, "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", (byte)6),
  TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA(49172, 1, "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", (byte)6),
  TLS_RSA_WITH_AES_256_CBC_SHA(53, 1, "TLS_RSA_WITH_AES_256_CBC_SHA", (byte)6),
  TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA(49157, 1, "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", (byte)6),
  TLS_ECDH_RSA_WITH_AES_256_CBC_SHA(49167, 1, "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", (byte)4),
  TLS_DHE_RSA_WITH_AES_256_CBC_SHA(57, 1, "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", (byte)6),
  TLS_DHE_DSS_WITH_AES_256_CBC_SHA(56, 1, "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", (byte)6),
  TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256(49187, 1, "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256(49191, 1, "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_RSA_WITH_AES_128_CBC_SHA256(60, 2, "TLS_RSA_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256(49189, 1, "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256(49193, 1, "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_DHE_RSA_WITH_AES_128_CBC_SHA256(103, 1, "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_DHE_DSS_WITH_AES_128_CBC_SHA256(64, 1, "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA(49161, 1, "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", (byte)4),
  TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA(49171, 1, "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", (byte)4),
  TLS_RSA_WITH_AES_128_CBC_SHA(47, 1, "TLS_RSA_WITH_AES_128_CBC_SHA", (byte)7),
  TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA(49156, 1, "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", (byte)4),
  TLS_ECDH_RSA_WITH_AES_128_CBC_SHA(49166, 1, "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", (byte)4),
  TLS_DHE_RSA_WITH_AES_128_CBC_SHA(51, 1, "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", (byte)7),
  TLS_DHE_DSS_WITH_AES_128_CBC_SHA(50, 1, "TLS_DHE_DSS_WITH_AES_128_CBC_SHA", (byte)7),
  TLS_ECDHE_ECDSA_WITH_RC4_128_SHA(49159, 2, "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", (byte)4),
  TLS_ECDHE_RSA_WITH_RC4_128_SHA(49169, 2, "TLS_ECDHE_RSA_WITH_RC4_128_SHA", (byte)4),
  TLS_RSA_WITH_RC4_128_SHA(5, 2, "SSL_RSA_WITH_RC4_128_SHA", (byte)7),
  TLS_ECDH_ECDSA_WITH_RC4_128_SHA(49154, 2, "TLS_ECDH_ECDSA_WITH_RC4_128_SHA", (byte)4),
  TLS_ECDH_RSA_WITH_RC4_128_SHA(49164, 2, "TLS_ECDH_RSA_WITH_RC4_128_SHA", (byte)4),
  TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA(49160, 1, "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", (byte)4),
  TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA(49170, 1, "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", (byte)4),
  TLS_RSA_WITH_3DES_EDE_CBC_SHA(10, 1, "SSL_RSA_WITH_3DES_EDE_CBC_SHA", (byte)7),
  TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA(49155, 1, "TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", (byte)4),
  TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA(49165, 1, "TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", (byte)4),
  TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA(22, 1, "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", (byte)7),
  TLS_RSA_WITH_RC4_128_MD5(4, 2, "SSL_RSA_WITH_RC4_128_MD5", (byte)7),
  TLS_DH_ANON_WITH_AES_256_CBC_SHA(58, 1, "TLS_DH_anon_WITH_AES_256_CBC_SHA", (byte)6),
  TLS_DH_ANON_WITH_AES_128_CBC_SHA256(108, 1, "TLS_DH_anon_WITH_AES_128_CBC_SHA256", (byte)4),
  TLS_ECDH_ANON_WITH_AES_128_CBC_SHA(49176, 1, "TLS_ECDH_anon_WITH_AES_128_CBC_SHA", (byte)4),
  TLS_DH_ANON_WITH_AES_128_CBC_SHA(52, 1, "TLS_DH_anon_WITH_AES_128_CBC_SHA", (byte)7),
  TLS_ECDH_ANON_WITH_RC4_128_SHA(49174, 2, "TLS_ECDH_anon_WITH_RC4_128_SHA", (byte)4),
  TLS_DH_ANON_WITH_RC4_128_MD5(24, 1, "SSL_DH_anon_WITH_RC4_128_MD5", (byte)7),
  TLS_ECDH_ANON_WITH_3DES_EDE_CBC_SHA(49175, 1, "TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", (byte)4),
  TLS_DH_ANON_WITH_3DES_EDE_CBC_SHA(27, 1, "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", (byte)7),
  TLS_RSA_WITH_NULL_SHA256(59, 2, "SSL_RSA_WITH_RC4_128_SHA", (byte)4),
  TLS_ECDHE_ECDSA_WITH_NULL_SHA(49158, 1, "TLS_ECDHE_ECDSA_WITH_NULL_SHA", (byte)4),
  TLS_ECDHE_RSA_WITH_NULL_SHA(49168, 1, "TLS_ECDHE_RSA_WITH_NULL_SHA", (byte)4),
  TLS_RSA_WITH_NULL_SHA(2, 2, "SSL_RSA_WITH_NULL_SHA", (byte)7),
  TLS_ECDH_ECDSA_WITH_NULL_SHA(49153, 1, "TLS_ECDH_ECDSA_WITH_NULL_SHA", (byte)4),
  TLS_ECDH_ANON_WITH_NULL_SHA(49173, 1, "TLS_ECDH_anon_WITH_NULL_SHA", (byte)4),
  TLS_RSA_WITH_NULL_MD5(1, 2, "SSL_RSA_WITH_NULL_MD5", (byte)7),
  TLS_RSA_WITH_DES_CBC_SHA(9, 1, "SSL_RSA_WITH_DES_CBC_SHA", (byte)3),
  TLS_DHE_RSA_WITH_DES_CBC_SHA(21, 1, "SSL_DHE_RSA_WITH_DES_CBC_SHA", (byte)3),
  TLS_DH_ANON_WITH_DES_CBC_SHA(26, 1, "SSL_DH_anon_WITH_DES_CBC_SHA", (byte)3),
  TLS_RSA_EXPORT_WITH_RC4_40_MD5(3, 2, "SSL_RSA_EXPORT_WITH_RC4_40_MD5", (byte)3),
  TLS_DH_ANON_EXPORT_WITH_RC4_40_MD5(23, 1, "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", (byte)1),
  TLS_RSA_EXPORT_WITH_DES40_CBC_SHA(8, 1, "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", (byte)1),
  TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA(20, 1, "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", (byte)1),
  TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA(17, 1, "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", (byte)1),
  TLS_DH_ANON_EXPORT_WITH_DES40_CBC_SHA(25, 1, "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", (byte)1);
  
  public final int code;
  
  public final int cipherSuitesType;
  
  public final String cipherSuitesName;
  
  private byte versionSupport;
  
  private static final Map<Integer, CipherSuites> map;
  
  private static final Map<String, CipherSuites> cipherSuiteNameMap;
  
  private static final Map<TLSVersion, List<CipherSuites>> versionTocipherSuitesMap;
  
  static {
    map = new LinkedHashMap<>();
    versionTocipherSuitesMap = new EnumMap<>(TLSVersion.class);
    cipherSuiteNameMap = new LinkedHashMap<>();
    for (CipherSuites cipherSuite : values()) {
      if (cipherSuite.isSupportTLS1_0()) {
        List<CipherSuites> cipherSuites = versionTocipherSuitesMap.get(TLSVersion.TLS1_0);
        if (cipherSuites == null) {
          cipherSuites = new ArrayList<>();
          versionTocipherSuitesMap.put(TLSVersion.TLS1_0, cipherSuites);
        } 
        cipherSuites.add(cipherSuite);
      } 
      if (cipherSuite.isSupportTLS1_1()) {
        List<CipherSuites> cipherSuites = versionTocipherSuitesMap.get(TLSVersion.TLS1_1);
        if (cipherSuites == null) {
          cipherSuites = new ArrayList<>();
          versionTocipherSuitesMap.put(TLSVersion.TLS1_1, cipherSuites);
        } 
        cipherSuites.add(cipherSuite);
      } 
      if (cipherSuite.isSupportTLS1_2()) {
        List<CipherSuites> cipherSuites = versionTocipherSuitesMap.get(TLSVersion.TLS1_2);
        if (cipherSuites == null) {
          cipherSuites = new ArrayList<>();
          versionTocipherSuitesMap.put(TLSVersion.TLS1_2, cipherSuites);
        } 
        cipherSuites.add(cipherSuite);
      } 
      map.put(Integer.valueOf(cipherSuite.code), cipherSuite);
      cipherSuiteNameMap.put(cipherSuite.cipherSuitesName, cipherSuite);
    } 
  }
  
  CipherSuites(int code, int cipherSuiteType, String name, byte versionSupport) {
    this.code = code;
    this.cipherSuitesType = cipherSuiteType;
    this.cipherSuitesName = name;
    this.versionSupport = versionSupport;
  }
  
  public int getCode() {
    return this.code;
  }
  
  public static CipherSuites fromCipherCode(int code) {
    return map.get(Integer.valueOf(code));
  }
  
  public static CipherSuites fromCipherName(String name) {
    return cipherSuiteNameMap.get(name);
  }
  
  public static boolean isSupported(int value) {
    return map.containsKey(Integer.valueOf(value));
  }
  
  public boolean isBlockCipher() {
    return (this.cipherSuitesType == 1);
  }
  
  public boolean isStreamCipher() {
    return (this.cipherSuitesType == 2);
  }
  
  public boolean isNullCipher() {
    return (this.cipherSuitesType == 3);
  }
  
  public boolean isSupportTLS1_0() {
    return ((this.versionSupport & 0x1) == 1);
  }
  
  public boolean isSupportTLS1_1() {
    return ((this.versionSupport & 0x2) == 2);
  }
  
  public boolean isSupportTLS1_2() {
    return ((this.versionSupport & 0x4) == 4);
  }
  
  public static List<CipherSuites> getSupportedCipherSuites(TLSVersion minTLSVersion, TLSVersion maxTlsVersion) {
    List<CipherSuites> cipherSuites = new ArrayList<>();
    for (TLSVersion tlsVersion : TLSVersion.values()) {
      if (tlsVersion.compareTo((TLSVersion)minTLSVersion) >= 0 && tlsVersion.compareTo((TLSVersion)maxTlsVersion) <= 0)
        cipherSuites.addAll(versionTocipherSuitesMap.get(tlsVersion)); 
    } 
    return cipherSuites;
  }
}
