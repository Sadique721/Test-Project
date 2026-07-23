package com.savbill.integrationsystem.RestApiService.UpdateUserUsage;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsupdateuserusage.WsUpdateUserUsage;
import com.savbill.integrationsystem.generated.wsupdateuserusage.WsUpdateUserUsageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class UpdateUserUsageController {
    private final Logger logger = LoggerFactory.getLogger(UpdateUserUsageController.class);

    @Autowired
    RadiusClientService radiusClientService;
    @Autowired
    private UpdateUserUsageService updateUserUsageService;

    @PostMapping("/updateUserUsage")
    public GenericResponse<Object> handleUpdateUserUsage(@RequestBody WsUpdateUserUsage request) {
        Map<String, Object> responseData = new HashMap<>();
        GenericResponse<Object> response = new GenericResponse<>();
        String userName = request.getUserName().trim();
        logger.info("Received request to update user usage: {}", request.getUserName());
        try {
            WsUpdateUserUsageResponse wsUpdateUserUsageResponse = updateUserUsageService.handleUpdateUserUsage(request);
            logger.info("User usage update processed successfully for user: {} byte{}:", userName, request.getUsageBytes());
            response.setData(wsUpdateUserUsageResponse);
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while updating user usage for user: {}. Error: {}", userName, e.getMessage());
            e.printStackTrace();
            response.setData(responseData);
            return response;
        }
    }
}
