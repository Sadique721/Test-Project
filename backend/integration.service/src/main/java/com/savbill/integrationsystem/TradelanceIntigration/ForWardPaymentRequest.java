package com.savbill.integrationsystem.TradelanceIntigration;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ForWardPaymentRequest {
    @JsonProperty("AccountNo")
    @NotBlank(message = "AccountNo cannot be null or empty")
    private String accountNo;

    @JsonProperty("PhoneNumber")
    @NotBlank(message = "PhoneNumber cannot be null or empty")
    private String phoneNumber;

    @JsonProperty("TransactionId")
    @NotBlank(message = "TransactionId cannot be null or empty")
    private String transactionId;

    @JsonProperty("Amount")
    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount cannot be null")
    private Double amount;

    @JsonProperty("Currency")
    @NotBlank(message = "Currency cannot be null or empty")
    private String currency;

    @JsonProperty("Channel")
    @NotBlank(message = "Channel cannot be null or empty")
    private String channel;

    @JsonProperty("Status")
    @NotBlank(message = "Status cannot be null or empty")
    private String status;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("TransactionTime")
    @NotNull(message = "TransactionTime cannot be null or empty")
    private long transactionTime;
}
