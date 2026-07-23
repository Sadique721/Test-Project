package com.savbill.cpm.modules.dashboard;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "SavbillSalesCrmsBss-Service")
public interface LeadFeignClient {
    @GetMapping("/api/v1/SavbillSalesCrmsBss/leadMaster/countByCurrentUser")
    Map<String, Object> getLeadCount(@RequestHeader("Authorization") String authHeader, @RequestParam(name = "staffId",required = true) Integer staffId);

}
