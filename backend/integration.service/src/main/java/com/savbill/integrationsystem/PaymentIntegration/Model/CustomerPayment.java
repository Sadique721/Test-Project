package com.savbill.integrationsystem.PaymentIntegration.Model;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustPayDTO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tbltpayment")
public class CustomerPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "orderid", nullable = false, length = 40)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @Column (name="custid")
    private Integer custId;

    @Column(name = "payment", nullable = false, length = 40)
    private Double payment;

    @Column(name = "status")
    private String status;

    @Column(name = "pgtransactionid")
    private String pgTransactionId;

    @Column(name = "linkid")
    private String linkId;

    @Column(name = "payment_date")
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime paymentDate;

    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "is_from_captive")
    private Boolean isFromCaptive = false;

    @Column(name="merchant_name")
    private String merchantName;

    @Column(name="transaction_date")
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime transactionDate;

    @Column(name="customer_user_name")
    private String customerUsername;

    @Column(name="mvnoid")
    private Integer mvnoid;

   @Column(name="buid")
   private Integer buid;

   @Column(name="creditdocid")
   private Integer creditDocumentId;

    @Column(name="paymentlink")
    private String paymentLink;

    @Column(name="checksum")
    private String checksum;

    @Column (name="partnerid")
    private Integer partnerId;

    @Column(name="partner_payment_id")
    private Integer partnerPaymentId;

    @Column(name = "customer_uuid")
    private String customerUUID;

    @Column(name = "is_scheduled",nullable = false)
    private Boolean isScheduled = false;

    @Column(name = "created_by_id")
    private Integer createdById;


    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Column(name = "is_advance_payment",nullable = false)
    private Boolean isAdvancePayment = false;

    @Column(name = "failure_description")
    private String failureDescription;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "wallet_amount")
    private Double walletAmount;

    @Column(name = "plan_price")
    private Double planPrice;

    @Column(name = "gateway_status")
    private String gatewayStatus;

    @Column(name = "payer_mobile_number")
    private String payerMobileNumber ;

    @Column(name = "auto_payment_initiator")
    private String autoPaymentInitiator ;

    @Column(name = "commission")
    private Double commission;

    @Column(name = "child_id")
    private Integer childId;

    @Column(name = "checkout_request_id")
    private String checkoutRequestId;




    public CustomerPayment() {
    }

    public CustomerPayment(CustPayDTO paymentDto) {
        this.custId = paymentDto.getCustId();
        this.payment = paymentDto.getPayment();
        this.status = paymentDto.getStatus();
        if(paymentDto.getCustomerUsername()!=null){
            this.customerUsername= paymentDto.getCustomerUsername();
        }if(paymentDto.getMerchantName()!=null){
            this.merchantName=paymentDto.getMerchantName();
        }
        if(paymentDto.getIsFromCaptive() != null){
            this.isFromCaptive = paymentDto.getIsFromCaptive();
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
