package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditPojo {

    private Long mappingId;

    private Integer creditDocumentId;

    private String paymode;

    private LocalDate paymentdate;

    private Double amount;

    private String status;

    private String customerName;

    private Long BatchPaymentId;

    private Double tdsAmount;

    private Double abbsAmount;
}
