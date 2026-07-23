package com.savbill.integrationsystem.RestApiService.updateSubscriberAccountXML;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSubscriptionDTO {

    private String serviceId;
    private List<Override> overrides;
}
