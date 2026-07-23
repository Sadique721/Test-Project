package com.diameter.commons;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DiameterCertificateSubjectCnChecker extends CertificateSubjectCnChecker {
  private PeerData peerData;
  
  public DiameterCertificateSubjectCnChecker(boolean ignoreException, PeerData peerData) {
    super(ignoreException);
    this.peerData = peerData;
  }
  
  public List<String> getPossibleSubjectCN() {
    ArrayList<String> subjectsCN = new ArrayList<>();
    String name = this.peerData.getPeerName();
    if (name != null)
      subjectsCN.add(name); 
    String hostIdentity = this.peerData.getHostIdentity();
    if (hostIdentity != null)
      subjectsCN.add(hostIdentity); 
    InetAddress inetAddress = this.peerData.getRemoteInetAddress();
    if (inetAddress != null) {
      subjectsCN.add(inetAddress.getHostName());
      subjectsCN.add(inetAddress.getHostAddress());
    } 
    return subjectsCN;
  }
}
