package com.savbill.notification.controller;

import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.entity.EmailConfig;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.EmailConfigDto;
import com.savbill.notification.helper.PasswordDto;
import com.savbill.notification.helper.UpdateEmailConfigDto;
import com.savbill.notification.services.EmailConfigService;
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
public class IwfEmailConfigControllerTest1 {
    @InjectMocks
    EmailConfigController emailConfigController;
    @Mock
    EmailConfigService emailConfigService;
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
    public void findAllEmailConfigTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        EmailConfig emailConfig=getemailConfig();
        List<EmailConfig> emailConfigList = new ArrayList<>();
        emailConfigList.add(emailConfig);
        Map<String, Object> response = new HashMap<>();
        response.put("emailConfigList",emailConfigList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(emailConfigService.findAllEmailConfig(1L,null, null)).thenReturn(emailConfigList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailConfigController.findAllEmailConfig(1L,null, null,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void findAllEmailConfigTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        EmailConfig emailConfig=getemailConfig();
        List<EmailConfig> emailConfigList = new ArrayList<>();
        emailConfigList.add(emailConfig);
        Map<String, Object> response = new HashMap<>();
        response.put("emailConfigList",emailConfigList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(emailConfigService.findAllEmailConfig(1L,null, null)).thenReturn(emailConfigList);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.SUCCESS, response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE, exception.getMessage());
        ResponseEntity<Map<String, Object>> output = emailConfigController.findAllEmailConfig(1L,null, null,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }

    @Test
    @Ignore
    public void addEmailConfigTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        EmailConfig emailConfig=getemailConfig();
        EmailConfigDto emailConfigDto=getemailConfigDto();
        Map<String, Object> response = new HashMap<>();
        response.put("emailConfig",emailConfig);
        response.put(NotificationConstants.MESSAGE, "Email configuration has been added successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(emailConfigService.addEmailConfig(emailConfigDto,1L,null)).thenReturn(emailConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailConfigController.addEmailConfig(emailConfigDto,1L,null,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void addEmailConfigTestExceptionTest() throws AuthException, CustomException, IOException {
        EmailConfigDto emailConfigDto=getemailConfigDto();
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
        ResponseEntity<Map<String, Object>> output = emailConfigController.addEmailConfig(emailConfigDto,1L,null,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    @Test
    @Ignore
    public void updateEmailConfigTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        EmailConfig emailConfig=getemailConfig();
        UpdateEmailConfigDto updateEmailConfigDto=getUpdateEmailConfigDto();
        Map<String, Object> response = new HashMap<>();
        response.put("emailConfig",emailConfig);
        response.put(NotificationConstants.MESSAGE, "Email Configuration has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(emailConfigService.updateEmailConfig(updateEmailConfigDto,1L,null)).thenReturn(emailConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailConfigController.updateEmailConfig(updateEmailConfigDto,1L,null,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void updateEmailConfigTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        EmailConfig emailConfig=getemailConfig();
        UpdateEmailConfigDto updateEmailConfigDto=getUpdateEmailConfigDto();
        Map<String, Object> response = new HashMap<>();
        response.put("emailConfig",emailConfig);
        response.put(NotificationConstants.MESSAGE, "Email Configuration has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(emailConfigService.updateEmailConfig(updateEmailConfigDto,1L,null)).thenReturn(emailConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailConfigController.updateEmailConfig(updateEmailConfigDto,1L,null,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void changePasswordTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        PasswordDto passwordDto=getPasswordDto();
        EmailConfig emailConfig=getemailConfig();
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "Password has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailConfigController.changePassword(passwordDto,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void changePasswordTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        PasswordDto passwordDto=getPasswordDto();
        EmailConfig emailConfig=getemailConfig();
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "Password has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailConfigController.changePassword(passwordDto,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    @Test
    @Ignore
    public void findEmailByIdTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        EmailConfig emailConfig=getemailConfig();
        Map<String, Object> response = new HashMap<>();
        response.put("emailConfig",emailConfig);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(emailConfigService.findEmailConfigById(1L,1L)).thenReturn(emailConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailConfigController.findEmailById(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void findEmailByIdTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        EmailConfig emailConfig=getemailConfig();
        Map<String, Object> response = new HashMap<>();
        response.put("emailConfig",emailConfig);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(emailConfigService.findEmailConfigById(1L,1L)).thenReturn(emailConfig);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
        ResponseEntity<Map<String, Object>> output = emailConfigController.findEmailById(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }


    EmailConfig getemailConfig(){
        EmailConfig emailConfig=new EmailConfig();
        emailConfig.setEmailConfigId(1L);
        emailConfig.setAuthType("SUCCESS");
        emailConfig.setHostServer("212");
        emailConfig.setPassword("1234");
        emailConfig.setMvnoId(1L);
        emailConfig.setPort("221122");
        emailConfig.setUserName("admin");
        emailConfig.setCreateDate(LocalDateTime.now());
        emailConfig.setLastModifiedDate(LocalDateTime.now());
        emailConfig.setCreatedBy("admin");
        emailConfig.setLastModifiedBy("admin");
        emailConfig.setSmtpAuth(true);
        return emailConfig;
    }
    EmailConfigDto getemailConfigDto(){
        EmailConfigDto emailConfigDto=new EmailConfigDto();
        emailConfigDto.setAuthType("SUCCESS");
        emailConfigDto.setHostServer("212");
        emailConfigDto.setPassword("1234");
        emailConfigDto.setPort("221122");
        emailConfigDto.setUserName("admin");
        emailConfigDto.setCreatedBy("admin");
        emailConfigDto.setSmtpAuth(true);
        return emailConfigDto;
    }

     PasswordDto getPasswordDto(){
         PasswordDto  passwordDto=new PasswordDto();
         passwordDto.setNewPassword("1212");
         passwordDto.setConfirmNewPassword("1212");
         passwordDto.setLastModifiedBy("admin");
         passwordDto.setUserName("admin");
        return passwordDto;
    }

     UpdateEmailConfigDto getUpdateEmailConfigDto(){
        UpdateEmailConfigDto updateEmailConfigDto=new UpdateEmailConfigDto();
        updateEmailConfigDto.setAuthType("SUCCESS");
        updateEmailConfigDto.setHostServer("212");
        updateEmailConfigDto.setPassword("1234");
        updateEmailConfigDto.setPort("221122");
        updateEmailConfigDto.setUserName("admin");
        updateEmailConfigDto.setSmtpAuth(true);
        return updateEmailConfigDto;
    }

}
