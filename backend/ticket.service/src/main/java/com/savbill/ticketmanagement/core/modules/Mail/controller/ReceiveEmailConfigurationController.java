package com.savbill.ticketmanagement.core.modules.Mail.controller;

import com.savbill.ticketmanagement.core.controller.APIResponseController;
import com.savbill.ticketmanagement.core.exceptions.CustomValidationException;
import com.savbill.ticketmanagement.core.modules.Mail.model.ReceiveEmailConfigurationDTO;
import com.savbill.ticketmanagement.core.modules.Mail.service.ReceiveEmailConfigurationService;
import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
import com.savbill.ticketmanagement.core.modules.utils.APIConstants;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.RECEIVE_EMAIL_CONFIG)
public class ReceiveEmailConfigurationController {

    @Autowired
    ReceiveEmailConfigurationService receiveEmailConfigurationService;

    @Autowired
    private APIResponseController apiResponseController;


    @PostMapping( "/create")
    @ApiModelProperty("An api for create Receive Email Configuration")
    public ResponseEntity<?> saveReceiveEmailConfiguration(@RequestBody ReceiveEmailConfigurationDTO receiveEmailConfigurationDTO) {
        MDC.put("type", "crete");
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            ReceiveEmailConfigurationDTO returnReceiveEmailConfigurationDTO = receiveEmailConfigurationService.saveReceiveEmailConfig(receiveEmailConfigurationDTO);
            response.put("receiveEmailConfig", returnReceiveEmailConfigurationDTO);
            response.put("message", "Receive Email Config add successfully");
            RESP_CODE = APIConstants.SUCCESS;
            ApplicationLogger.logger.info("emailConfig   " + receiveEmailConfigurationDTO.getName() + ":  request: { From : {}}; Response : {{}}", "ReceiveEmailConfig", response, RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ApplicationLogger.logger.error("Unable to save receive email config " + receiveEmailConfigurationDTO.getName() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "ReceiveEmailConfig", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            ex.printStackTrace();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            ApplicationLogger.logger.error("Unable to save receive email config " + receiveEmailConfigurationDTO.getName() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "ReceiveEmailConfig", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }
}
