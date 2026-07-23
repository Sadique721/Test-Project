package com.diameter.commons;

import java.security.cert.X509CRL;
import java.util.List;

public interface CRLConfiguration {
  List<X509CRL> getCRLs();
  
  boolean getOCSPEnabled();
  
  String getOCSPURL();
}

