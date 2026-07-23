package com.diameter.dto;

public class DiameterPeerResponse {

    private String peerName;
    private String hostIdentity;
    private String ipAddress;
    private Integer port;
    private String state;
    private Boolean connected;

    public DiameterPeerResponse() {
    }

    public DiameterPeerResponse(String peerName,
                                String hostIdentity,
                                String ipAddress,
                                Integer port,
                                String state,
                                Boolean connected) {
        this.peerName = peerName;
        this.hostIdentity = hostIdentity;
        this.ipAddress = ipAddress;
        this.port = port;
        this.state = state;
        this.connected = connected;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getHostIdentity() {
        return hostIdentity;
    }

    public void setHostIdentity(String hostIdentity) {
        this.hostIdentity = hostIdentity;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }
}
