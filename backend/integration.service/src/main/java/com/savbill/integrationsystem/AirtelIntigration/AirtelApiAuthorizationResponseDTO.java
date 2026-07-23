package com.savbill.integrationsystem.AirtelIntigration;

import lombok.Data;

@Data
public class AirtelApiAuthorizationResponseDTO {

    private String token_type;

    private String access_token;

    private String expires_in;


}
