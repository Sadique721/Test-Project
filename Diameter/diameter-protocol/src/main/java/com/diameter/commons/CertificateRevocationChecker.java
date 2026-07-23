package com.diameter.commons;

import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

public class CertificateRevocationChecker implements CertificateChecker {
  private boolean ignoreException;
  
  private List<X509CRL> crls;
  
  public CertificateRevocationChecker(boolean ignoreException, List<X509CRL> crls) {
    this.ignoreException = ignoreException;
    this.crls = crls;
  }
  
  public TLSWarning checkCertificatePath(X509Certificate[] certificates) throws CertificateException {
    for (X509Certificate certificate : certificates) {
      if (this.crls != null && !this.crls.isEmpty()) {
        TLSWarning tlsWarning = new TLSWarning("Certificate Revocation");
        for (X509CRL crl : this.crls) {
          if (!certificate.getIssuerDN().equals(crl.getIssuerDN()))
            continue; 
          if (crl.isRevoked(certificate)) {
            if (this.ignoreException) {
              tlsWarning.addCertificate(certificate);
              continue;
            } 
            throw new CertificateException("Certificate is revoked");
          } 
        } 
      } 
    } 
    return null;
  }
}