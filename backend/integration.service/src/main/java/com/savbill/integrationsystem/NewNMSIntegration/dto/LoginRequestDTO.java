package com.savbill.integrationsystem.NewNMSIntegration.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginRequestDTO {
    private String grantType;
    private String userName;
    private String value;
    private String baseURL;
    private String port;

}