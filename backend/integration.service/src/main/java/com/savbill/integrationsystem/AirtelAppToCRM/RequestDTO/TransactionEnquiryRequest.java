package com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO;

import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = AirtelValidateConstant.COMMAND)
public class TransactionEnquiryRequest {

    private String TXNID;
    private String MSISDN;

    @XmlElement(name = AirtelValidateConstant.TXNID)
    public String getTXNID() {
        return TXNID;
    }
    public void setTXNID(String txnid) {
        this.TXNID = txnid;
    }

    @XmlElement(name = AirtelValidateConstant.MSISDN)
    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String msisdn) {
        this.MSISDN = msisdn;
    }

}
