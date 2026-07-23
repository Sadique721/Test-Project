package com.diameter.commons;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class EliteSSLContextExt {
  private static final String MODULE = "ELITE-SSL-CNTX";
  
  private EliteSSLParameter sslParameter;
  
  private KeyStore keyStore;
  
  private SSLContext sslContext;
  
  private EliteTrustManager eliteTrustManager;
  
  private List<TLSVersion> tlsVersions;
  
  public EliteSSLContextExt(EliteSSLParameter sslParameter, EliteTrustManager eliteTrustManager) {
    this.sslParameter = sslParameter;
    this.eliteTrustManager = eliteTrustManager;
    this.tlsVersions = new ArrayList<>();
  }
  
  public void init() throws InitializationFailedException {
    if (this.sslParameter.getMinTlsVersion() == null)
      throw new InitializationFailedException("Min TLS Version not found"); 
    if (this.sslParameter.getMaxTlsVersion() == null)
      throw new InitializationFailedException("Max TLS Version not found"); 
    for (TLSVersion tlsVersion : TLSVersion.values()) {
      if (tlsVersion.compareTo(this.sslParameter.getMinTlsVersion()) >= 0 && tlsVersion
        .compareTo(this.sslParameter.getMaxTlsVersion()) <= 0)
        this.tlsVersions.add(tlsVersion); 
    } 
    if (this.tlsVersions.size() == 0)
      throw new InitializationFailedException("No TLS version recorded for Min-TLS-Version: " + this.sslParameter
          .getMinTlsVersion() + " and Max-TLS-Version: " + this.sslParameter
          .getMaxTlsVersion()); 
    Collections.reverse(this.tlsVersions);
    loadKeystore();
    loadServerCertificate();
    initSSLContext();
  }
  
  private void loadKeystore() throws InitializationFailedException {
    try {
      this.keyStore = KeyStore.getInstance("JKS");
      this.keyStore.load(null, "horizon".toCharArray());
    } catch (Exception ex) {
      throw new InitializationFailedException("Error loading keystore", ex);
    } 
  }
  
  private void loadServerCertificate() throws InitializationFailedException {
    try {
      ServerCertificateProfile serverCertificateProfile = this.sslParameter.getServerCertificateProfile();
      if (serverCertificateProfile == null)
        return; 
      PrivateKey privateKey = serverCertificateProfile.getPrivateKey();
      Collection<? extends Certificate> certificates = this.sslParameter.getServerCertificateProfile().getCertificates();
      Certificate[] serverCertificate = new Certificate[certificates.size()];
      serverCertificate = certificates.<Certificate>toArray(serverCertificate);
      this.keyStore.setKeyEntry("Pk_" + privateKey.hashCode(), privateKey, "horizon".toCharArray(), serverCertificate);
    } catch (Exception e) {
      throw new InitializationFailedException("Error loading servercertificate in keystore", e);
    } 
  }
  
  private void initSSLContext() throws InitializationFailedException {
    try {
      this.sslContext = SSLContext.getInstance((this.sslParameter.getMaxTlsVersion()).version);
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(this.keyStore, "horizon".toCharArray());
      this.sslContext.init(keyManagerFactory.getKeyManagers(), (TrustManager[])new EliteTrustManager[] { this.eliteTrustManager }, new SecureRandom());
    } catch (Exception e) {
      throw new InitializationFailedException("Error creating ssl context", e);
    } 
  }
  
  public SSLServerSocketFactory getSSLServerSocketFactory() {
    return this.sslContext.getServerSocketFactory();
  }
  
  public SSLSocketFactory getSSLSocketFactory() {
    return this.sslContext.getSocketFactory();
  }
  
  public SSLSessionContext getClientSessionContext() {
    return this.sslContext.getClientSessionContext();
  }
  
  public SSLSessionContext getServerSessionContext() {
    return this.sslContext.getServerSessionContext();
  }
  
  public List<String> getEnabledCiphersuites() {
    return this.sslParameter.getEnabledCiphersuites(this.tlsVersions);
  }
  
  public boolean isClientCertificateRequestRequired() {
    return this.sslParameter.isClientCertificateRequestRequired();
  }
  
  public EliteSSLParameter getEliteSSLParameter() {
    return this.sslParameter;
  }
  
  public EliteTrustManager getTrustManager() {
    return this.eliteTrustManager;
  }
  
  public List<TLSVersion> getEnabledTLSVersion() {
    return this.tlsVersions;
  }
  
  public SSLSession getSSLSession(TLSConnectionMode tlsConnectionMode) {
    SSLSessionContext sslSessionContext = null;
    if (TLSConnectionMode.CLIENT == tlsConnectionMode) {
      sslSessionContext = this.sslContext.getClientSessionContext();
    } else if (TLSConnectionMode.SERVER == tlsConnectionMode) {
      sslSessionContext = this.sslContext.getServerSessionContext();
    } else {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("ELITE-SSL-CNTX", "Unable to provide SSLSession. Reason: Invalid TLSConnection Mode"); 
      return null;
    } 
    if (sslSessionContext == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("ELITE-SSL-CNTX", "Unable to provide SSLSession. Reason: SSL session context creation disabled by the SSLSocket"); 
      return null;
    } 
    Enumeration<byte[]> sessionIds = sslSessionContext.getIds();
    if (sessionIds == null || !sessionIds.hasMoreElements()) {
      LogManager.getLogger().warn("ELITE-SSL-CNTX", "Session-Id list is empty");
      return null;
    } 
    return sslSessionContext.getSession(sessionIds.nextElement());
  }
  
  public SSLContext getSslContext() {
    return this.sslContext;
  }
}
