package com.diameter.commons;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class CertifcateTrustedCertificateChecker implements CertificateChecker {
  private boolean ignoreException;
  
  private List<X509Certificate> trustedCertificates;
  
  public CertifcateTrustedCertificateChecker(boolean ignoreException, List<X509Certificate> certificates) {
    this.ignoreException = ignoreException;
    this.trustedCertificates = certificates;
  }
  
  public TLSWarning checkCertificatePath(X509Certificate[] certificates) throws CertificateException {
    TLSWarning tlsWarning = new TLSWarning("Unknown CA");
    for (X509Certificate certificate : certificates) {
      for (X509Certificate trustedCert : this.trustedCertificates) {
        if (!certificate.getIssuerDN().equals(trustedCert.getSubjectDN()))
          continue; 
        try {
          certificate.verify(trustedCert.getPublicKey());
          return null;
        } catch (Exception e) {
          tlsWarning.addCertificate(certificate);
        } 
      } 
    } 
    if (this.ignoreException)
      return tlsWarning; 
    throw new CertificateException("Unknown CA.");
  }
}
