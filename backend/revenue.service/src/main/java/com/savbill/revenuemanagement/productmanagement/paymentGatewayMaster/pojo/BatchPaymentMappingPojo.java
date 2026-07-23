package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "BatchPaymentMapping", description = "This is data transfer object for Batch payment Mapping which is used to add New Batch Payment Mapping")
@Data
public class BatchPaymentMappingPojo
{
    @ApiModelProperty(notes = "id of the batch Payment Mapping",hidden = true)
    private Long id;

    @ApiModelProperty(notes = "credit document id for the batch Payment Mapping",hidden = false)
    private Long credit_doc_id;

    @ApiModelProperty(notes = "batch id for batch Payment Mapping",hidden = true)
    private BatchPaymentPojo batchPayment;
}
