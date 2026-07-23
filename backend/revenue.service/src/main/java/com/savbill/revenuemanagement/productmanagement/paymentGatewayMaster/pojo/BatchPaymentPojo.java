package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchPaymentPojo
{
    private Long id;

    private String batchName;

    private List<BatchPaymentMappingPojo> batchPaymentMappingList=new ArrayList<>();

    @ApiModelProperty(notes = "Batch Payment CreatedBy",hidden = true)
    private String createBy;

    @ApiModelProperty(notes = "Batch Payment Assigned or not",allowableValues = "Not Assigned, Assigned")
    private String assignedStatus;
}
