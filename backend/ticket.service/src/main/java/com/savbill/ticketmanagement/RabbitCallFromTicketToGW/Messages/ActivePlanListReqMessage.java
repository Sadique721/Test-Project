package com.savbill.ticketmanagement.RabbitCallFromTicketToGW.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Data
@NoArgsConstructor
public class ActivePlanListReqMessage {
    private Integer customerId;

    public ActivePlanListReqMessage(Integer customerId){

        this.customerId =customerId;
    }
}
