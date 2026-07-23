package com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML;

import com.savbill.integrationsystem.RestApiService.updateSubscriberAccountXML.ServiceSubscriptionDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceSubscription {  // Make this class public

    @XmlElement(name = "ServiceId")
    public String serviceId;

    @XmlElement(name = "Override")
    public List<Override> overrides;

    public ServiceSubscription(ServiceSubscription dto) {
        this.serviceId = dto.getServiceId();
        if (dto.getOverrides() != null) {
            this.overrides = dto.getOverrides().stream()
                    .map(Override::new)
                    .collect(Collectors.toList());
        }
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<Override> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<Override> overrides) {
        this.overrides = overrides;
    }
    public ServiceSubscription(ServiceSubscriptionDTO dto) {
        this.serviceId = dto.getServiceId();
        if (dto.getOverrides() != null) {
            this.overrides = dto.getOverrides().stream()
                    .map(Override::new)
                    .collect(Collectors.toList());
        }
    }

    public ServiceSubscription() {
    }
}
