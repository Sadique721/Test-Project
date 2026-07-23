package com.diameter.commons;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class TLSWarning {
  private String warning;
  
  private List<X509Certificate> certificates;
  
  public TLSWarning(String warning) {
    this.warning = warning;
    this.certificates = new ArrayList<>();
  }
  
  public void addCertificate(X509Certificate certificate) {
    this.certificates.add(certificate);
  }
  
  public String getWarning() {
    return this.warning;
  }
  
  public List<X509Certificate> getCertificates() {
    return this.certificates;
  }
}
