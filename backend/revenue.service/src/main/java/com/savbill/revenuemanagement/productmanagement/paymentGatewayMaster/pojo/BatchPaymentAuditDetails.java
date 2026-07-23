package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo;

import lombok.Data;

@Data
public class BatchPaymentAuditDetails {
    private Long batchId;
    private String batchName;
    private String TeamName;
    private String StaffName;
    private String status;
    private String remark;
}
