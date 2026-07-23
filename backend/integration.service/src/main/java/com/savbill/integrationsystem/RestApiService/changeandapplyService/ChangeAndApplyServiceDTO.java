package com.savbill.integrationsystem.RestApiService.changeandapplyService;

import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ServiceSubscriptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangeAndApplyServiceDTO {
    @JsonProperty("String_1")
    private String string_1;
    @JsonProperty("String_2")
    private ServiceSubscriptions string_2;
}
