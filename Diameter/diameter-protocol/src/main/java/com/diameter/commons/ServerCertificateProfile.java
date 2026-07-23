package com.diameter.commons;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.List;

public interface ServerCertificateProfile {
  String getName();
  
  PrivateKey getPrivateKey();
  
  String getPrivateKeyPassword();
  
  String getPlainTextPrivateKeyPassword();
  
  PrivateKeyAlgo getPrivateKeyAlgo();
  
  Collection<? extends Certificate> getCertificates();
  
  String getCertificatePath();
  
  String getPrivateKeyPath();
  
  Long getId();
  
  String getUUID();
  
  String getCertificateName();
  
  String getPrivateKeyName();
  
  byte[] getCertificateFileBytes();
  
  byte[] getPrivatekeyFileBytes();
  
  List<byte[]> getCertificateChainBytes();
}
