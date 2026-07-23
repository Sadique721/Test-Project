package com.savbill.commonGateway.common.Interfaces;


import com.savbill.commonGateway.core.dto.GenericDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "SavbillCommonGateway-Service")
public interface CmsClient {


    //http://localhost:30085/api/v1/cpm/getTested
    @GetMapping("/api/v1/cpm/workflowaudit/getWorkflowInProgressData/")
    GenericDataDTO callWorkFlowInProgressFunction(@RequestHeader("Authorization") String token, @RequestParam(name = "mvnoid", required = false) Integer mvnoid);

    @GetMapping("/api/v1/cpm/address/getListOfUsedBuildingNumber/{buildingMgmtId}")
    GenericDataDTO getUsedBuildingIds(@RequestHeader("Authorization") String token, @PathVariable(name = "buildingMgmtId", required = true) Integer buildingMgmtId);
}
