package com.savbill.integrationsystem.RestApiService.UpdateUserUsage;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.wsupdateuserusage.WsUpdateUserUsage;
import com.savbill.integrationsystem.generated.wsupdateuserusage.WsUpdateUserUsageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UpdateUserUsageService {
    @Autowired
    RadiusClientService radiusClientService;

    public WsUpdateUserUsageResponse handleUpdateUserUsage(WsUpdateUserUsage request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: handleUpdateUserUsage AT:{}", new Date(startTime));
        WsUpdateUserUsageResponse response = new WsUpdateUserUsageResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        WsUpdateUserUsageResponse.UpdateUserUsage updateUserUsage = new WsUpdateUserUsageResponse.UpdateUserUsage();
        updateUserUsage.setRequestId(requestId);
        String userName = request.getUserName().trim();

        try {
            if (!userName.trim().equals("")) {
                double bytes = request.getUsageBytes();
                log.debug("Usage bytes received: {}", bytes);

                if (bytes <= 0) {
                    log.warn("Usage bytes are empty or null");
                    updateUserUsage.setRequestId(requestId);
                    updateUserUsage.setResponeCode(SoapConstants.EMPTY);
                    updateUserUsage.setResponseMessage("Input Usage Bytes Empty or Null.");
                    response.setUpdateUserUsage(updateUserUsage);
                    return response;
                }
                try {
                    log.debug("Call Radius Client To Update Usage For user: {}", userName);
                    GenericDataDTO genericDataDTO = radiusClientService.UpdateUerUsage(userName, SoapConstants.MVNOID, request.getUsageBytes());
                    log.debug("Integration Received Response In:{}MS From Radius For Update Usage For user: {},Response:{}", System.currentTimeMillis() - startTime, userName, genericDataDTO.getData());
                    if (Objects.nonNull(genericDataDTO.getData())) {
                        log.info("User usage update successful for user: {}", userName);
//                        if(genericDataDTO.getResponseCode() == 422){
//                            updateUserUsage.setResponeCode(422);
//                            updateUserUsage.setResponseMessage("Usage exceeds total quota.");
//                            response.setUpdateUserUsage(updateUserUsage);
//                            return response;
//                        }
                        if (genericDataDTO != null && genericDataDTO.getData() instanceof Map) {
                            updateUserUsage.setRequestId(requestId);
                            updateUserUsage.setResponeCode(SoapConstants.SUCCESS_CODE);
                            updateUserUsage.setResponseMessage(SoapConstants.SUCCESS);
                            response.setUpdateUserUsage(updateUserUsage);
                            log.info("SuccessFully Update User Usage: {}", userName);
                            return response;
                        }
                    } else {
                        log.warn("User does not exist or failed to fetch data for user: {}", userName);
                        updateUserUsage.setRequestId(requestId);
                        updateUserUsage.setResponeCode(SoapConstants.NotAcceptable);
                        updateUserUsage.setResponseMessage(SoapConstants.USER_NOT_EXIST_SPR);
                    }
                } catch (Exception e) {
                    log.error("Error occurred while updating user usage for {}: {}", userName, e.getMessage());
                    updateUserUsage.setRequestId(requestId);
                    updateUserUsage.setResponeCode(SoapConstants.INTERNAL_ERROR);
                    updateUserUsage.setResponseMessage(SoapConstants.ERROR_RADIUS_CLIENT + e.getMessage());
                }
            } else {
                log.warn("Username is null or empty");
                updateUserUsage.setRequestId(requestId);
                updateUserUsage.setResponeCode(SoapConstants.EMPTY);
                updateUserUsage.setResponseMessage(SoapConstants.INPUT_USERNAME_NULL_Empty);
            }
            response.setUpdateUserUsage(updateUserUsage);
            return response;
        } catch (Exception e) {
            log.error("Internal error occurred: {}", e.getMessage());
            updateUserUsage.setRequestId(requestId);
            updateUserUsage.setResponeCode(SoapConstants.INTERNAL_ERROR);
            updateUserUsage.setResponseMessage(SoapConstants.INTERNAL_ERROR + e.getMessage());
            response.setUpdateUserUsage(updateUserUsage);
            return response;
        }
    }
}
