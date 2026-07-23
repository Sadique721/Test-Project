package com.savbill.integrationsystem.mvno;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IspDto {
    @NotBlank(message = "Name cannot be null or empty")
    private String name;

    @NotBlank(message = "Description cannot be null or empty")
    private String description;

    @NotBlank(message = "Email cannot be null or empty")
    private String email;

    @NotBlank(message = "Address cannot be null or empty")
    private String address;

    @NotBlank(message = "Full Name cannot be null or empty")
    private String fullName;

    @NotBlank(message = "Client ID cannot be null or empty")
    private String clientId;

}
