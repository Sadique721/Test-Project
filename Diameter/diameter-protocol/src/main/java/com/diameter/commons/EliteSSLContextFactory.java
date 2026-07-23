package com.diameter.commons;

public class EliteSSLContextFactory {
  private TrustedCAConfiguration trustedCAConfig;
  
  private CRLConfiguration crlConfig;
  
  public EliteSSLContextFactory(TrustedCAConfiguration trustedCAConfig, CRLConfiguration crlConfig) {
    this.trustedCAConfig = trustedCAConfig;
    this.crlConfig = crlConfig;
  }
  
  public EliteSSLContextExt createSSLContext(EliteSSLParameter sslParameter) throws Exception {
    EliteTrustManagerParams trustManagerParams = new EliteTrustManagerParams(this.trustedCAConfig.getCACertificates(), this.crlConfig.getCRLs());
    if (!sslParameter.isValidateCertificateExpiry())
      trustManagerParams.ignoreValidationForCertificateExpiry(); 
    if (!sslParameter.isValidateCertificateRevocation())
      trustManagerParams.ignoreValidationForCertificateRevocation(); 
    if (!sslParameter.isValidateCertificateCA())
      trustManagerParams.ignoreValidationForCertificateCA(); 
    EliteTrustManager eliteTrustManager = new EliteTrustManager(trustManagerParams);
    EliteSSLContextExt eliteSSLContext = new EliteSSLContextExt(sslParameter, eliteTrustManager);
    eliteSSLContext.init();
    return eliteSSLContext;
  }
}
