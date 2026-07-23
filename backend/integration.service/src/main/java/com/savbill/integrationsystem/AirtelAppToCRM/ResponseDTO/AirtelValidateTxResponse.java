package com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO;

import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = AirtelValidateConstant.COMMAND)
public class AirtelValidateTxResponse {

    private int status;
    private String firstName;
    private String lastName;
    private String dueDate;
    private String ammount;
    private String currency;
    private String transcationId;
    private String message;


    @XmlElement(name = AirtelValidateConstant.STATUS)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    @XmlElement(name = AirtelValidateConstant.FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name = AirtelValidateConstant.LAST_NAME)
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlElement(name = AirtelValidateConstant.DUE_DATE)
    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @XmlElement(name = AirtelValidateConstant.AMOUNT)
    public String getAmmount() {
        return ammount;
    }
    public void setAmmount(String ammount) {
        this.ammount = ammount;
    }

    @XmlElement(name = AirtelValidateConstant.CURRENCY)
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @XmlElement(name = AirtelValidateConstant.TXN_ID)
    public String getTranscationId() {
        return transcationId;
    }

    public void setTranscationId(String transcationId) {
        this.transcationId = transcationId;
    }

    @XmlElement(name = AirtelValidateConstant.MESSAGE)
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
