package com.savbill.revenuemanagement.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustPayDTOMessage {

    private Long id;

    private Long orderId;

    private Integer custId;

    private Double payment;

    private String status;

    private String pgTransactionId;

    private String linkId;

    private String paymentDate;

    private Integer planId;

    private Boolean isFromCaptive = false;

    private String merchantName;

    private String transactionDate;

    private String customerUsername;

    private Integer mvnoid;

    private Integer buid;

    private Integer creditDocumentId;

    private String paymentLink;

    private String checksum;

    private Integer partnerId;

    private Integer partnerPaymentId;

    private String customerUUID;

    private String paymentGatewayName;

    private Integer custServiceMappingId;

    private Integer createdById;

    private String createdByName;

    private Integer invoiceId;

    private String accountNumber;

    private Integer childId;

}
