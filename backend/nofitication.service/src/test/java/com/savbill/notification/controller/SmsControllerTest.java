package com.savbill.notification.controller;

import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.entity.Sms;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.services.SmsService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class SmsControllerTest {
    @InjectMocks
    SmsController smsController;
    @Mock
    SmsService smsService;
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
    public void sendEmailTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "SMS has been Sent successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = smsController.sendSms(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void findAllSmssTest() throws AuthException, CustomException, IOException {
        Sms sms=getsms();
        List<Sms> smsList=new ArrayList<>();
        List<Long> buIdlist=new ArrayList<>();
        buIdlist.add(0L);
        smsList.add(sms);
        PageableResponse<Sms> smsPageableResponse = new PageableResponse<Sms>();
        PaginationDTO paginationDTO=getpaginationDTO();
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "createDate"));
        smsPageableResponse = smsPageableResponse.convert(new PageImpl(smsList, pageable,  smsList.size()));
        Map<String, Object> response = new HashMap<>();
        response.put("smsList", smsPageableResponse);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
//        Mockito.when(smsService.findAllSmss(1L,"Active","7777777777",1L,paginationDTO, buIdlist)).thenReturn(smsPageableResponse);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = smsController.findAllSmss(paginationDTO,1L,"Active","7777777777",request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    @Ignore
    public void findSmsByIdTest() throws AuthException, CustomException, IOException {
        Sms sms=getsms();
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put("sms",sms);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(smsService.findSmsById(1L,1L,false)).thenReturn(sms);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = smsController.findSmsById(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void findSmsBySourceNameTest() throws AuthException, CustomException, IOException {
        Sms sms=getsms();
        List<Sms> smsList=new ArrayList<>();
        List<SmsDataDTO> smsDataDTOList = new ArrayList<>();
        smsList.add(sms);
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put("smsList",smsList);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(smsService.findSmsBySourceName(1L,"active","7777777777",1L)).thenReturn(smsDataDTOList);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = smsController.findSmsBySourceName(1L,"active","7777777777",1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void addSmsTest() throws AuthException, CustomException, IOException {
        Sms sms=getsms();
        SmsDto smsDto=getsmsDto();
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put("sms",sms);
        response.put(NotificationConstants.MESSAGE, "SMS has been added successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(smsService.saveSms(smsDto,1L, 0L)).thenReturn(sms);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = smsController.addSms(smsDto,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void updateSmsTest() throws AuthException, CustomException, IOException {
        Sms sms=getsms();
        UpdateSmsDto updateSmsDto=getUpdateSmsDto();
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put("sms",sms);
        response.put(NotificationConstants.MESSAGE, "SMS has been updated successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(smsService.updateSms(updateSmsDto,1L,0L,request)).thenReturn(sms);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = smsController.updateSms(updateSmsDto,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void deleteSmsTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "SMS has been deleted successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = smsController.deleteSms(1L,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    Sms getsms(){
       Sms sms=new Sms();
       sms.setSmsId(1L);
       sms.setCountryCode("+91");
       sms.setStatus("Active");
       sms.setMessage("Success");
       sms.setMobileNo("7777777777");
       sms.setCreateDate(LocalDateTime.now());
       sms.setLastModifiedDate(LocalDateTime.now());
       sms.setCreatedBy("admin");
       sms.setLastModifiedBy("admin");
       sms.setMvnoId(1L);
       sms.setSmsConfigId(1L);
       sms.setSmsConfigId(1L);
       sms.setSourceName("aaa");
//        Event event=new Event();
//        sms.setEvent(event);
        LocalDateTime timestamp =LocalDateTime.now();
        sms.setDate(timestamp);
        return sms;
    }

    SmsDto getsmsDto(){
        SmsDto smsDto=new SmsDto();
         smsDto.setCountryCode("+91");
        smsDto.setStatus("Active");
        smsDto.setMessage("Success");
        smsDto.setMobileNo("7777777777");
         smsDto.setCreatedBy("admin");
         smsDto.setSourceName("aaa");
         return smsDto;
    }
     UpdateSmsDto getUpdateSmsDto(){
         UpdateSmsDto  updateSmsDto=new UpdateSmsDto();
         updateSmsDto.setCountryCode("+91");
         updateSmsDto.setStatus("Active");
         updateSmsDto.setMessage("Success");
         updateSmsDto.setMobileNo("7777777777");
         updateSmsDto.setLastModifiedBy("admin");
         updateSmsDto.setSourceName("aaa");
        return updateSmsDto;
    }
    PaginationDTO getpaginationDTO(){
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setSize(1);
        paginationDTO.setPage(1);
        paginationDTO.setFromDate("2018/8/2");
        paginationDTO.setToDate("2020/8/2");
        return paginationDTO;
    }


}
