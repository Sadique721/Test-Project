package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BatchPaymentDetailPojo {

    private Long batchId;
    private String batchName;
    private String invoiceCount;
    private String totalAmount;
    private String assignmentStatus;
    private String assignee;
    private String createdBy;
    private Integer staffId;
    private Integer nextStaffId;
    private String nextstaffname;
    private String batchStatus;
    private List<CreditPojo> creditDocumentList;
    private String remarks;

    private String filename;

    private Integer creditDocId;

    private Integer custId;

    private String assignedName;
}
