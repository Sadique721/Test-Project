package com.savbill.notification.controller;

import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.entity.SmsConfigMapping;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.SmsConfigMappingDto;
import com.savbill.notification.services.SmsConfigMappingService;
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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class SmsConfigMappingControllerTest {
    @InjectMocks
    SmsConfigMappingController smsConfigMappingController;
    @Mock
    SmsConfigMappingService smsConfigMappingService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    TokenDataExtractor tokenDataExtractor;
    @Mock
    ApiDataValidator apiDataValidator;


    @Test
    public void findAllSmsConfigMappingsTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        SmsConfigMapping smsConfigMapping=getsmsConfigMapping();
        List<SmsConfigMapping>  smsConfigMappingList=new ArrayList<>();
        smsConfigMappingList.add(smsConfigMapping);
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfigMappingList",smsConfigMappingList);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigMappingService.findAllSmsConfigMapping(1L)).thenReturn(smsConfigMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigMappingController.findAllSmsConfigMappings(1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    public void findSmsConfigMappingBySmsConfigIdTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        SmsConfigMapping smsConfigMapping=getsmsConfigMapping();
        List<SmsConfigMapping>  smsConfigMappingList=new ArrayList<>();
        smsConfigMappingList.add(smsConfigMapping);
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfigMappingList",smsConfigMappingList);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigMappingService.findSmsConfigMappingBySmsConfigId(1L,1L)).thenReturn(smsConfigMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigMappingController.findSmsConfigMappingBySmsConfigId(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void addSmsConfigMappingTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        SmsConfigMapping smsConfigMapping=getsmsConfigMapping();
        List<SmsConfigMapping>  smsConfigMappingList=new ArrayList<>();
        smsConfigMappingList.add(smsConfigMapping);
        SmsConfigMappingDto smsConfigMappingDto=getsmsConfigMappingDto();
        List<SmsConfigMappingDto> smsConfigMappingDtoList=new ArrayList<>();
        smsConfigMappingDtoList.add(smsConfigMappingDto);
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfigMappingList",smsConfigMappingList);
        response.put(NotificationConstants.MESSAGE, "Sms config parameters has been added successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigMappingService.saveSmsConfigMapping(smsConfigMappingDtoList,1L)).thenReturn(smsConfigMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigMappingController.addSmsConfigMapping(smsConfigMappingDtoList,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void updateSmsConfigMappingTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;

        SmsConfigMapping smsConfigMapping=getsmsConfigMapping();
        List<SmsConfigMapping>  smsConfigMappingList=new ArrayList<>();
        smsConfigMappingList.add(smsConfigMapping);
        SmsConfigMappingDto smsConfigMappingDto=getsmsConfigMappingDto();
        List<SmsConfigMappingDto> smsConfigMappingDtoList=new ArrayList<>();
        smsConfigMappingDtoList.add(smsConfigMappingDto);
        Map<String, Object> response = new HashMap<>();
        response.put("smsConfigMappingList",smsConfigMappingList);
        response.put(NotificationConstants.MESSAGE, "SMS config parameters has been updated successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(smsConfigMappingService.updateSmsConfigMapping(smsConfigMappingDtoList,1L,1L,request)).thenReturn(smsConfigMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigMappingController.updateSmsConfigMapping(smsConfigMappingDtoList,1L,request,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void deleteSmsConfigMappingTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "SMS config parameter has been deleted successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =smsConfigMappingController.deleteSmsConfigMapping(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    SmsConfigMapping getsmsConfigMapping(){
        SmsConfigMapping smsConfigMapping=new SmsConfigMapping();
        smsConfigMapping.setSmsConfigId(1L);
        smsConfigMapping.setSmsConfigMappingId(1L);
        smsConfigMapping.setMvnoId(1L);
        smsConfigMapping.setParameter("1111");
        smsConfigMapping.setValue("333");
        Timestamp timestamp =Timestamp.valueOf("2007-09-23 10:10:10.0");
        smsConfigMapping.setCreatedOn(timestamp);
        smsConfigMapping.setLastModifiedOn(timestamp);
        return smsConfigMapping;
    }

    SmsConfigMappingDto getsmsConfigMappingDto(){
        SmsConfigMappingDto smsConfigMappingDto=new SmsConfigMappingDto();
        smsConfigMappingDto.setSmsConfigId(1L);
        smsConfigMappingDto.setParameter("1212");
        smsConfigMappingDto.setValue("3333");
        return  smsConfigMappingDto;
    }
}
