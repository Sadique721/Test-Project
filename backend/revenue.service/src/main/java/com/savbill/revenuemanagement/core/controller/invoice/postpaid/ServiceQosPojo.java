package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import lombok.Data;

import java.util.List;

@Data
public class ServiceQosPojo {

    String serviceName;
    List<QosNameCount> qosNameCounts;
    List<ServiceTotalAmount> serviceTotalAmount;
}
