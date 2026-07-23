package com.savbill.integrationsystem.mvno;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SAVBILLAPIGATEWAYCOMMON-SERVICE",contextId = "SavbillApiGatewayService")
public interface ApiGatewayClient {


    @PostMapping("/api/v1/SavbillApiGateWayCommon/mvno/save")
    GenericDataDTO saveMvno(@RequestBody MvnoDTO mvnoDTO, @RequestHeader("Authorization") String token);
}
