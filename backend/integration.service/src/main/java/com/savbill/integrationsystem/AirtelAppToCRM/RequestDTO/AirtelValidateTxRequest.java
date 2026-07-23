package com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO;

import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = AirtelValidateConstant.COMMAND)
public class AirtelValidateTxRequest {

    private String customerMsisdn;
    private String merchantMsisdn;
    private String customerName;
    private String amount;
    private String pin;
    private String reference;
    private String username;
    private String password;
    private String reference1;
    private String reference2;
    private String type;
    private String customerRef;

    @XmlElement(name = AirtelValidateConstant.CUSTOMERMS_ISDN)
    public String getCustomerMsisdn() {
        return customerMsisdn;
    }

    public void setCustomerMsisdn(String customerMsisdn) {
        this.customerMsisdn = customerMsisdn;
    }

    @XmlElement(name = AirtelValidateConstant.MERCHANTMS_ISDN)
    public String getMerchantMsisdn() {
        return merchantMsisdn;
    }

    public void setMerchantMsisdn(String merchantMsisdn) {
        this.merchantMsisdn = merchantMsisdn;
    }

    @XmlElement(name = AirtelValidateConstant.CUSTOMERNAME)
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @XmlElement(name = AirtelValidateConstant.AMOUNT)
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @XmlElement(name = AirtelValidateConstant.PIN)
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @XmlElement(name = AirtelValidateConstant.REFERENCE)
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @XmlElement(name = AirtelValidateConstant.USER_NAME)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement(name = AirtelValidateConstant.PASSWORD)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement(name = AirtelValidateConstant.REFERENCE_1)
    public String getReference1() {
        return reference1;
    }

    public void setReference1(String reference1) {
        this.reference1 = reference1;
    }

    @XmlElement(name = AirtelValidateConstant.REFERENCE_2)
    public String getReference2() {
        return reference2;
    }

    public void setReference2(String reference2) {
        this.reference2 = reference2;
    }

    @XmlElement(name = AirtelValidateConstant.TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = AirtelValidateConstant.CUSTOMER_REF)
    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

}
