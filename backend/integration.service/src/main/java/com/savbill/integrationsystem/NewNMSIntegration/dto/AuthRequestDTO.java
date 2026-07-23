package com.savbill.integrationsystem.NewNMSIntegration.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String grantType;
    private String userName;
    private String value;

    public AuthRequestDTO() {}

    public AuthRequestDTO(String grantType, String userName, String value) {
        this.grantType = grantType;
        this.userName = userName;
        this.value = value;
    }
}
