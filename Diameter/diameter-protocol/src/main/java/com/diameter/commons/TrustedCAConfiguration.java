package com.diameter.commons;

import java.security.cert.X509Certificate;
import java.util.List;

public interface TrustedCAConfiguration {
  List<X509Certificate> getCACertificates();
}
