package com.savbill.revenuemanagement.FeignClient;


import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanRequestDto;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SAVBILLAPIGATEWAYBSS-SERVICE",contextId = "SavbillCustomermanagementService")
public interface CMSClient {
    @PostMapping("/api/v1/cms/subscriber/autoApprovalPayment")
    GenericDataDTO autoApprovalPayment(@RequestBody AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto, @RequestHeader("Authorization") String token);

}
