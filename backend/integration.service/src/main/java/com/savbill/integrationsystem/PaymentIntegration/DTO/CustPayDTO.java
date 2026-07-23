package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@ApiModel(value = "Customer", description = "This is data transfer object for Payment which is used to add New PaymentInfo")
@Data
public class CustPayDTO {

    @ApiModelProperty(notes = "Status of the customer",hidden = true)
    private String orderId;

    @ApiModelProperty(notes = "Id of the customer", required = true)
    private Integer custId;

    @ApiModelProperty(notes = "Payment of the customer", required = true)
    private Double payment;

    @ApiModelProperty(notes = "Status of the customer", allowableValues = "Initiate,Success,Failure", value = "This field accept value only : Active or Inactive", required = true)
    private String status;

    @ApiModelProperty(notes = "Purchased plan id of customer")
    private Long planId;

    private String linkId;

    private Boolean isFromCaptive;


    private String merchantName;


    private LocalDateTime transactionDate;


    private String customerUsername;

    private LocalDateTime paymentDate;


    private Integer mvnoid;


    private Integer buid;


    public CustPayDTO(CustomerPayment customerPayment) {
        setOrderId(customerPayment.getOrderId().toString());
        setPayment(customerPayment.getPayment());
        setCustId(customerPayment.getCustId());
        setStatus(customerPayment.getStatus());
        setIsFromCaptive(customerPayment.getIsFromCaptive());
        setMerchantName(customerPayment.getMerchantName());
        setCustomerUsername(customerPayment.getCustomerUsername());
        setTransactionDate(customerPayment.getTransactionDate());
        setPaymentDate(customerPayment.getPaymentDate());
    }

    public CustPayDTO() {

    }
}
