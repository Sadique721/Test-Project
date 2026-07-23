package com.diameter.commons;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class EliteTrustManagerParams {
  private boolean validateCertificateExpiry = true;
  
  private boolean validateCertificateRevocationExceptions = true;
  
  private boolean validateCertificateCA = true;
  
  private List<X509Certificate> trustedCertificates;
  
  private List<X509CRL> crls;
  
  public EliteTrustManagerParams(List<X509Certificate> trustedCertificates, List<X509CRL> crls) {
    this.trustedCertificates = trustedCertificates;
    this.crls = crls;
  }
  
  public static EliteTrustManagerParams newTrustManagerParams(File trustedCertificateLocation, File crlLocation) throws Exception {
    List<X509Certificate> trustedCertificates = scanTrustedCertificates(trustedCertificateLocation.getAbsolutePath());
    List<X509CRL> crls = scanCRLs(crlLocation);
    return new EliteTrustManagerParams(trustedCertificates, crls);
  }
  
  private static List<X509CRL> scanCRLs(File crlLocation) throws Exception {
    List<X509CRL> crls = new ArrayList<>();
    List<File> files = FileUtil.getRecursiveFileFromPath(crlLocation.getAbsolutePath());
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    for (File file : files) {
      X509CRL crl = (X509CRL)cf.generateCRL(fullStream(file));
      crls.add(crl);
    } 
    return crls;
  }
  
  private static List<X509Certificate> scanTrustedCertificates(String trustedCertificateLocation) throws Exception {
    List<X509Certificate> trustedCertificates = new ArrayList<>();
    List<File> files = FileUtil.getRecursiveFileFromPath(trustedCertificateLocation);
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    for (File file : files) {
      X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(fullStream(file));
      trustedCertificates.add(certificate);
    } 
    return trustedCertificates;
  }
  
  private static InputStream fullStream(File fname) throws IOException {
    try(FileInputStream fis = new FileInputStream(fname); 
        DataInputStream dis = new DataInputStream(fis)) {
      byte[] bytes = new byte[dis.available()];
      return new ByteArrayInputStream(bytes);
    } 
  }
  
  public List<X509Certificate> getTrustedCertificates() {
    return this.trustedCertificates;
  }
  
  public List<X509CRL> getCRLs() {
    return this.crls;
  }
  
  public void ignoreValidationForCertificateExpiry() {
    this.validateCertificateExpiry = false;
  }
  
  public void ignoreValidationForCertificateRevocation() {
    this.validateCertificateRevocationExceptions = false;
  }
  
  public void ignoreValidationForCertificateCA() {
    this.validateCertificateCA = false;
  }
  
  public boolean isValidateCertificateExpiry() {
    return this.validateCertificateExpiry;
  }
  
  public boolean isValidateCertificateRevocation() {
    return this.validateCertificateRevocationExceptions;
  }
  
  public boolean isValidateCertificateCA() {
    return this.validateCertificateCA;
  }
}
