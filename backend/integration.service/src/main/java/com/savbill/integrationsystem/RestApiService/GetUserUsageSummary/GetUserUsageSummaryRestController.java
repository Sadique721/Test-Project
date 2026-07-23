package com.savbill.integrationsystem.RestApiService.GetUserUsageSummary;

import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserUsageSummaryService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummary;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class GetUserUsageSummaryRestController {

    @Autowired
    public RadiusClientService radiusClientService;

    @Autowired
    private GetUserUsageSummaryService getUserUsageSummaryService;

    @PostMapping("/getUserUssageSummary")
    public GenericDataDTO getUserUsageSummaryResponse(@RequestBody WsGetUserUsageSummary request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(SoapConstants.INTERNAL_ERROR); // Default response code
        genericDataDTO.setResponseMessage("Processing failed");
        try {
            log.info("Received request to get usage summary for subscriber: {}", request.getSubscriberId());
            List<WsGetUserUsageSummaryResponse> responses = getUserUsageSummaryService.getUserUsageSummary(request);

            if (responses == null || responses.isEmpty()) {
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                genericDataDTO.setResponseMessage("No data available");
                log.warn("No usage summary data found for subscriber: {}", request.getSubscriberId());
            } else {
                for (WsGetUserUsageSummaryResponse response : responses) {
                    if (response.getGetUserUsageSummary() != null && response.getGetUserUsageSummary().getResponseCode() == SoapConstants.SUCCESS_CODE) {
                        genericDataDTO.setData(response.getGetUserUsageSummary());
                        genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
                        genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
                        log.info("Successfully retrieved usage summary for subscriber: {}", request.getSubscriberId());
                    } else {
                        genericDataDTO.setResponseCode(response.getGetUserUsageSummary().getResponseCode());
                        genericDataDTO.setResponseMessage(response.getGetUserUsageSummary().getResponseMessage());
                        log.warn("No usage summary data available in response for subscriber: {}", request.getSubscriberId());
                    }
                }
            }
        } catch (Exception e) {
            genericDataDTO.setResponseMessage("An error occurred while processing the request: " + e.getMessage());
            log.error("Error occurred while processing the request for subscriber: {}", request.getSubscriberId(), e);
            e.printStackTrace(); // Optional: Log the stack trace for debugging
        }

        return genericDataDTO;
    }
}
