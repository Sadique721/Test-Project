package com.savbill.integrationsystem.Mpesa.ResponseDTO;

import com.savbill.integrationsystem.Mpesa.Constants.ValidateMpesaConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MpesaBrokerResponse")
public class MpesaBrokerResponseDTO {

    private String conversationID;
    private String originatorConversationID;
    private String responseCode;
    private String transactionID;
    private Long serviceID;
    private String serviceStatus;
    private String responseDesc;

    @XmlElement(name = ValidateMpesaConstant.CONVERSATION_ID)
    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    @XmlElement(name = ValidateMpesaConstant.ORIGINATOR_CONVERSATION_ID)
    public String getOriginatorConversationID() {
        return originatorConversationID;
    }

    public void setOriginatorConversationID(String originatorConversationID) {
        this.originatorConversationID = originatorConversationID;
    }

    @XmlElement(name = ValidateMpesaConstant.RESPONSE_CODE)
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @XmlElement(name = ValidateMpesaConstant.TRANSACTION_ID)
    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    @XmlElement(name = ValidateMpesaConstant.SERVICE_ID)
    public Long getServiceID() {
        return serviceID;
    }

    public void setServiceID(Long serviceID) {
        this.serviceID = serviceID;
    }

    @XmlElement(name = ValidateMpesaConstant.SERVICE_STATUS)
    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    @XmlElement(name = ValidateMpesaConstant.SERVICE_DESC)
    public String getResponseDesc() {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }
}

