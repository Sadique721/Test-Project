package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerPaymentDTO {


    private Integer customerId;

    private Integer partnerId;

    private Integer planId;

    private String requestFor;

    private Integer mvnoId;

    private Integer custServiceMappingId;

    private String amount;
    private String actualAmount;

    private String mobileNumber;

    private Boolean isFromCaptive;

    @JsonAlias({"customerUsername"})
    private String customerUserName;

    private Integer buid;

    private Integer partnerPaymentId;

    private String customerUUID;

    private String orderId;

    private String merchantName;

    private String status;

    private Integer invoiceId;

    private Boolean isBuyPlan;

    private Boolean isAdvancePayment;

    private String hash;

    private String accountNumber;

    private Double walletAmount;

    private Double planPrice;

    private String payerMobileNumber ;

    private String autoPaymentInitiator ;

    private String email;

    private Double commission;

    private String billAddressLine1;

    private String billAddressLine2;

    private String billToAddressCity;

    private String billToAddressState;

    private String billToAddressZip;

    private Integer childId;

    private String  orderType;

}
