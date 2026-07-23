package com.savbill.ticketmanagement.RabbitCallFromTicketToGW.Messages;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class ActivePlanListRespMessage {
    Integer customerId;
    List<Integer> activePlanListIds = new ArrayList<>();

//    public ActivePlanListRespMessage(List<Integer> activePlanListIds){
//        this.activePlanListIds = activePlanListIds;
//    }
}
