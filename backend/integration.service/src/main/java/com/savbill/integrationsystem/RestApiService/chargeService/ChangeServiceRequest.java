package com.savbill.integrationsystem.RestApiService.chargeService;


import com.savbill.integrationsystem.generated.changeservice.WsChangeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeServiceRequest {
    private String actionItem;
    private String requestId;
    private String userName;
    private Double overrides;
    private String serviceId;

    public ChangeServiceRequest(WsChangeService request) {
        this.actionItem = request.getActionItem();
        this.userName = request.getUserName();
        this.requestId = request.getRequestId();
        this.overrides = request.getOverrides();
        this.serviceId = request.getServiceId();
    }
}
