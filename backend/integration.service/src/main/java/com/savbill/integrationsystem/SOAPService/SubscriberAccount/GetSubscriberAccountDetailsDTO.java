package com.savbill.integrationsystem.SOAPService.SubscriberAccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSubscriberAccountDetailsDTO {
    private String custName;

    private String cStatus;

    private String password;

    private String locationLock;

    private String planId;

    private String creationDate;

}
