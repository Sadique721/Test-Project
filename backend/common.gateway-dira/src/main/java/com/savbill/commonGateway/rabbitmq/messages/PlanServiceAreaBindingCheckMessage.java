package com.savbill.commonGateway.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class PlanServiceAreaBindingCheckMessage {

    List<Long> servicAreaIds = new ArrayList<>();



}
