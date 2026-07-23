package com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO;

import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = AirtelValidateConstant.COMMAND)
public class TransactionEnquiryResponse {
    private int status;
    private String Message;
    private String Refrence;

    @XmlElement(name = AirtelValidateConstant.STATUS)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    @XmlElement(name = AirtelValidateConstant.MESSAGE)
    public String getMessage() {
        return Message;
    }
    public void setMessage(String message) {
        this.Message = message;
    }

    @XmlElement(name = AirtelValidateConstant.REFERENCE)
    public String getReference() {
        return Refrence;
    }
    public void setReference(String reference) {
        this.Refrence = reference;
    }

}
