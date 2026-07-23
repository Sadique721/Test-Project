package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbrHoldResumeMessage {

    private List<Long> cprIds;
    private Boolean isServiceHold;
}
