package com.savbill.integrationsystem.PaywayIntigration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusDto {

    @JsonProperty("TransactionId")
    private String TransactionId;

    @JsonProperty("AccountNo")
    private String AccountNo;

    @JsonProperty("Amount")
    private Double Amount;

    @JsonProperty("Channel")
    private String Channel;

    @JsonProperty("PhoneNumber")
    private String PhoneNumber;

    @JsonProperty("Name")
    private String Name;

    @JsonProperty("Email")
    private String Email;

    @JsonProperty("Message")
    private String Message;

    @JsonProperty("Status")
    private String Status;

    @JsonProperty("TransactionTime")
    private String TransactionTime;

    @JsonProperty("Forwarded")
    private String Forwarded;

    @JsonProperty("StatusCode")
    private String StatusCode;


}
