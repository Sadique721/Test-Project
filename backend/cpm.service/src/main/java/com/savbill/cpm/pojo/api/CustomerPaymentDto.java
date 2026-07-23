package com.savbill.cpm.pojo.api;

import com.savbill.cpm.model.common.CustomerPayment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel(value = "Customer", description = "This is data transfer object for Payment which is used to add New PaymentInfo")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomerPaymentDto
{
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


    private Integer mvnoId;


    private Integer buid;

    private String pgTransactionId;

    private Integer customerId;

    private String amount;

    private Integer invoiceId;

    private String mobileNumber;

    private String customerUUID;

    private String accountNumber;

    public CustomerPaymentDto(CustomerPayment customerPayment) {
        setOrderId(customerPayment.getOrderId().toString());
        setPayment(customerPayment.getPayment());
        setCustId(customerPayment.getCustId());
        setStatus(customerPayment.getStatus());
        setIsFromCaptive(customerPayment.getIsFromCaptive());
        setMerchantName(customerPayment.getMerchantName());
        setCustomerUsername(customerPayment.getCustomerUsername());
        setTransactionDate(customerPayment.getTransactionDate());
        setPaymentDate(customerPayment.getPaymentDate());
        setAccountNumber(customerPayment.getAccountNumber());
    }

    public CustomerPaymentDto() {

    }
}
