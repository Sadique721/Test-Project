package com.savbill.integrationsystem.AirtelIntigration;

import lombok.Data;

@Data
public class AirtelAuthorizationRequestDTO {

    private String client_id;

    private String client_secret;

    private String grant_type;
}
