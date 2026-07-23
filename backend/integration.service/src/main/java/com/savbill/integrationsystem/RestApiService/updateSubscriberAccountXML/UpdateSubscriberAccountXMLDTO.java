package com.savbill.integrationsystem.RestApiService.updateSubscriberAccountXML;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubscriberAccountXMLDTO {
    private String name;
    private String activated;
    private String password;
    private String locationLock;
    private List<ServiceSubscriptionDTO> serviceSubscriptions;


}


