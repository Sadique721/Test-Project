package com.savbill.integrationsystem.RestApiService.addSubscriberAccountXML;

import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ServiceSubscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSubscriberAccountXMLDTO {
    private String name;
    private String activated;
    private String password;
    private String locationLock;
    private List<ServiceSubscription> serviceSubscriptions;

}
