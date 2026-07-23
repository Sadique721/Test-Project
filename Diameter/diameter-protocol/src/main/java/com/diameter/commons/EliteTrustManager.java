package com.diameter.commons;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509TrustManager;


public class EliteTrustManager implements X509TrustManager {
  private EliteTrustManagerParams trustManagerParams;
  
  private List<TLSWarning> tlsWarnings;
  
  private CertifcateTrustedCertificateChecker certifcateTrustedCertificateChecker;
  
  private CertificateRevocationChecker certificateRevocationChecker;
  
  private CertificateValidationChecker certificateValidationChecker;
  
  private CertificateSubjectCnChecker certificateSubjectCnChecker;
  
  private boolean certificateExpiryVarified = false;
  
  private boolean certificateRevocationVarified = false;
  
  private boolean certificateCAVarified = false;
  
  private boolean certificateSubjectCNVarified = false;
  
  public EliteTrustManager(EliteTrustManagerParams eliteTrustManagerParams) {
    this.tlsWarnings = new ArrayList<>();
    this.trustManagerParams = eliteTrustManagerParams;
    this
      .certifcateTrustedCertificateChecker = new CertifcateTrustedCertificateChecker(!this.trustManagerParams.isValidateCertificateCA(), this.trustManagerParams.getTrustedCertificates());
    this
      .certificateRevocationChecker = new CertificateRevocationChecker(!this.trustManagerParams.isValidateCertificateRevocation(), this.trustManagerParams.getCRLs());
    this.certificateValidationChecker = new CertificateValidationChecker(!this.trustManagerParams.isValidateCertificateExpiry());
  }
  
  public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    try {
      reset();
      if (arg0 == null || arg0.length == 0) {
        if (this.trustManagerParams.isValidateCertificateCA()) {
          this.tlsWarnings.add(new TLSWarning("Client Cetificate Authentication"));
          return;
        } 
        throw new CertificateException("No certificate found");
      } 
      checkIfValid(arg0);
    } catch (GeneralSecurityException e) {
      throw new CertificateException(e);
    } 
  }
  
  public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    try {
      reset();
      if (arg0 == null || arg0.length == 0)
        throw new CertificateException("No certificate found"); 
      checkIfValid(arg0);
    } catch (GeneralSecurityException e) {
      throw new CertificateException(e);
    } 
  }
  
  private void reset() {
    this.tlsWarnings.clear();
    this.certificateExpiryVarified = false;
    this.certificateRevocationVarified = false;
    this.certificateCAVarified = false;
    this.certificateSubjectCNVarified = false;
  }
  
  public X509Certificate[] getAcceptedIssuers() {
    return this.trustManagerParams.getTrustedCertificates().<X509Certificate>toArray(new X509Certificate[this.trustManagerParams.getTrustedCertificates().size()]);
  }
  
  private void checkIfValid(X509Certificate[] x509Certificates) throws GeneralSecurityException {
    TLSWarning tlsWarning = this.certifcateTrustedCertificateChecker.checkCertificatePath(x509Certificates);
    if (tlsWarning != null)
      this.tlsWarnings.add(tlsWarning); 
    this.certificateCAVarified = true;
    tlsWarning = this.certificateRevocationChecker.checkCertificatePath(x509Certificates);
    if (tlsWarning != null)
      this.tlsWarnings.add(tlsWarning); 
    this.certificateRevocationVarified = true;
    tlsWarning = this.certificateValidationChecker.checkCertificatePath(x509Certificates);
    if (tlsWarning != null)
      this.tlsWarnings.add(tlsWarning); 
    this.certificateExpiryVarified = true;
    if (this.certificateSubjectCnChecker != null) {
      tlsWarning = this.certificateSubjectCnChecker.checkCertificatePath(x509Certificates);
      if (tlsWarning != null)
        this.tlsWarnings.add(tlsWarning); 
      this.certificateSubjectCNVarified = true;
    } 
  }
  
  public void setCertifcateTrustedCertificateChecker(CertifcateTrustedCertificateChecker certifcateTrustedCertificateChecker) {
    this.certifcateTrustedCertificateChecker = certifcateTrustedCertificateChecker;
  }
  
  public void setCertificateRevocationChecker(CertificateRevocationChecker certificateRevocationChecker) {
    this.certificateRevocationChecker = certificateRevocationChecker;
  }
  
  public void setCertificateValidationChecker(CertificateValidationChecker certificateValidationChecker) {
    this.certificateValidationChecker = certificateValidationChecker;
  }
  
  public void setCertificateSubjectCnChecker(CertificateSubjectCnChecker certificateSubjectCnChecker) {
    this.certificateSubjectCnChecker = certificateSubjectCnChecker;
  }
  
  public void setValidateCertificateSubjectCN(boolean validateCertificateSubjectCN) {
    this.certificateSubjectCNVarified = validateCertificateSubjectCN;
  }
  
  public boolean isCertificateExpiryVarified() {
    return this.certificateExpiryVarified;
  }
  
  public boolean isCertificateRevocationVarified() {
    return this.certificateRevocationVarified;
  }
  
  public boolean isCertificateCAVarified() {
    return this.certificateCAVarified;
  }
  
  public boolean isCertificateSubjectCNVarified() {
    return this.certificateSubjectCNVarified;
  }
  
  public List<TLSWarning> getTlsWarnings() {
    return this.tlsWarnings;
  }
}
