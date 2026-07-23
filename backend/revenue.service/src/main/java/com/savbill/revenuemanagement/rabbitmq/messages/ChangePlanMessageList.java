package com.savbill.revenuemanagement.rabbitmq.messages;

import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.ChangePlanMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChangePlanMessageList {
    List<ChangePlanMessage> changePlanMessageList = new ArrayList<>();
}
