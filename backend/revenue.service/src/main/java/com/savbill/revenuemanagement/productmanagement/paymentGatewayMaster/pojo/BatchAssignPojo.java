package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo;

import lombok.Data;

@Data
public class BatchAssignPojo {
    private Integer staffId;
    private Integer nextStaffId;
    private Long batchId;
    private String remark;
}
