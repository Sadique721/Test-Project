package com.savbill.cpm.modules.dashboard;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "SavbillTicketManagement-Service")
public interface TicketFeignClient {
    @GetMapping("/api/v1/TicketManagement/case/count")
    Map<String, Object> getTicketCount(@RequestHeader("Authorization") String authHeader, @RequestParam(name = "staffId",required = true) Integer staffId);
}
