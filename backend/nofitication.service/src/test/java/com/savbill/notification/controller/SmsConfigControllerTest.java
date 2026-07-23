package com.savbill.notification.controller;

import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.services.SmsConfigService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SmsConfigControllerTest {
    @InjectMocks
    SmsConfigController smsConfigController;
    @Mock
    SmsConfigService smsConfigService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    TokenDataExtractor tokenDataExtractor;
    @Mock
    ApiDataValidator apiDataValidator;

    @Test
    @Ignore
    public void findAllSmsConfigTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        SmsConfig smsConfig=getsmsConfig();
        List<SmsConfig> smsConfigList=new ArrayList<>();
        smsConfigList.add(smsConfig);
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfigList",smsConfigList);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigService.findAllSmsConfig(1L , null, null)).thenReturn(smsConfigList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigController.findAllSmsConfig(1L, null,null,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void addSmsConfigTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        SmsConfig smsConfig=getsmsConfig();
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfig",smsConfig);
        response.put(NotificationConstants.MESSAGE, "SMS Configuration has been added successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigService.addSmsConfig("www",1L,"admin",null,true, null)).thenReturn(smsConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigController.addSmsConfig("www",1L,true,"admin",null,null,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void updateSmsConfigTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        SmsConfig smsConfig=getsmsConfig();
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfig",smsConfig);
        HttpServletRequest request1=null;
        response.put(NotificationConstants.MESSAGE, "SMS Configuration has been updated successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigService.updateSmsConfig(smsConfig,1L,request1)).thenReturn(smsConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigController.updateSmsConfig(smsConfig,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void findSmsByIdTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        SmsConfig smsConfig=getsmsConfig();
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfig",smsConfig);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigService.findSmsConfigById(1L,1L)).thenReturn(smsConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigController.findSmsById(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    SmsConfig getsmsConfig(){
        SmsConfig smsConfig=new SmsConfig();
        smsConfig.setSmsConfigId(1L);
        smsConfig.setSmsUrl("www");
        smsConfig.setMvnoId(1L);
        smsConfig.setCreateDate(LocalDateTime.now());
        smsConfig.setLastModifiedDate(LocalDateTime.now());
        smsConfig.setCreatedBy("admin");
        smsConfig.setLastModifiedBy("admin");
        return smsConfig;

    }

}
