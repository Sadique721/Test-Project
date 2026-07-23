package com.savbill.revenuemanagement.rabbitmq.messages;


import lombok.Data;

@Data
public class SaveVoucherBatchSharedDataMessage {

    private Long voucherBatchId;

    private String batchName;

    private Long voucherProfileId;

    private Integer planId;

    private Long resellerId;

    private String createDate;

    private Integer voucherQuantity;

    private Double price;

    private Long mvnoId;

    private Long buId;

    private Integer partnerId;

}
