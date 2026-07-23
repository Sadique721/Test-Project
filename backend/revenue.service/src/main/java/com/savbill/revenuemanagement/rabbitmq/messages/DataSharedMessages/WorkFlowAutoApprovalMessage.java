package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkFlowAutoApprovalMessage {

    Integer customerId ;
    String triggeredAction;
    Integer mvnoId;
    Integer buId;

}
