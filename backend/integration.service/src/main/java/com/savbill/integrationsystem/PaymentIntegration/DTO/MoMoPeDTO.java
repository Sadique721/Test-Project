package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoPeDTO {

    private String referenceId;

    private String gatewayUrl;

    private String subscriptionKey;

    private String callBackUrl;

    private String targetEnvironment;

    private String jsonPayload;

    private String apiKey;

    private String apiUser;

}
