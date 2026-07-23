package com.savbill.radius.aaa.eap;


import com.savbill.radius.aaa.eap.data.TLSSecurityParameter;
import com.savbill.radius.aaa.eap.util.DHKeyExchange;

//TODO: using this class as session and value of hash map
public class EAPSession {

    private String identity;
    private byte[] bufferData = new byte[0];

    private byte[] toBeSendData;

    private int tlsDataLengthToBeSend;

    private byte[] stream;

    private boolean isServerFinishedDone;

    private DHKeyExchange dheKeyExchange;

    private TLSSecurityParameter tlsSecurityParameter = new TLSSecurityParameter();

    private int authenticationType;

    public EAPSession(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public byte[] getBufferData() {
        return bufferData;
    }

    public void setBufferData(byte[] bufferData) {
        this.bufferData = bufferData;
    }

    public TLSSecurityParameter getTlsSecurityParameter() {
        return tlsSecurityParameter;
    }

    public byte[] getToBeSendData() {
        return toBeSendData;
    }

    public void setToBeSendData(byte[] toBeSendData) {
        this.toBeSendData = toBeSendData;
    }

    public void setTlsSecurityParameter(TLSSecurityParameter tlsSecurityParameter) {
        this.tlsSecurityParameter = tlsSecurityParameter;
    }

    public int getTlsDataLengthToBeSend() {
        return tlsDataLengthToBeSend;
    }

    public void setTlsDataLengthToBeSend(int tlsDataLengthToBeSend) {
        this.tlsDataLengthToBeSend = tlsDataLengthToBeSend;
    }

    public byte[] getStream() {
        return stream;
    }

    public void setStream(byte[] stream) {
        this.stream = stream;
    }

    public DHKeyExchange getDheKeyExchange() {
        return dheKeyExchange;
    }

    public void setDheKeyExchange(DHKeyExchange dheKeyExchange) {
        this.dheKeyExchange = dheKeyExchange;
    }

    public boolean isServerFinishedDone() {
        return isServerFinishedDone;
    }

    public void setServerFinishedDone(boolean serverFinishedDone) {
        isServerFinishedDone = serverFinishedDone;
    }

    public int getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(int authenticationType) {
        this.authenticationType = authenticationType;
    }
}
