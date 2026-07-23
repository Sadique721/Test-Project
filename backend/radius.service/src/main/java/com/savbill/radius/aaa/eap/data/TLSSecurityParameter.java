package com.savbill.radius.aaa.eap.data;

public class TLSSecurityParameter {

    private byte[] clientRandom;
    private byte[] serverRandom;

    private byte[] preMasterKey;

    private byte[] privateKey;

    private byte[] masterKey;

    private  int cipherSuite;

    private SecurityKeys securityKeys;

    public byte[] getClientRandom() {
        return clientRandom;
    }

    public void setClientRandom(byte[] clientRandom) {
        this.clientRandom = clientRandom;
    }

    public byte[] getServerRandom() {
        return serverRandom;
    }

    public void setServerRandom(byte[] serverRandom) {
        this.serverRandom = serverRandom;
    }

    public byte[] getPreMasterKey() {
        return preMasterKey;
    }

    public void setPreMasterKey(byte[] preMasterKey) {
        this.preMasterKey = preMasterKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(byte[] masterKey) {
        this.masterKey = masterKey;
    }

    public int getCipherSuite() {
        return cipherSuite;
    }

    public void setCipherSuite(int cipherSuite) {
        this.cipherSuite = cipherSuite;
    }

    public SecurityKeys getSecurityKeys() {
        return securityKeys;
    }

    public void setSecurityKeys(SecurityKeys securityKeys) {
        this.securityKeys = securityKeys;
    }
}
