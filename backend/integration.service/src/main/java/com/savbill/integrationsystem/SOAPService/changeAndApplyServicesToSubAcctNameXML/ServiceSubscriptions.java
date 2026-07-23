package com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceSubscriptions {

    @XmlElement(name = "ServiceSubscription")
    private List<ServiceSubscription> serviceSubscriptions = new ArrayList<>(); // Initialize to avoid null

    public List<ServiceSubscription> getServiceSubscriptions() {
        return serviceSubscriptions;
    }

    public void setServiceSubscriptions(List<ServiceSubscription> serviceSubscriptions) {
        this.serviceSubscriptions = serviceSubscriptions;
    }
}

