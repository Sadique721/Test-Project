package com.savbill.notification.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.notification.utils.CommonConstants;
import com.savbill.notification.utils.NotificationConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Service Status", description = "REST API to check service status!!!!", tags = "Status")
@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class APIResponseController {
    //final Logger log = Logger.getLogger(APIResponseController.class);

    public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response) {
        try {
//			log.info(String.format("%s", new ObjectMapper().writeValueAsString(response)));
            response.put("timestamp",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (responseCode.equals(NotificationConstants.SUCCESS)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            } else if (responseCode.equals(NotificationConstants.FAIL)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(NotificationConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(CommonConstants.UNAUTHORIZED)) {

                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
            } else if (responseCode.equals(CommonConstants.FORBIDDEN)) {

                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            //System.out.println("Error while performing operation" +e);
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", NotificationConstants.INTERNAL_SERVER_ERROR);
            response.put(NotificationConstants.ERROR_TAG, e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void buildErrorMessageForResponse(Map<String, Object> response, Throwable e) {
        String errorMsg = "";
        if (e.getMessage().contains(NotificationConstants.BASIC_STRING_MSG)) {
            errorMsg = e.getMessage().replace(NotificationConstants.BASIC_STRING_MSG, "");
            response.put(NotificationConstants.VALIDATION_REASON, NotificationConstants.VALIDATION_REASON_BASIC_STRING_MESSAGE);
            response.put(NotificationConstants.ERROR_MESSAGE, errorMsg);
        } else if (e.getMessage().contains(NotificationConstants.BASIC_NUMERIC_MSG)) {
            errorMsg = e.getMessage().replace(NotificationConstants.BASIC_NUMERIC_MSG, "");
            response.put(NotificationConstants.VALIDATION_REASON, NotificationConstants.VALIDATION_REASON_BASIC_NUMERIC_MESSAGE);
            response.put(NotificationConstants.ERROR_MESSAGE, errorMsg);
        } else {
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
        }
    }

    //	@PostMapping("/welcome")
//	public String showWelcomePage() {
//		return "Welcome To 'SavbillNotification'" + "<br>" + "User is authenticated and successfully logged in." + "<br>"
//				+ "You can access api by providing proper and correct url.";
//	}
    @ApiOperation(value = "Used to check whether Notification service is up or not.")
    @GetMapping("/serviceStatus")
    public String checkServiceStatus() {
        try {
            return "{\"success\": true,\"message\": \"Notification Service is Up.\"}";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }




}
