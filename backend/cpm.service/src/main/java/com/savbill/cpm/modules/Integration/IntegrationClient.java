package com.savbill.cpm.modules.Integration;

import com.savbill.cpm.modules.Integration.Pojo.CdataCustDetailsPojo;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.NMSIntegrationMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "SAVBILLINTEGRATIONSYSTEM-SERVICE",contextId = "SavbillIntegrationMicroService")
public interface IntegrationClient {


    @PostMapping("/api/v1/SavbillIntegrationSystem/nmsMaster/CdataProvisioning")
    String generateCdataAPIcall(@RequestHeader("Authorization") String token, @RequestBody CdataCustDetailsPojo cdataCustDetailsPojo);

    @PostMapping("/api/v1/SavbillIntegrationSystem/nmsIntegration/NMSProvisioning")
    String generateNMSAPICALL(@RequestHeader("Authorization") String token, @RequestBody NMSIntegrationMessage nmsIntegrationMessage);

    @PostMapping("/api/v1/SavbillIntegrationSystem/nmsIntegration/NMSUpdateWANConfig")
    String generateNMSUpdateWANConfig(@RequestHeader("Authorization") String token, @RequestBody NMSIntegrationMessage nmsIntegrationMessage);
}
