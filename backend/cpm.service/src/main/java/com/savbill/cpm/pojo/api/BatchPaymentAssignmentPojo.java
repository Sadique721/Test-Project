package com.savbill.cpm.pojo.api;

import lombok.Data;

@Data
public class BatchPaymentAssignmentPojo
{
    private Long batchId;

    private Integer staffUserId;

    private Integer nextStaffUserId;

    private String remark;

    private String status;

    private String assignedStatus;
}
