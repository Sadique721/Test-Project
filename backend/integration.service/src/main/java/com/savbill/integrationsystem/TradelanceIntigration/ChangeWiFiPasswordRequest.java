package com.savbill.integrationsystem.TradelanceIntigration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChangeWiFiPasswordRequest {
    @JsonProperty("AccountNo")
    @NotBlank(message = "AccountNo cannot be null or empty")
    private String accountNo;

    @JsonProperty("PhoneNumber")
    @NotBlank(message = "PhoneNumber cannot be null or empty")
    private String phoneNumber;

    @JsonProperty("SSID")
    @NotBlank(message = "SSID cannot be null or empty")
    private String ssid;

    @JsonProperty("Password")
    @NotBlank(message = "Password cannot be null or empty")
    private String password;
}
