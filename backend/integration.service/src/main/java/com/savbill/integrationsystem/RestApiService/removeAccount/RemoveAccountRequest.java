package com.savbill.integrationsystem.RestApiService.removeAccount;

import com.savbill.integrationsystem.generated.wsremoveaccount.WsRemoveAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveAccountRequest {
    private String actionItem;
    private String requestId;
    private String userName;

    public RemoveAccountRequest(WsRemoveAccount request) {
        this.actionItem = request.getActionItem();
        this.userName = request.getUserName();
        this.requestId = request.getRequestId();
    }
}
