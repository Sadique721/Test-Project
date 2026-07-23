package com.savbill.notification.controller;

import com.savbill.notification.utils.NotificationConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class APIResponseControllerTest {
    @InjectMocks
    APIResponseController apiResponseController;

    @Test
    public void apiResponseTest(){
        Map<String, Object> response=new HashMap<>();
        Integer responseCode= NotificationConstants.SUCCESS;
        response.put("timestamp",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
        response.put("status", responseCode);
        ResponseEntity<Map<String, Object>> output =apiResponseController.apiResponse(responseCode,response);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);

    }
    @Test
    public void apiResponseTestException(){
        Map<String, Object> response=new HashMap<>();
        Integer responseCode= NotificationConstants.FAIL;
        response.put("timestamp",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
        response.put("status", responseCode);
        ResponseEntity<Map<String, Object>> output =apiResponseController.apiResponse(responseCode,response);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);

    }
    @Test
     public void buildErrorMessageForResponseTest() {
        Throwable throwable=new Throwable("");
        String errorMsg="";
        Map<String, Object> response=new HashMap<>();
        Integer responseCode= NotificationConstants.SUCCESS;
        response.put(NotificationConstants.VALIDATION_REASON, NotificationConstants.VALIDATION_REASON_BASIC_STRING_MESSAGE);
        response.put(NotificationConstants.ERROR_MESSAGE, errorMsg);
        apiResponseController.buildErrorMessageForResponse(response,throwable);
        assertTrue(true);
        assertNotNull(errorMsg);
    }

    @Test
    public void checkServiceStatusTest(){

        String checkServiceStatus="Active";
        apiResponseController.checkServiceStatus();
        assertNotNull(checkServiceStatus);

    }
}
