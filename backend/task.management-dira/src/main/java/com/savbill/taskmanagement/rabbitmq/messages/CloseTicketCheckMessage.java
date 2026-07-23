package com.savbill.taskmanagement.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseTicketCheckMessage {

    HashMap<String, Object> customerMessage = new HashMap<>();


    public CloseTicketCheckMessage(Integer staffId, Integer caseId, String caseNumber, String status) {
        this.staffId = staffId;
        this.caseId = caseId;
        this.caseNumber = caseNumber;
        this.status = status;
    }

    Integer staffId ;
    Integer caseId;
    String caseNumber;
    String status;
    Integer customerId;



}
