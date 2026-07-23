package com.diameter.commons;


import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public interface CertificateChecker {
  TLSWarning checkCertificatePath(X509Certificate[] paramArrayOfX509Certificate) throws GeneralSecurityException;
}