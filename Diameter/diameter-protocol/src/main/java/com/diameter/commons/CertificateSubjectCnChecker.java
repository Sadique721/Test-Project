package com.diameter.commons;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.StringTokenizer;

public abstract class CertificateSubjectCnChecker implements CertificateChecker {
  private boolean ignoreException;
  
  public CertificateSubjectCnChecker(boolean ignoreException) {
    this.ignoreException = ignoreException;
  }
  
  public TLSWarning checkCertificatePath(X509Certificate[] certificates) throws GeneralSecurityException {
    X509Certificate x509Certificate = certificates[0];
    String subjectDN = x509Certificate.getSubjectX500Principal().getName();
    String subjectCNFromCertificate = getCN(subjectDN);
    if (matchCN(subjectCNFromCertificate))
      return null; 
    if (this.ignoreException) {
      TLSWarning tlsWarning = new TLSWarning("Certificate SubjectCN check");
      tlsWarning.addCertificate(x509Certificate);
      return tlsWarning;
    } 
    throw new GeneralSecurityException("Certificate SubjectCN name mismatch. Expected: " + getPossibleSubjectCN() + " Actual SubjectCN : " + subjectCNFromCertificate);
  }
  
  private boolean matchCN(String subjectCNFromCertificate) {
    boolean result = false;
    for (String cn : getPossibleSubjectCN()) {
      if (StringUtility.matchesElitePattern(cn, subjectCNFromCertificate)) {
        result = true;
        break;
      } 
    } 
    return result;
  }
  
  private String getCN(String dn) {
    StringTokenizer stk = new StringTokenizer(dn, ",");
    while (stk.hasMoreTokens()) {
      String token = stk.nextToken().trim();
      String[] strTokenArr = token.split("=");
      if (strTokenArr.length != 2)
        continue; 
      String identifier = strTokenArr[0].trim();
      if (!identifier.equals("CN"))
        continue; 
      String value = strTokenArr[1].trim();
      if (value != null) {
        String[] cnValue = value.split(" ");
        return cnValue[0].trim();
      } 
    } 
    return null;
  }
  
  public abstract List<String> getPossibleSubjectCN();
}
