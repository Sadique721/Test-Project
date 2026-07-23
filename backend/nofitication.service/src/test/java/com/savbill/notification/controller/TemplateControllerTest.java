package com.savbill.notification.controller;

import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.Template;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.TemplateDto;
import com.savbill.notification.services.TemplateService;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@Ignore
public class TemplateControllerTest {
    @InjectMocks
    TemplateController templateController;
    @Mock
    TemplateService templateService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    TokenDataExtractor tokenDataExtractor;
    @Mock
    ApiDataValidator apiDataValidator;

    @Test
    public void findAllTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Template template=gettemplate();
        List<Template> templateList=new ArrayList<>();
        templateList.add(template);
        Map<String, Object> response = new HashMap<>();
        response.put("templateList",templateList);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(templateService.findAll()).thenReturn(templateList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.findAll(request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    public void findAllTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Template template=gettemplate();
        List<Template> templateList=new ArrayList<>();
        templateList.add(template);
        Map<String, Object> response = new HashMap<>();
        response.put("templateList",templateList);
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(templateService.findAll()).thenReturn(templateList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.findAll(request);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }


    @Test
    public void saveTemplateTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Template template=gettemplate();
        TemplateDto templateDto=gettemplateDto();
        Map<String, Object> response = new HashMap<>();
        response.put("template",template);
        response.put(NotificationConstants.MESSAGE, "Template has been added successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(templateService.saveTemplate(templateDto)).thenReturn(template);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.saveTemplate(templateDto,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    public void saveTemplateTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Template template=gettemplate();
        TemplateDto templateDto=gettemplateDto();
        Map<String, Object> response = new HashMap<>();
        response.put("template",template);
        response.put(NotificationConstants.MESSAGE, "Template has been added successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(templateService.saveTemplate(templateDto)).thenReturn(template);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.saveTemplate(templateDto,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }




    @Test
    public void updateTemplateTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Template template=gettemplate();
        List<Template> templateList=new ArrayList<>();
        templateList.add(template);
        TemplateDto templateDto=gettemplateDto();
        List<TemplateDto> templateDtoList=new ArrayList<>();
        templateDtoList.add(templateDto);
        Map<String, Object> response = new HashMap<>();
        response.put("template",templateDtoList);
        response.put(NotificationConstants.MESSAGE, "Template has been updated successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(templateService.udpateTemplate(templateDtoList,httpServletRequest)).thenReturn(templateDtoList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.updateTemplate(templateDtoList,httpServletRequest,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void updateTemplateTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Template template=gettemplate();
        List<Template> templateList=new ArrayList<>();
        templateList.add(template);
        TemplateDto templateDto=gettemplateDto();
        List<TemplateDto> templateDtoList=new ArrayList<>();
        templateDtoList.add(templateDto);
        Map<String, Object> response = new HashMap<>();
        response.put("template",templateDtoList);
        response.put(NotificationConstants.MESSAGE, "Template has been updated successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(templateService.udpateTemplate(templateDtoList,httpServletRequest)).thenReturn(templateDtoList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.updateTemplate(templateDtoList,httpServletRequest,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    @Test
    public void deleteTemplateTest() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "Template has been deleted successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.deleteTemplate(1L,request,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void deleteTemplateTestException() throws AuthException, CustomException, IOException {
        Long userMvnoId=11L;
        Map<String, Object> response = new HashMap<>();
        response.put(NotificationConstants.MESSAGE, "Template has been deleted successfully.");
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.setRequestURI("/foo");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
        Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =templateController.deleteTemplate(1L,request,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 400);
    }
    Template gettemplate(){
        Template template=new Template();
        template.setTemplateId(1L);
        template.setTemplateName("Login");
        Timestamp timestamp =Timestamp.valueOf("2007-09-23 10:10:10.0");
        template.setCreateDate(timestamp);
        template.setLastModificationDate(timestamp);
        template.setStatus("Active");
        template.setAppendUrl("WWW");
        template.setEmailTemplateData("wwww");
        template.setEmailEventConfigured(true);
        Event event=new Event();
        template.setEvent(event);
        template.setSmsEventConfigured(true);
        template.setSmsTemplateData("zzzz");
        template.setAppendUrl("WWWW");
        return template;
    }
    TemplateDto gettemplateDto(){
        TemplateDto templateDto=new TemplateDto();
        templateDto.setTemplateName("Login");
        Timestamp timestamp =Timestamp.valueOf("2007-09-23 10:10:10.0");
        templateDto.setStatus("Active");
        templateDto.setAppendUrl("WWW");
        templateDto.setEmailTemplateData("wwww");
        templateDto.setEmailEventConfigured(true);
        Event event=new Event();
        templateDto.setSmsEventConfigured(true);
        templateDto.setSmsTemplateData("zzzz");
        templateDto.setAppendUrl("WWWW");
        return templateDto;
    }

}
