package com.savbill.notification.helper;

import lombok.Data;

@Data
public class TicketDTO {
    private  String caseNumber;
    private String caseStatus;

    public TicketDTO(String caseNumber, String caseStatus) {
        this.caseNumber = caseNumber;
        this.caseStatus = caseStatus;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public String getCaseStatus() {
        return caseStatus;
    }


}
