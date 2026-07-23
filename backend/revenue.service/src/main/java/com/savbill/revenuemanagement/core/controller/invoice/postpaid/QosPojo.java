package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import lombok.Data;

@Data
public class QosPojo {

    Long qosPolicyId;
    String qosName;

    public QosPojo(Long qosPolicyId, String qosName) {
        this.qosPolicyId = qosPolicyId;
        this.qosName = qosName;
    }
}
