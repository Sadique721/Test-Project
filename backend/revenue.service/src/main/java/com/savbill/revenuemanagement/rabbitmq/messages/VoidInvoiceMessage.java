package com.savbill.revenuemanagement.rabbitmq.messages;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VoidInvoiceMessage {
    List<Integer> cprIdlist=new ArrayList<>();
}

