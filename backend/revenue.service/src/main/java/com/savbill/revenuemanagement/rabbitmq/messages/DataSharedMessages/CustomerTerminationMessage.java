package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import lombok.Data;

@Data
public class CustomerTerminationMessage {
    private Integer custId;
    private String status;
    private Boolean generateCreditnote;

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        status = status;
    }

    public Boolean getGenerateCreditnote() {
        return generateCreditnote;
    }

    public void setGenerateCreditnote(Boolean generateCreditnote) {
        generateCreditnote = generateCreditnote;
    }
}
