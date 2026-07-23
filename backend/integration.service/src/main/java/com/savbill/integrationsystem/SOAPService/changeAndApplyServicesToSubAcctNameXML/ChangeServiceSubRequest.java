package com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML;

import com.savbill.integrationsystem.RestApiService.changeandapplyService.ChangeAndApplyServiceDTO;

import com.savbill.integrationsystem.generated.changeandapplyservicestosubacctnamexml.ChangeAndApplyServicesToSubAcctNameXML;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeServiceSubRequest {
    private String userName;
    private List<Override> overrides;
    private String serviceId;

    public ChangeServiceSubRequest(ChangeAndApplyServicesToSubAcctNameXML request, ServiceSubscriptions serviceSubscription) {
        this.userName = request.getString1();
        this.overrides = serviceSubscription.getServiceSubscriptions().get(0).getOverrides();
        this.serviceId = serviceSubscription.getServiceSubscriptions().get(0).getServiceId();
    }


    public ChangeServiceSubRequest(ChangeAndApplyServiceDTO request, ServiceSubscriptions serviceSubscriptions) {
        this.userName = request.getString_1();
        List<ServiceSubscription> subscription = new ArrayList<>();
        this.overrides = serviceSubscriptions.getServiceSubscriptions().get(0).getOverrides();
        this.serviceId = serviceSubscriptions.getServiceSubscriptions().get(0).getServiceId();

    }
}