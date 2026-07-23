package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum PrivateKeyAlgo {
  RSA("RSA"),
  DHA("DiffieHellman"),
  DSA("DSA");
  
  public final String keyAlgoName;
  
  private static Map<String, PrivateKeyAlgo> privateKeyAlgos;
  
  static {
    privateKeyAlgos = new HashMap<>();
    for (PrivateKeyAlgo privateKeyAlgo : values())
      privateKeyAlgos.put(privateKeyAlgo.keyAlgoName, privateKeyAlgo); 
  }
  
  PrivateKeyAlgo(String keyAlgoName) {
    this.keyAlgoName = keyAlgoName;
  }
  
  public static PrivateKeyAlgo fromAlgoName(String algoName) {
    return privateKeyAlgos.get(algoName);
  }
}
