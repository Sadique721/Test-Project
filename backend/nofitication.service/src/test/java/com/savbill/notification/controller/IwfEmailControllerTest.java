package com.savbill.notification.controller;

import com.savbill.notification.helper.*;
import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.entity.Email;
import com.savbill.notification.entity.Event;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.*;
import com.savbill.notification.services.EmailService;
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
@Ignore
public class IwfEmailControllerTest {
    @InjectMocks
    EmailController emailController;
    @Mock
    EmailService emailService;
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
        response.put(NotificationConstants.MESSAGE, "Email has been Sent successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.sendEmail(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void sendEmailTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "Email has been Sent successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
        ResponseEntity<Map<String, Object>> output = emailController.sendEmail(1L,1L,request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    @Test
    @Ignore
    public void findAllEmailsTest() throws IOException {
        Email email=getemail();
        List<Email> emailList=new ArrayList<>();
        List<Long> buidList=new ArrayList<>();
        emailList.add(email);
        PageableResponse<Email> emailPageableResponse = new PageableResponse<Email>();
        PaginationDTO paginationDTO=getpaginationDTO();
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "createDate"));
        emailPageableResponse = emailPageableResponse.convert(new PageImpl(emailList, pageable,  emailList.size()));
        Map<String, Object> response = new HashMap<>();
        response.put("emailList", emailPageableResponse);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        //Mockito.when(emailService.findAllEmails(1L,"Active","pm@savbillnet.tech",1L,paginationDTO,buidList)).thenReturn(emailPageableResponse);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.findAllEmails(paginationDTO,1L,"Active","pm@savbillnet.tech",httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    @Ignore
    public void findAllEmailsTestException() throws IOException {
        Email email=getemail();
        List<Email> emailList=new ArrayList<>();
        emailList.add(email);
        List<Long> buidList=new ArrayList<>();
        PageableResponse<Email>  emailPageableResponse = new PageableResponse<Email>();
        PaginationDTO paginationDTO=getpaginationDTO();
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "createDate"));
        emailPageableResponse = emailPageableResponse.convert(new PageImpl(emailList, pageable,  emailList.size()));
        Map<String, Object> response = new HashMap<>();
        response.put("emailList", emailPageableResponse);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        //Mockito.when(emailService.findAllEmails(1L,"Active","pm@savbillnet.tech",1L,paginationDTO,buidList)).thenReturn(emailPageableResponse);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
        ResponseEntity<Map<String, Object>> output = emailController.findAllEmails(paginationDTO,1L,"Active","pm@savbillnet.tech",httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),400);
    }
    @Test
    @Ignore
    public void findEmailByIdTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Email email=getemail();
        Map<String, Object> response = new HashMap<>();
        response.put("email",email);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(emailService.findEmailById(1L,1L,false)).thenReturn(email);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.findEmailById(1L,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    @Ignore
    public void findEmailByIdTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Email email=getemail();
        Map<String, Object> response = new HashMap<>();
        response.put("email",email);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(emailService.findEmailById(1L,1L,false)).thenReturn(email);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
        ResponseEntity<Map<String, Object>> output = emailController.findEmailById(1L,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    @Test
    @Ignore
    public void findEmailBySourceNameTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Email email=getemail();
        List<Email> emailList=new ArrayList<>();
        List<EmailDataDTO> emailDataDTOListList=new ArrayList<>();
        emailList.add(email);
        Map<String, Object> response = new HashMap<>();
        response.put("emailList",emailList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(emailService.findEmailBySourceName(1L,"Active","pm@savbillnet.tech",1L)).thenReturn(emailDataDTOListList);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.findEmailBySourceName(1L,"Active","pm@savbillnet.tech",1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void findEmailBySourceNameTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Email email=getemail();
        List<Email> emailList=new ArrayList<>();
        emailList.add(email);
        List<EmailDataDTO> emailDataList=new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        response.put("emailList",emailList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(emailService.findEmailBySourceName(1L,"Active","pm@savbillnet.tech",1L)).thenReturn(emailDataList);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.findEmailBySourceName(1L,"Active","pm@savbillnet.tech",1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void addEmailTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Email email=getemail();
        EmailDto emailDto=getemailDto();
        Map<String, Object> response = new HashMap<>();
        response.put("email",email);
        response.put(NotificationConstants.MESSAGE, "Email has been added successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(emailService.saveEmail(emailDto,1L)).thenReturn(email);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.addEmail(emailDto,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void addEmailTestException() throws AuthException, CustomException, IOException {
        Email email=getemail();
        EmailDto emailDto=getemailDto();
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(emailService.saveEmail(emailDto,1L)).thenReturn(email);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenThrow(NullPointerException.class);
         Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
        ResponseEntity<Map<String, Object>> output = emailController.addEmail(emailDto,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    @Test
    @Ignore
    public void updateEmailTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Email email=getemail();
        UpdateEmailDto updateEmailDto=getUpdateEmailDto();
        Map<String, Object> response = new HashMap<>();
        response.put("email",email);
        HttpServletRequest request = null;
        response.put(NotificationConstants.MESSAGE, "Email has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(emailService.updateEmail(updateEmailDto,1L, 0L,request)).thenReturn(email);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.updateEmail(updateEmailDto,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void updateEmailTestException() throws AuthException, CustomException, IOException {
        Email email=getemail();
        UpdateEmailDto updateEmailDto=getUpdateEmailDto();
        Map<String, Object> response = new HashMap<>();
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
        HttpServletRequest httpServletRequest1=null;
          ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(emailService.updateEmail(updateEmailDto,1L, 0L,httpServletRequest1)).thenReturn(email);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.updateEmail(updateEmailDto,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    @Test
    @Ignore
    public void deleteEmailTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "Email has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.deleteEmail(1L,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void deleteEmailTestException() throws AuthException, CustomException, IOException {
        Map<String, Object> response = new HashMap<>();
        NullPointerException exception=new NullPointerException();
        response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
         ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = emailController.deleteEmail(1L,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    Email getemail(){
        Email email=new Email();
        email.setEmailId(1L);
        email.setEmailAddress("pm@savbillNet.tech");
        email.setStatus("Active");
        email.setMessage("Success");
        email.setMvnoId(1L);
        email.setSourceName("aaa");
        email.setCreateDate(LocalDateTime.now());
        email.setLastModifiedDate(LocalDateTime.now());
        email.setCreatedBy("admin");
        email.setLastModifiedBy("admin");
        email.setEmailConfigId(1L);
        Event event=new Event();
        email.setEvent(event);
        LocalDateTime timestamp =LocalDateTime.now();
        email.setDate(timestamp);
        return email;
    }
    EmailDto getemailDto(){
        EmailDto emailDto=new EmailDto();
        emailDto.setEmailAddress("pm@savbillNet.tech");
        emailDto.setStatus("Active");
        emailDto.setMessage("Success");
        emailDto.setSourceName("aaa");
        emailDto.setCreatedBy("admin");
        return emailDto;
    }
     UpdateEmailDto getUpdateEmailDto(){
        UpdateEmailDto emailDto=new UpdateEmailDto();
        emailDto.setEmailAddress("pm@savbillNet.tech");
        emailDto.setStatus("Active");
        emailDto.setMessage("Success");
        emailDto.setSourceName("aaa");
        emailDto.setLastModifiedBy("admin");
        emailDto.setEmailId(1L);
        return emailDto;
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
