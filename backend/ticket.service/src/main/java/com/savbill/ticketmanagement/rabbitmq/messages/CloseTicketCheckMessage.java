package com.savbill.ticketmanagement.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloseTicketCheckMessage {

    HashMap<String, Object> customerMessage = new HashMap<>();
    Integer customerId ;
    Integer caseId;
    String caseNumber;
    String status;
    Integer staffId;

    public CloseTicketCheckMessage(Integer customerId, Integer caseId, String caseNumber, String status) {
        this.customerId = customerId;
        this.caseId = caseId;
        this.caseNumber = caseNumber;
        this.status = status;
    }






}
