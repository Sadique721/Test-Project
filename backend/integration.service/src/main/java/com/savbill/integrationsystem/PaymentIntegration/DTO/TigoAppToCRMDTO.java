package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.core.CommonConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = AirtelValidateConstant.COMMAND)
public class TigoAppToCRMDTO {
    private String type = CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_REQUEST;
    private String transId;
    private String msisdn;
    private String amount;
    private String companyName;
    /**
     * customerReference : Customer Account Number
     * */
    private String customerReference;
    private String senderName;

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.TXNID)
    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.MSISDN)
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.AMOUNT)
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.COMPANY_NAME)
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.CUSTOMER_REFERENCE)
    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.SENDER_NAME)
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
