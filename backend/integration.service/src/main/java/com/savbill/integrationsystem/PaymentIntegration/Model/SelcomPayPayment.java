package com.savbill.integrationsystem.PaymentIntegration.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelcomPayPayment {

    private String vendor;
    private String amount;
    private String currency;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("buyer_email")
    private String buyerEmail;
    @JsonProperty("buyer_name")
    private String buyerName;
    @JsonProperty("buyer_phone")
    private String buyerPhone;
    @JsonProperty("gateway_buyer_uuid")
    private String gatewayBuyerUuid;
    @JsonProperty("payment_methods")
    private String paymentMethods;
//    @JsonProperty("redirect_url")
//    private String redirectUrl;
    @JsonProperty("webhook")
    private String webHook;
    @JsonProperty("billing.firstname")
    private String billingFirstName;
    @JsonProperty("billing.lastname")
    private String billingLastName;
    @JsonProperty("billing.address_1")
    private String billingAddress1;
    @JsonProperty("billing.city")
    private String billingCity;
    @JsonProperty("billing.state_or_region")
    private String billingStateOrRegion;
    @JsonProperty("billing.country")
    private String billingCountry;
    @JsonProperty("billing.phone")
    private String billingPhone;
    @JsonProperty("no_of_items")
    private Long noOfItems;



}
